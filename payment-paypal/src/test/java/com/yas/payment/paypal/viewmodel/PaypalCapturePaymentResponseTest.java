package com.yas.payment.paypal.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PaypalCapturePaymentResponse Tests")
class PaypalCapturePaymentResponseTest {

    @Test
    @DisplayName("Should create record with all fields")
    void testCreatePaypalCapturePaymentResponse_WithAllFields() {
        // Act
        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("299.99"))
                .paymentFee(new BigDecimal("5.99"))
                .gatewayTransactionId("txn-456")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("checkout-123", response.checkoutId());
        assertEquals(new BigDecimal("299.99"), response.amount());
        assertEquals(new BigDecimal("5.99"), response.paymentFee());
        assertEquals("txn-456", response.gatewayTransactionId());
        assertEquals("PAYPAL", response.paymentMethod());
        assertEquals("COMPLETED", response.paymentStatus());
        assertNull(response.failureMessage());
    }

    @Test
    @DisplayName("Should create record with failure message for failed payment")
    void testCreatePaypalCapturePaymentResponse_WithFailureMessage() {
        // Act
        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-456")
                .amount(new BigDecimal("100.00"))
                .paymentFee(new BigDecimal("2.00"))
                .gatewayTransactionId("txn-789")
                .paymentMethod("PAYPAL")
                .paymentStatus("FAILED")
                .failureMessage("Insufficient funds")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("FAILED", response.paymentStatus());
        assertEquals("Insufficient funds", response.failureMessage());
    }

    @Test
    @DisplayName("Should create record with zero amounts")
    void testCreatePaypalCapturePaymentResponse_WithZeroAmounts() {
        // Act
        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-789")
                .amount(BigDecimal.ZERO)
                .paymentFee(BigDecimal.ZERO)
                .gatewayTransactionId("txn-000")
                .paymentMethod("PAYPAL")
                .paymentStatus("PENDING")
                .failureMessage(null)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.amount());
        assertEquals(BigDecimal.ZERO, response.paymentFee());
    }

    @Test
    @DisplayName("Should create record with null fields")
    void testCreatePaypalCapturePaymentResponse_WithNullFields() {
        // Act
        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId(null)
                .amount(null)
                .paymentFee(null)
                .gatewayTransactionId(null)
                .paymentMethod(null)
                .paymentStatus(null)
                .failureMessage(null)
                .build();

        // Assert
        assertNotNull(response);
        assertNull(response.checkoutId());
        assertNull(response.amount());
        assertNull(response.paymentFee());
        assertNull(response.gatewayTransactionId());
        assertNull(response.paymentMethod());
        assertNull(response.paymentStatus());
        assertNull(response.failureMessage());
    }

    @Test
    @DisplayName("Should support record equals and hashCode")
    void testPaypalCapturePaymentResponse_EqualsAndHashCode() {
        // Arrange
        PaypalCapturePaymentResponse response1 = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("299.99"))
                .paymentFee(new BigDecimal("5.99"))
                .gatewayTransactionId("txn-456")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();
        
        PaypalCapturePaymentResponse response2 = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("299.99"))
                .paymentFee(new BigDecimal("5.99"))
                .gatewayTransactionId("txn-456")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();
        
        PaypalCapturePaymentResponse response3 = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-456")
                .amount(new BigDecimal("199.99"))
                .paymentFee(new BigDecimal("3.99"))
                .gatewayTransactionId("txn-789")
                .paymentMethod("PAYPAL")
                .paymentStatus("PENDING")
                .failureMessage(null)
                .build();

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    @DisplayName("Should support record toString")
    void testPaypalCapturePaymentResponse_ToString() {
        // Arrange
        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("299.99"))
                .paymentFee(new BigDecimal("5.99"))
                .gatewayTransactionId("txn-456")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("checkout-123"));
        assertTrue(toString.contains("299.99"));
        assertTrue(toString.contains("PAYPAL"));
        assertTrue(toString.contains("COMPLETED"));
        assertTrue(toString.contains("PaypalCapturePaymentResponse"));
    }
}