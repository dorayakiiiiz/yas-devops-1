package com.yas.payment.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
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
                .amount(1000000)
                .paymentMethod("CREDIT_CARD")
                .build();

        initPaymentResponseVm = InitPaymentResponseVm.builder()
                .paymentId("PAY-123")
                .paymentUrl("https://payment.example.com/PAY-123")
                .status("INITIATED")
                .build();

        // Khởi tạo dữ liệu mẫu cho CapturePayment
        capturePaymentRequestVm = CapturePaymentRequestVm.builder()
                .paymentId("PAY-123")
                .captureAmount(1000000)
                .build();

        capturePaymentResponseVm = CapturePaymentResponseVm.builder()
                .paymentId("PAY-123")
                .status("CAPTURED")
                .capturedAmount(1000000)
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
        assertThat(response.getPaymentId()).isEqualTo("PAY-123");
        assertThat(response.getPaymentUrl()).isEqualTo("https://payment.example.com/PAY-123");
        assertThat(response.getStatus()).isEqualTo("INITIATED");

        verify(paymentService, times(1)).initPayment(initPaymentRequestVm);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    void testInitPayment_WithNullRequest() {
        // Given
        when(paymentService.initPayment(null)).thenThrow(new IllegalArgumentException("Request cannot be null"));

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            paymentController.initPayment(null);
        });

        verify(paymentService, times(1)).initPayment(null);
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
        assertThat(response.getPaymentId()).isEqualTo("PAY-123");
        assertThat(response.getStatus()).isEqualTo("CAPTURED");
        assertThat(response.getCapturedAmount()).isEqualTo(1000000);

        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    void testCapturePayment_WithInvalidAmount() {
        // Given
        CapturePaymentRequestVm invalidRequest = CapturePaymentRequestVm.builder()
                .paymentId("PAY-123")
                .captureAmount(-1000) // Số tiền không hợp lệ
                .build();

        when(paymentService.capturePayment(invalidRequest))
                .thenThrow(new IllegalArgumentException("Capture amount must be positive"));

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            paymentController.capturePayment(invalidRequest);
        });

        verify(paymentService, times(1)).capturePayment(invalidRequest);
    }

    @Test
    void testCapturePayment_WithNonExistentPayment() {
        // Given
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenThrow(new RuntimeException("Payment not found with id: PAY-999"));

        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentId("PAY-999")
                .captureAmount(1000000)
                .build();

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            paymentController.capturePayment(request);
        });

        verify(paymentService, times(1)).capturePayment(request);
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
    void testCancelPayment_ReturnsOkStatus() {
        // When
        ResponseEntity<String> response = paymentController.cancelPayment();

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotBlank();
    }

    @Test
    void testInitPayment_VerifyServiceCalledOnce() {
        // Given
        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initPaymentResponseVm);

        // When
        paymentController.initPayment(initPaymentRequestVm);

        // Then
        verify(paymentService, times(1)).initPayment(initPaymentRequestVm);
    }

    @Test
    void testCapturePayment_VerifyServiceCalledOnce() {
        // Given
        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturePaymentResponseVm);

        // When
        paymentController.capturePayment(capturePaymentRequestVm);

        // Then
        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
    }

    @Test
    void testInitPayment_WithDifferentPaymentMethods() {
        // Given
        InitPaymentRequestVm paypalRequest = InitPaymentRequestVm.builder()
                .orderId("ORDER-456")
                .amount(500000)
                .paymentMethod("PAYPAL")
                .build();

        InitPaymentResponseVm paypalResponse = InitPaymentResponseVm.builder()
                .paymentId("PAY-456")
                .paymentUrl("https://paypal.example.com/PAY-456")
                .status("INITIATED")
                .build();

        when(paymentService.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(paypalResponse);

        // When
        InitPaymentResponseVm response = paymentController.initPayment(paypalRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentId()).isEqualTo("PAY-456");
        verify(paymentService, times(1)).initPayment(paypalRequest);
    }
}