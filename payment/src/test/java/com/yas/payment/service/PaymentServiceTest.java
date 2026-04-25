package com.yas.payment.service;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.provider.handler.PaymentHandler;
import com.yas.payment.viewmodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentRepository paymentRepository;
    private OrderService orderService;
    private PaymentHandler paymentHandler;
    private PaymentHandler anotherPaymentHandler;
    private List<PaymentHandler> paymentHandlers;
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderService = mock(OrderService.class);
        paymentHandler = mock(PaymentHandler.class);
        anotherPaymentHandler = mock(PaymentHandler.class);
        
        paymentHandlers = new ArrayList<>();
        paymentHandlers.add(paymentHandler);
        paymentHandlers.add(anotherPaymentHandler);
        
        paymentService = new PaymentService(paymentRepository, orderService, paymentHandlers);

        when(paymentHandler.getProviderId()).thenReturn(PaymentMethod.PAYPAL.name());
        when(anotherPaymentHandler.getProviderId()).thenReturn(PaymentMethod.BANKING.name());
        
        paymentService.initializeProviders();

        payment = Payment.builder()
                .id(1L)
                .checkoutId("secretCheckoutId")
                .orderId(2L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentFee(BigDecimal.valueOf(500))
                .paymentMethod(PaymentMethod.BANKING)
                .amount(BigDecimal.valueOf(100.0))
                .failureMessage(null)
                .gatewayTransactionId("gatewayId")
                .build();
    }

    @Test
    @DisplayName("Init payment should succeed with valid payment method")
    void initPayment_Success() {
        // Given
        InitPaymentRequestVm initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(BigDecimal.TEN)
                .checkoutId("123")
                .build();
        
        InitiatedPayment initiatedPayment = InitiatedPayment.builder()
                .paymentId("123")
                .status("success")
                .redirectUrl("http://abc.com")
                .build();
        
        when(paymentHandler.initPayment(initPaymentRequestVm)).thenReturn(initiatedPayment);
        
        // When
        InitPaymentResponseVm result = paymentService.initPayment(initPaymentRequestVm);
        
        // Then
        assertNotNull(result);
        assertEquals(initiatedPayment.getPaymentId(), result.paymentId());
        assertEquals(initiatedPayment.getStatus(), result.status());
        assertEquals(initiatedPayment.getRedirectUrl(), result.redirectUrl());
        
        verify(paymentHandler, times(1)).initPayment(initPaymentRequestVm);
    }

    @Test
    @DisplayName("Init payment should throw exception when payment handler not found")
    void initPayment_ThrowsException_WhenPaymentHandlerNotFound() {
        // Given
        InitPaymentRequestVm initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod("INVALID_METHOD")
                .totalPrice(BigDecimal.TEN)
                .checkoutId("123")
                .build();
        
        // When & Then
        assertThatThrownBy(() -> paymentService.initPayment(initPaymentRequestVm))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No payment handler found for provider: INVALID_METHOD");
        
        verify(paymentHandler, never()).initPayment(any());
    }

    @Test
    @DisplayName("Capture payment should succeed with valid data")
    void capturePayment_Success() {
        // Given
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        CapturedPayment capturedPayment = prepareCapturedPayment();
        Long orderId = 999L;
        
        when(paymentHandler.capturePayment(capturePaymentRequestVM)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(orderId);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        
        // When
        CapturePaymentResponseVm response = paymentService.capturePayment(capturePaymentRequestVM);
        
        // Then
        verifyPaymentCreation(response);
        verifyOrderServiceInteractions(capturedPayment);
        verifyResult(capturedPayment, response);
    }

    @Test
    @DisplayName("Capture payment should throw exception when payment handler not found")
    void capturePayment_ThrowsException_WhenPaymentHandlerNotFound() {
        // Given
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod("INVALID_METHOD")
                .token("123")
                .build();
        
        // When & Then
        assertThatThrownBy(() -> paymentService.capturePayment(capturePaymentRequestVM))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No payment handler found for provider: INVALID_METHOD");
        
        verify(paymentHandler, never()).capturePayment(any());
        verify(orderService, never()).updateCheckoutStatus(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Capture payment should handle cancelled payment status")
    void capturePayment_WithCancelledPaymentStatus() {
        // Given
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        CapturedPayment cancelledCapturedPayment = CapturedPayment.builder()
                .orderId(null)
                .checkoutId("secretCheckoutId")
                .amount(BigDecimal.valueOf(100.0))
                .paymentFee(BigDecimal.valueOf(500))
                .gatewayTransactionId(null)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.CANCELLED)
                .failureMessage("Payment cancelled by user")
                .build();
        
        Long orderId = null;
        
        when(paymentHandler.capturePayment(capturePaymentRequestVM)).thenReturn(cancelledCapturedPayment);
        when(orderService.updateCheckoutStatus(cancelledCapturedPayment)).thenReturn(orderId);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        CapturePaymentResponseVm response = paymentService.capturePayment(capturePaymentRequestVM);
        
        // Then
        assertNotNull(response);
        assertNull(response.orderId());
        assertEquals(PaymentStatus.CANCELLED, response.paymentStatus());
        assertEquals("Payment cancelled by user", response.failureMessage());
        assertNull(response.gatewayTransactionId());
        
        verify(orderService, times(1)).updateCheckoutStatus(cancelledCapturedPayment);
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Capture payment should handle pending payment status")
    void capturePayment_WithPendingPaymentStatus() {
        // Given
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        CapturedPayment pendingCapturedPayment = CapturedPayment.builder()
                .orderId(null)
                .checkoutId("secretCheckoutId")
                .amount(BigDecimal.valueOf(100.0))
                .paymentFee(BigDecimal.valueOf(500))
                .gatewayTransactionId("pending-tx-123")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .failureMessage(null)
                .build();
        
        Long orderId = null;
        
        when(paymentHandler.capturePayment(capturePaymentRequestVM)).thenReturn(pendingCapturedPayment);
        when(orderService.updateCheckoutStatus(pendingCapturedPayment)).thenReturn(orderId);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        CapturePaymentResponseVm response = paymentService.capturePayment(capturePaymentRequestVM);
        
        // Then
        assertNotNull(response);
        assertNull(response.orderId());
        assertEquals(PaymentStatus.PENDING, response.paymentStatus());
        assertEquals("pending-tx-123", response.gatewayTransactionId());
        assertNull(response.failureMessage());
        
        verify(orderService, times(1)).updateCheckoutStatus(pendingCapturedPayment);
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Capture payment should handle null order ID from order service")
    void capturePayment_WhenOrderServiceReturnsNullOrderId() {
        // Given
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        CapturedPayment capturedPayment = prepareCapturedPayment();
        
        when(paymentHandler.capturePayment(capturePaymentRequestVM)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(null);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        
        // When
        CapturePaymentResponseVm response = paymentService.capturePayment(capturePaymentRequestVM);
        
        // Then
        assertNotNull(response);
        assertNull(response.orderId());
        
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertNull(paymentCaptor.getValue().getOrderId());
        
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
    }

    @Test
    @DisplayName("Initialize providers should register all payment handlers correctly")
    void initializeProviders_ShouldRegisterAllHandlers() {
        // Given
        PaymentService newPaymentService = new PaymentService(paymentRepository, orderService, paymentHandlers);
        
        // When
        newPaymentService.initializeProviders();
        
        // Then - verify that handlers are accessible by calling initPayment for each provider
        InitPaymentRequestVm paypalRequest = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(BigDecimal.TEN)
                .checkoutId("123")
                .build();
        
        InitPaymentRequestVm bankingRequest = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.BANKING.name())
                .totalPrice(BigDecimal.TEN)
                .checkoutId("456")
                .build();
        
        InitiatedPayment initiatedPayment = InitiatedPayment.builder()
                .paymentId("123")
                .status("success")
                .redirectUrl("http://abc.com")
                .build();
        
        when(paymentHandler.initPayment(any())).thenReturn(initiatedPayment);
        when(anotherPaymentHandler.initPayment(any())).thenReturn(initiatedPayment);
        
        // Should not throw exceptions
        assertDoesNotThrow(() -> newPaymentService.initPayment(paypalRequest));
        assertDoesNotThrow(() -> newPaymentService.initPayment(bankingRequest));
        
        verify(paymentHandler, times(1)).initPayment(any());
        verify(anotherPaymentHandler, times(1)).initPayment(any());
    }

    @Test
    @DisplayName("Should handle multiple payment handlers and select correct one")
    void capturePayment_ShouldSelectCorrectHandler() {
        // Given
        CapturePaymentRequestVm paypalRequest = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("paypal-token")
                .build();
        
        CapturePaymentRequestVm bankingRequest = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.BANKING.name())
                .token("banking-token")
                .build();
        
        CapturedPayment paypalCapturedPayment = prepareCapturedPayment();
        CapturedPayment bankingCapturedPayment = prepareCapturedPayment();
        
        when(paymentHandler.capturePayment(paypalRequest)).thenReturn(paypalCapturedPayment);
        when(anotherPaymentHandler.capturePayment(bankingRequest)).thenReturn(bankingCapturedPayment);
        when(orderService.updateCheckoutStatus(any())).thenReturn(1L);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        
        // When
        paymentService.capturePayment(paypalRequest);
        paymentService.capturePayment(bankingRequest);
        
        // Then
        verify(paymentHandler, times(1)).capturePayment(paypalRequest);
        verify(anotherPaymentHandler, times(1)).capturePayment(bankingRequest);
        verify(paymentHandler, never()).capturePayment(bankingRequest);
        verify(anotherPaymentHandler, never()).capturePayment(paypalRequest);
    }

    @Test
    @DisplayName("Create payment should save payment with all fields correctly")
    void createPayment_ShouldSavePaymentWithCorrectFields() {
        // Given
        CapturedPayment capturedPayment = prepareCapturedPayment();
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        
        // When - call capturePayment which internally calls createPayment
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        when(paymentHandler.capturePayment(request)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(1L);
        
        paymentService.capturePayment(request);
        
        // Then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        
        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(capturedPayment.getCheckoutId(), savedPayment.getCheckoutId());
        assertEquals(capturedPayment.getOrderId(), savedPayment.getOrderId());
        assertEquals(capturedPayment.getPaymentStatus(), savedPayment.getPaymentStatus());
        assertEquals(capturedPayment.getPaymentFee(), savedPayment.getPaymentFee());
        assertEquals(capturedPayment.getPaymentMethod(), savedPayment.getPaymentMethod());
        assertEquals(capturedPayment.getAmount(), savedPayment.getAmount());
        assertEquals(capturedPayment.getFailureMessage(), savedPayment.getFailureMessage());
        assertEquals(capturedPayment.getGatewayTransactionId(), savedPayment.getGatewayTransactionId());
    }

    @Test
    @DisplayName("Should handle payment with zero amount")
    void capturePayment_WithZeroAmount() {
        // Given
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        CapturedPayment zeroAmountPayment = CapturedPayment.builder()
                .orderId(2L)
                .checkoutId("checkout-zero")
                .amount(BigDecimal.ZERO)
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId("tx-zero")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();
        
        Payment zeroAmountDbPayment = Payment.builder()
                .id(1L)
                .checkoutId("checkout-zero")
                .orderId(2L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentFee(BigDecimal.ZERO)
                .paymentMethod(PaymentMethod.PAYPAL)
                .amount(BigDecimal.ZERO)
                .gatewayTransactionId("tx-zero")
                .build();
        
        when(paymentHandler.capturePayment(request)).thenReturn(zeroAmountPayment);
        when(orderService.updateCheckoutStatus(zeroAmountPayment)).thenReturn(2L);
        when(paymentRepository.save(any(Payment.class))).thenReturn(zeroAmountDbPayment);
        
        // When
        CapturePaymentResponseVm response = paymentService.capturePayment(request);
        
        // Then
        assertEquals(BigDecimal.ZERO, response.amount());
        assertEquals(BigDecimal.ZERO, response.paymentFee());
        
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(BigDecimal.ZERO, paymentCaptor.getValue().getAmount());
        assertEquals(BigDecimal.ZERO, paymentCaptor.getValue().getPaymentFee());
    }

    @Test
    @DisplayName("Should handle payment repository save exception")
    void capturePayment_WhenPaymentRepositoryFails() {
        // Given
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("123")
                .build();
        
        CapturedPayment capturedPayment = prepareCapturedPayment();
        
        when(paymentHandler.capturePayment(request)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(1L);
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThatThrownBy(() -> paymentService.capturePayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
        
        verify(orderService, times(1)).updateCheckoutStatus(capturedPayment);
        verify(orderService, never()).updateOrderStatus(any());
    }

    private CapturedPayment prepareCapturedPayment() {
        return CapturedPayment.builder()
                .orderId(2L)
                .checkoutId("secretCheckoutId")
                .amount(BigDecimal.valueOf(100.0))
                .paymentFee(BigDecimal.valueOf(500))
                .gatewayTransactionId("gatewayId")
                .paymentMethod(PaymentMethod.BANKING)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage(null)
                .build();
    }

    private void verifyPaymentCreation(CapturePaymentResponseVm capturedPayment) {
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment capturedPaymentResult = paymentCaptor.getValue();

        assertThat(capturedPaymentResult.getCheckoutId()).isEqualTo(capturedPayment.checkoutId());
        assertThat(capturedPaymentResult.getOrderId()).isEqualTo(capturedPayment.orderId());
        assertThat(capturedPaymentResult.getPaymentStatus()).isEqualTo(capturedPayment.paymentStatus());
        assertThat(capturedPaymentResult.getPaymentFee()).isEqualByComparingTo(capturedPayment.paymentFee());
        assertThat(capturedPaymentResult.getAmount()).isEqualByComparingTo(capturedPayment.amount());
    }

    private void verifyOrderServiceInteractions(CapturedPayment capturedPayment) {
        verify(orderService, times(1)).updateCheckoutStatus(capturedPayment);
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
    }

    private void verifyResult(CapturedPayment capturedPayment, CapturePaymentResponseVm responseVm) {
        assertEquals(capturedPayment.getOrderId(), responseVm.orderId());
        assertEquals(capturedPayment.getCheckoutId(), responseVm.checkoutId());
        assertEquals(capturedPayment.getAmount(), responseVm.amount());
        assertEquals(capturedPayment.getPaymentFee(), responseVm.paymentFee());
        assertEquals(capturedPayment.getGatewayTransactionId(), responseVm.gatewayTransactionId());
        assertEquals(capturedPayment.getPaymentMethod(), responseVm.paymentMethod());
        assertEquals(capturedPayment.getPaymentStatus(), responseVm.paymentStatus());
        assertEquals(capturedPayment.getFailureMessage(), responseVm.failureMessage());
    }
}