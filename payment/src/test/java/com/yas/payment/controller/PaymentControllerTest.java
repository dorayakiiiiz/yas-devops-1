package com.yas.payment.controller;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private InitPaymentRequestVm initPaymentRequestVm;
    private InitPaymentResponseVm initPaymentResponseVm;
    private CapturePaymentRequestVm capturePaymentRequestVm;
    private CapturePaymentResponseVm capturePaymentResponseVm;

    @BeforeEach
    void setUp() {
        // Khởi tạo dữ liệu mẫu cho InitPayment
        initPaymentRequestVm = InitPaymentRequestVm.builder()
                .orderId("ORDER-123")
                .amount(BigDecimal.valueOf(1000000))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .returnUrl("https://example.com/return")
                .cancelUrl("https://example.com/cancel")
                .build();

        initPaymentResponseVm = InitPaymentResponseVm.builder()
                .paymentId("PAY-123")
                .status("INITIATED")
                .redirectUrl("https://payment.example.com/PAY-123")
                .build();

        // Khởi tạo dữ liệu mẫu cho CapturePayment
        capturePaymentRequestVm = CapturePaymentRequestVm.builder()
                .paymentId("PAY-123")
                .captureAmount(BigDecimal.valueOf(1000000))
                .build();

        capturePaymentResponseVm = CapturePaymentResponseVm.builder()
                .orderId(123L)
                .checkoutId("CHECKOUT-123")
                .amount(BigDecimal.valueOf(1000000))
                .paymentFee(BigDecimal.valueOf(10000))
                .gatewayTransactionId("TXN-123456")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();
    }

    @Test
    void testInitPayment_Success() {
        // Given
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initPaymentResponseVm);

        // When
        InitPaymentResponseVm response = paymentController.initPayment(initPaymentRequestVm);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.paymentId()).isEqualTo("PAY-123");
        assertThat(response.redirectUrl()).isEqualTo("https://payment.example.com/PAY-123");
        assertThat(response.status()).isEqualTo("INITIATED");

        verify(paymentService, times(1)).initPayment(initPaymentRequestVm);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    void testInitPayment_WithInvalidRequest() {
        // Given
        InitPaymentRequestVm invalidRequest = InitPaymentRequestVm.builder()
                .orderId(null)
                .amount(BigDecimal.valueOf(-1000))
                .paymentMethod(null)
                .build();

        when(paymentService.initPayment(invalidRequest))
                .thenThrow(new IllegalArgumentException("Invalid payment request"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentController.initPayment(invalidRequest);
        });

        verify(paymentService, times(1)).initPayment(invalidRequest);
    }

    @Test
    void testCapturePayment_Success() {
        // Given
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturePaymentResponseVm);

        // When
        CapturePaymentResponseVm response = paymentController.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(123L);
        assertThat(response.checkoutId()).isEqualTo("CHECKOUT-123");
        assertThat(response.amount()).isEqualByComparingTo(BigDecimal.valueOf(1000000));
        assertThat(response.paymentFee()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.gatewayTransactionId()).isEqualTo("TXN-123456");
        assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.failureMessage()).isNull();

        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    void testCapturePayment_WithInvalidAmount() {
        // Given
        CapturePaymentRequestVm invalidRequest = CapturePaymentRequestVm.builder()
                .paymentId("PAY-123")
                .captureAmount(BigDecimal.valueOf(-1000))
                .build();

        when(paymentService.capturePayment(invalidRequest))
                .thenThrow(new IllegalArgumentException("Capture amount must be positive"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentController.capturePayment(invalidRequest);
        });

        verify(paymentService, times(1)).capturePayment(invalidRequest);
    }

    @Test
    void testCapturePayment_WithNonExistentPayment() {
        // Given
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentId("PAY-999")
                .captureAmount(BigDecimal.valueOf(1000000))
                .build();

        when(paymentService.capturePayment(request))
                .thenThrow(new RuntimeException("Payment not found with id: PAY-999"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            paymentController.capturePayment(request);
        });

        verify(paymentService, times(1)).capturePayment(request);
    }

    @Test
    void testCapturePayment_WithAlreadyCapturedPayment() {
        // Given
        when(paymentService.capturePayment(capturePaymentRequestVm))
                .thenThrow(new IllegalStateException("Payment already captured"));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            paymentController.capturePayment(capturePaymentRequestVm);
        });

        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
    }

    @Test
    void testCancelPayment_Success() {
        // When
        ResponseEntity<String> response = paymentController.cancelPayment();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Payment cancelled");

        verifyNoInteractions(paymentService);
    }

    @Test
    void testInitPayment_WithDifferentPaymentMethods() {
        // Given
        InitPaymentRequestVm paypalRequest = InitPaymentRequestVm.builder()
                .orderId("ORDER-456")
                .amount(BigDecimal.valueOf(500000))
                .paymentMethod(PaymentMethod.PAYPAL)
                .returnUrl("https://example.com/return")
                .cancelUrl("https://example.com/cancel")
                .build();

        InitPaymentResponseVm paypalResponse = InitPaymentResponseVm.builder()
                .paymentId("PAY-456")
                .redirectUrl("https://paypal.example.com/PAY-456")
                .status("INITIATED")
                .build();

        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(paypalResponse);

        // When
        InitPaymentResponseVm response = paymentController.initPayment(paypalRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.paymentId()).isEqualTo("PAY-456");
        assertThat(response.redirectUrl()).contains("paypal");

        verify(paymentService, times(1)).initPayment(paypalRequest);
    }

    @Test
    void testCapturePayment_WithDifferentPaymentStatus() {
        // Given
        CapturePaymentResponseVm pendingResponse = CapturePaymentResponseVm.builder()
                .orderId(124L)
                .checkoutId("CHECKOUT-124")
                .amount(BigDecimal.valueOf(2000000))
                .paymentFee(BigDecimal.valueOf(20000))
                .gatewayTransactionId("TXN-789012")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .failureMessage(null)
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(pendingResponse);

        // When
        CapturePaymentResponseVm response = paymentController.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.failureMessage()).isNull();

        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
    }

    @Test
    void testCapturePayment_WithFailureStatus() {
        // Given
        CapturePaymentResponseVm failedResponse = CapturePaymentResponseVm.builder()
                .orderId(125L)
                .checkoutId("CHECKOUT-125")
                .amount(BigDecimal.valueOf(3000000))
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId(null)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.FAILED)
                .failureMessage("Insufficient funds")
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(failedResponse);

        // When
        CapturePaymentResponseVm response = paymentController.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(response.failureMessage()).isEqualTo("Insufficient funds");
        assertThat(response.gatewayTransactionId()).isNull();

        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
    }

    @Test
    void testInitPayment_VerifyServiceCalledExactlyOnce() {
        // Given
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initPaymentResponseVm);

        // When
        paymentController.initPayment(initPaymentRequestVm);

        // Then
        verify(paymentService, times(1)).initPayment(initPaymentRequestVm);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    void testCapturePayment_VerifyServiceCalledExactlyOnce() {
        // Given
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturePaymentResponseVm);

        // When
        paymentController.capturePayment(capturePaymentRequestVm);

        // Then
        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
        verifyNoMoreInteractions(paymentService);
    }
}