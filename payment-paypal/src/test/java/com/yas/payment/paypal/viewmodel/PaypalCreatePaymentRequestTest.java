package com.yas.payment.paypal.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PaypalCreatePaymentRequest Tests")
class PaypalCreatePaymentRequestTest {

    @Test
    @DisplayName("Should create record with all fields")
    void testCreatePaypalCreatePaymentRequest_WithAllFields() {
        // Act
        PaypalCreatePaymentRequest request = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("199.99"))
                .checkoutId("checkout-123")
                .paymentMethod("PAYPAL")
                .paymentSettings("{\"clientId\":\"test\",\"clientSecret\":\"secret\",\"mode\":\"sandbox\"}")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(new BigDecimal("199.99"), request.totalPrice());
        assertEquals("checkout-123", request.checkoutId());
        assertEquals("PAYPAL", request.paymentMethod());
        assertEquals("{\"clientId\":\"test\",\"clientSecret\":\"secret\",\"mode\":\"sandbox\"}", request.paymentSettings());
    }

    @Test
    @DisplayName("Should create record with zero total price")
    void testCreatePaypalCreatePaymentRequest_WithZeroPrice() {
        // Act
        PaypalCreatePaymentRequest request = PaypalCreatePaymentRequest.builder()
                .totalPrice(BigDecimal.ZERO)
                .checkoutId("checkout-000")
                .paymentMethod("PAYPAL")
                .paymentSettings("{}")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(BigDecimal.ZERO, request.totalPrice());
    }

    @Test
    @DisplayName("Should create record with negative total price (refund case)")
    void testCreatePaypalCreatePaymentRequest_WithNegativePrice() {
        // Act
        PaypalCreatePaymentRequest request = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("-50.00"))
                .checkoutId("checkout-refund")
                .paymentMethod("PAYPAL")
                .paymentSettings("{}")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals(new BigDecimal("-50.00"), request.totalPrice());
    }

    @Test
    @DisplayName("Should create record with null fields")
    void testCreatePaypalCreatePaymentRequest_WithNullFields() {
        // Act
        PaypalCreatePaymentRequest request = PaypalCreatePaymentRequest.builder()
                .totalPrice(null)
                .checkoutId(null)
                .paymentMethod(null)
                .paymentSettings(null)
                .build();

        // Assert
        assertNotNull(request);
        assertNull(request.totalPrice());
        assertNull(request.checkoutId());
        assertNull(request.paymentMethod());
        assertNull(request.paymentSettings());
    }

    @Test
    @DisplayName("Should create record with empty string fields")
    void testCreatePaypalCreatePaymentRequest_WithEmptyStrings() {
        // Act
        PaypalCreatePaymentRequest request = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("99.99"))
                .checkoutId("")
                .paymentMethod("")
                .paymentSettings("")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("", request.checkoutId());
        assertEquals("", request.paymentMethod());
        assertEquals("", request.paymentSettings());
    }

    @Test
    @DisplayName("Should support record equals and hashCode")
    void testPaypalCreatePaymentRequest_EqualsAndHashCode() {
        // Arrange
        PaypalCreatePaymentRequest request1 = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("199.99"))
                .checkoutId("checkout-123")
                .paymentMethod("PAYPAL")
                .paymentSettings("settings1")
                .build();
        
        PaypalCreatePaymentRequest request2 = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("199.99"))
                .checkoutId("checkout-123")
                .paymentMethod("PAYPAL")
                .paymentSettings("settings1")
                .build();
        
        PaypalCreatePaymentRequest request3 = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("299.99"))
                .checkoutId("checkout-456")
                .paymentMethod("CREDIT_CARD")
                .paymentSettings("settings2")
                .build();

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    @DisplayName("Should support record toString")
    void testPaypalCreatePaymentRequest_ToString() {
        // Arrange
        PaypalCreatePaymentRequest request = PaypalCreatePaymentRequest.builder()
                .totalPrice(new BigDecimal("199.99"))
                .checkoutId("checkout-123")
                .paymentMethod("PAYPAL")
                .paymentSettings("test-settings")
                .build();

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("199.99"));
        assertTrue(toString.contains("checkout-123"));
        assertTrue(toString.contains("PAYPAL"));
        assertTrue(toString.contains("PaypalCreatePaymentRequest"));
    }
}