package com.yas.payment.paypal.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PaypalCapturePaymentRequest Tests")
class PaypalCapturePaymentRequestTest {

    @Test
    @DisplayName("Should create record with all fields")
    void testCreatePaypalCapturePaymentRequest_WithAllFields() {
        // Act
        PaypalCapturePaymentRequest request = PaypalCapturePaymentRequest.builder()
                .token("EC-123456789")
                .paymentSettings("{\"clientId\":\"test\",\"clientSecret\":\"secret\"}")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("EC-123456789", request.token());
        assertEquals("{\"clientId\":\"test\",\"clientSecret\":\"secret\"}", request.paymentSettings());
    }

    @Test
    @DisplayName("Should create record with null fields")
    void testCreatePaypalCapturePaymentRequest_WithNullFields() {
        // Act
        PaypalCapturePaymentRequest request = PaypalCapturePaymentRequest.builder()
                .token(null)
                .paymentSettings(null)
                .build();

        // Assert
        assertNotNull(request);
        assertNull(request.token());
        assertNull(request.paymentSettings());
    }

    @Test
    @DisplayName("Should create record with empty string fields")
    void testCreatePaypalCapturePaymentRequest_WithEmptyStrings() {
        // Act
        PaypalCapturePaymentRequest request = PaypalCapturePaymentRequest.builder()
                .token("")
                .paymentSettings("")
                .build();

        // Assert
        assertNotNull(request);
        assertEquals("", request.token());
        assertEquals("", request.paymentSettings());
    }

    @Test
    @DisplayName("Should support record equals and hashCode")
    void testPaypalCapturePaymentRequest_EqualsAndHashCode() {
        // Arrange
        PaypalCapturePaymentRequest request1 = PaypalCapturePaymentRequest.builder()
                .token("EC-123")
                .paymentSettings("settings1")
                .build();
        
        PaypalCapturePaymentRequest request2 = PaypalCapturePaymentRequest.builder()
                .token("EC-123")
                .paymentSettings("settings1")
                .build();
        
        PaypalCapturePaymentRequest request3 = PaypalCapturePaymentRequest.builder()
                .token("EC-456")
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
    void testPaypalCapturePaymentRequest_ToString() {
        // Arrange
        PaypalCapturePaymentRequest request = PaypalCapturePaymentRequest.builder()
                .token("EC-789")
                .paymentSettings("test-settings")
                .build();

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("EC-789"));
        assertTrue(toString.contains("test-settings"));
        assertTrue(toString.contains("PaypalCapturePaymentRequest"));
    }
}