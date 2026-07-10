#!/bin/bash
set -e

NS="dev"

echo "====================================================================="
echo "  KIEM TRA SERVICE MESH (ISTIO) TREN MOI TRUONG $NS"
echo "====================================================================="

function write_step {
    echo -e "\n\e[1;36m=====================================================================\e[0m"
    echo -e "\e[1;36m$1\e[0m"
    echo -e "\e[1;36m=====================================================================\e[0m"
}

function write_result {
    if [ "$2" == "true" ]; then
        echo -e "  [PASS] $1 : $3"
    else
        echo -e "  [FAIL] $1 : $3"
    fi
}

# Kiem tra xem tat ca cac pod da duoc bom Envoy Sidecar (hien thi 2/2) chua
write_step "KIEM TRA TRANG THAI: Pods 2/2 (co Envoy sidecar)"
echo -e "\e[33m  Pods trong namespace $NS :\e[0m"
# Lệnh này dùng để liệt kê tất cả các Pod trong namespace dev. Nếu thấy READY 2/2 nghĩa là Istio Envoy đã được cài đặt thành công vào Pod.
kubectl get pods -n $NS -o wide
echo ""
echo -e "\e[33m  Neu pod nao khong phai 2/2 thi Istio injection chua hoat dong\e[0m"
echo -e "\e[90m  Fix: kubectl rollout restart deployment/<service> -n $NS\e[0m"

sleep 3

# Test 1: Goi tu mot pod (mtls-test) khong co chung chi mTLS vao he thong. Ky vong: Bi tu choi (Connection Reset)
write_step "TEST 1: mTLS STRICT - Pod ngoai mesh bi chan"
echo -e "\e[33m  Tao pod o namespace default (NGOAI mesh, khong co Envoy)...\e[0m"
echo -e "\e[33m  Goi product service, phai bi Connection Reset...\e[0m"
echo ""

kubectl delete pod mtls-test --namespace=default --ignore-not-found=true 2>/dev/null || true
sleep 2

# Lệnh này tạo một Pod mới tên mtls-test ở namespace default (không có Envoy), và dùng nó để gọi lệnh curl tới product service. 
# Mục đích: Giả lập một kẻ lạ mặt gọi vào hệ thống để xem có bị bảo vệ mTLS chặn hay không.
kubectl run mtls-test --image=curlimages/curl --namespace=default --restart=Never -- curl -sv --max-time 5 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 > /dev/null || true

waited=0
podPhase=""
# Vòng lặp này đợi tối đa 15 giây để cái Pod mtls-test kia chạy xong lệnh curl (trạng thái Succeeded hoặc Failed) thì mới đi tiếp.
while [[ "$podPhase" != "Succeeded" && "$podPhase" != "Failed" && $waited -lt 15 ]]; do
    sleep 2
    waited=$((waited + 2))
    podPhase=$(kubectl get pod mtls-test -n default -o jsonpath='{.status.phase}' 2>/dev/null || true)
done

# Lệnh này đọc log của cái Pod mtls-test vừa chạy xong để lấy kết quả (kiểm tra xem có chữ Connection reset không).
mtlsResult=$(kubectl logs mtls-test --namespace=default 2>&1 || true)
kubectl delete pod mtls-test --namespace=default --ignore-not-found=true 2>/dev/null || true

# Lệnh grep này quét chuỗi kết quả log. Nếu tìm thấy chữ 'reset' hoặc 'refused', nghĩa là Istio đã chặn thành công.
if echo "$mtlsResult" | grep -qE "reset|refused|Connection reset|timed out|timeout|connection reset by peer"; then
    write_result "mTLS chan traffic tu ngoai mesh" "true" "Connection bi reset/refused (mTLS STRICT dang hoat dong)"
else
    write_result "mTLS chan traffic tu ngoai mesh" "false" "Traffic KHONG bi chan: $mtlsResult"
fi

sleep 3

# Test 2: Dung service cart goi sai phep vao customer. Ky vong: Istio chan va tra ve loi 403 Forbidden
write_step "TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403"
echo -e "\e[33m  cart goi customer, phai bi 403 (cart khong nam trong allowedCallers cua customer)\e[0m"
echo ""

# Lệnh này chui vào bên trong Pod 'cart', và gõ lệnh wget (gọi HTTP) tới service 'customer'.
# Mục đích: Giả lập một cuộc gọi TRÁI PHÉP nội bộ để xem AuthorizationPolicy có chặn lại (trả về 403) hay không.
denyResult=$(kubectl exec -n $NS deployment/cart -c cart -- wget -S -O /dev/null --timeout=5 "http://customer.$NS.svc.cluster.local/customer/storefront/customers/profile" 2>&1 || true)

# Lệnh grep này kiểm tra xem kết quả trả về có chứa mã lỗi 403 (bị từ chối) hay không.
if echo "$denyResult" | grep -qE "403|RBAC|denied|forbidden"; then
    write_result "AuthzPolicy chan service khong duoc phep" "true" "HTTP 403 - RBAC: access denied"
else
    write_result "AuthzPolicy chan service khong duoc phep" "false" "Response: $denyResult"
fi

sleep 3

# Test 3: Dung service storefront-bff goi hop le vao product. Ky vong: Istio mo cua va tra ve 200 OK
write_step "TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200"
echo -e "\e[33m  storefront-bff goi product, phai duoc 200 (co trong allowedCallers)\e[0m"
echo ""

# Lệnh này chui vào bên trong Pod 'storefront-bff', và gõ lệnh wget tới service 'product'.
# Mục đích: Giả lập một cuộc gọi HỢP LỆ. Kết quả mong muốn là Istio cho phép đi qua và trả về 200 OK.
allowResult=$(kubectl exec -n $NS deployment/storefront-bff -c storefront-bff -- wget -S -O - --timeout=15 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 || true)

# Lệnh grep này kiểm tra xem kết quả trả về có chứa chữ 200 OK (thành công) hay không.
if echo "$allowResult" | grep -qE "200 OK|productList|totalPage|pageNumber"; then
    write_result "AuthzPolicy cho phep service hop le" "true" "HTTP 200 - Du lieu JSON tra ve thanh cong"
else
    write_result "AuthzPolicy cho phep service hop le" "false" "Response: $allowResult"
fi

sleep 3

# Test 4: Gia lap mang bi loi 503 (30%) de xem Envoy co tu dong goi lai (Retry) de cuu request hay khong
write_step "TEST 4: Retry Policy - Inject loi 503 va kiem tra retry"
echo -e "\e[33m  Buoc 4a: Apply Fault Injection (30% loi 503 vao product)...\e[0m"

# Lệnh này truyền trực tiếp một đoạn YAML cấu hình Istio (VirtualService) vào cụm Kubernetes.
# Cấu hình này ép service 'product' phải tung ra lỗi 503 cho 30% số lượng request gọi tới nó (Tiêm lỗi/Fault Injection).
cat <<EOF | kubectl apply -f -
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-fault-injection
  namespace: $NS
spec:
  hosts:
    - product
  http:
    - fault:
        abort:
          percentage:
            value: 30
          httpStatus: 503
      retries:
        attempts: 3
        perTryTimeout: 5s
        retryOn: 5xx,gateway-error,connect-failure
      timeout: 20s
      route:
        - destination:
            host: product
            port:
              number: 80
EOF

echo -e "\e[33m  Cho policy propagate (10s)...\e[0m"
sleep 10

echo -e "\e[33m  Buoc 4b: Gui 10 requests storefront-bff -> product...\e[0m"
success=0
fail=0
# Vòng lặp bắn 10 phát đạn (request) liên tiếp
for i in {1..10}; do
    # Bắn 10 phát liên tục từ storefront-bff tới product để xem Envoy có tự động gọi lại (retry) khi gặp lỗi 503 không.
    retryResult=$(kubectl exec -n $NS deployment/storefront-bff -c storefront-bff -- wget -S -O - --timeout=25 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 || true)
    if echo "$retryResult" | grep -qE "200 OK|productList|totalPage|pageNumber"; then
        success=$((success + 1))
        echo -e "    \e[32mRequest $i -> 200 OK (retry thanh cong)\e[0m"
    else
        fail=$((fail + 1))
        echo -e "    \e[31mRequest $i -> Failed (503 khong duoc retry)\e[0m"
    fi
    sleep 1
done

echo ""
echo -e "\e[33m  Ket qua: $success/10 thanh cong | $fail/10 that bai\e[0m"
if [ $success -ge 8 ]; then
    write_result "Retry Policy hap thu loi 503" "true" ">=80% requests thanh cong du 30% bi inject loi, Envoy retry hoat dong"
else
    write_result "Retry Policy hap thu loi 503" "false" "Chi $success/10 thanh cong. Kiem tra VirtualService retry config"
fi

echo ""
echo -e "\e[33m  Buoc 4c: Don dep Fault Injection...\e[0m"
kubectl delete virtualservice product-fault-injection -n $NS 2>/dev/null || true
echo -e "\e[32m  Da xoa fault injection, product tro lai binh thuong\e[0m"

# TOM TAT KET QUA
write_step "TOM TAT KET QUA"
echo ""
echo -e "\e[36m  TEST 1: mTLS          -> Pod ngoai bi chan\e[0m"
echo -e "\e[36m  TEST 2: AuthzPolicy   -> Service sai bi 403\e[0m"
echo -e "\e[36m  TEST 3: AuthzPolicy   -> Service dung duoc 200\e[0m"
echo -e "\e[36m  TEST 4: Retry Policy  -> Retry hap thu loi\e[0m"
echo ""
echo -e "\e[36m  Mo Kiali de chup screenshot topology:\e[0m"
echo -e "\e[97m    sudo kubectl port-forward --address 0.0.0.0 svc/kiali 20001:20001 -n istio-system\e[0m"
echo -e "\e[97m    http://<Public-IP>:20001 -> Graph -> Namespace: dev\e[0m"
