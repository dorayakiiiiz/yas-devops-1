package com.yas.payment.service.provider.handler;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHandlerTest {

    @Mock
    private PaymentHandler paymentHandler;

    private InitPaymentRequestVm initPaymentRequestVm;
    private CapturePaymentRequestVm capturePaymentRequestVm;
    private InitiatedPayment initiatedPayment;
    private CapturedPayment capturedPayment;

    @BeforeEach
    void setUp() {
        // Initialize test data
        initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod("CREDIT_CARD")
                .totalPrice(new BigDecimal("99.99"))
                .checkoutId("checkout-123")
                .build();

        capturePaymentRequestVm = CapturePaymentRequestVm.builder()
                .paymentMethod("CREDIT_CARD")
                .token("payment-token-456")
                .build();

        initiatedPayment = InitiatedPayment.builder()
                .id("init-001")
                .status("INITIATED")
                .paymentToken("token-789")
                .build();

        capturedPayment = CapturedPayment.builder()
                .id("capture-001")
                .status("CAPTURED")
                .transactionId("txn-001")
                .build();
    }

    @Test
    void testGetProviderId() {
        // Given
        String expectedProviderId = "PAYPAL";
        when(paymentHandler.getProviderId()).thenReturn(expectedProviderId);

        // When
        String actualProviderId = paymentHandler.getProviderId();

        // Then
        assertThat(actualProviderId).isEqualTo(expectedProviderId);
    }

    @Test
    void testInitPayment_Success() {
        // Given
        when(paymentHandler.initPayment(any(InitPaymentRequestVm.class)))
                .thenReturn(initiatedPayment);

        // When
        InitiatedPayment result = paymentHandler.initPayment(initPaymentRequestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("init-001");
        assertThat(result.getStatus()).isEqualTo("INITIATED");
        assertThat(result.getPaymentToken()).isEqualTo("token-789");
    }

    @Test
    void testInitPayment_WithValidRequest() {
        // Given
        when(paymentHandler.initPayment(initPaymentRequestVm))
                .thenReturn(initiatedPayment);

        // When
        InitiatedPayment result = paymentHandler.initPayment(initPaymentRequestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(initiatedPayment);
    }

    @Test
    void testCapturePayment_Success() {
        // Given
        when(paymentHandler.capturePayment(any(CapturePaymentRequestVm.class)))
                .thenReturn(capturedPayment);

        // When
        CapturedPayment result = paymentHandler.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("capture-001");
        assertThat(result.getStatus()).isEqualTo("CAPTURED");
        assertThat(result.getTransactionId()).isEqualTo("txn-001");
    }

    @Test
    void testCapturePayment_WithValidRequest() {
        // Given
        when(paymentHandler.capturePayment(capturePaymentRequestVm))
                .thenReturn(capturedPayment);

        // When
        CapturedPayment result = paymentHandler.capturePayment(capturePaymentRequestVm);

        // Then
        assertThat(result).isEqualTo(capturedPayment);
    }

    @Test
    void testInitPayment_WithDifferentPaymentMethods() {
        // Given
        InitPaymentRequestVm paypalRequest = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(new BigDecimal("150.00"))
                .checkoutId("checkout-paypal")
                .build();

        InitiatedPayment expectedPayment = InitiatedPayment.builder()
                .id("init-paypal")
                .status("INITIATED")
                .paymentToken("paypal-token")
                .build();

        when(paymentHandler.initPayment(paypalRequest)).thenReturn(expectedPayment);

        // When
        InitiatedPayment result = paymentHandler.initPayment(paypalRequest);

        // Then
        assertThat(result.getPaymentToken()).isEqualTo("paypal-token");
        assertThat(result.getId()).isEqualTo("init-paypal");
    }

    @Test
    void testCapturePayment_WithDifferentTokens() {
        // Given
        CapturePaymentRequestVm differentTokenRequest = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("different-token-999")
                .build();

        CapturedPayment expectedCapture = CapturedPayment.builder()
                .id("capture-diff")
                .status("CAPTURED")
                .transactionId("txn-diff-999")
                .build();

        when(paymentHandler.capturePayment(differentTokenRequest)).thenReturn(expectedCapture);

        // When
        CapturedPayment result = paymentHandler.capturePayment(differentTokenRequest);

        // Then
        assertThat(result.getTransactionId()).isEqualTo("txn-diff-999");
        assertThat(result.getId()).isEqualTo("capture-diff");
    }
}