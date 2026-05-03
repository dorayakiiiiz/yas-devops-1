package com.yas.payment.paypal.viewmodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PaypalCreatePaymentResponse Tests")
class PaypalCreatePaymentResponseTest {

    @Test
    @DisplayName("Should create record with all fields for successful payment")
    void testCreatePaypalCreatePaymentResponse_Success() {
        // Act
        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("PAYID-123456789")
                .redirectUrl("https://www.paypal.com/checkoutnow?token=EC-123")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("CREATED", response.status());
        assertEquals("PAYID-123456789", response.paymentId());
        assertEquals("https://www.paypal.com/checkoutnow?token=EC-123", response.redirectUrl());
    }

    @Test
    @DisplayName("Should create record with status APPROVED")
    void testCreatePaypalCreatePaymentResponse_Approved() {
        // Act
        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("APPROVED")
                .paymentId("PAYID-987654321")
                .redirectUrl("https://www.paypal.com/checkoutnow?token=EC-456")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("APPROVED", response.status());
    }

    @Test
    @DisplayName("Should create record with status FAILED")
    void testCreatePaypalCreatePaymentResponse_Failed() {
        // Act
        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("FAILED")
                .paymentId(null)
                .redirectUrl(null)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("FAILED", response.status());
        assertNull(response.paymentId());
        assertNull(response.redirectUrl());
    }

    @Test
    @DisplayName("Should create record with null fields")
    void testCreatePaypalCreatePaymentResponse_WithNullFields() {
        // Act
        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status(null)
                .paymentId(null)
                .redirectUrl(null)
                .build();

        // Assert
        assertNotNull(response);
        assertNull(response.status());
        assertNull(response.paymentId());
        assertNull(response.redirectUrl());
    }

    @Test
    @DisplayName("Should create record with empty string fields")
    void testCreatePaypalCreatePaymentResponse_WithEmptyStrings() {
        // Act
        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("")
                .paymentId("")
                .redirectUrl("")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("", response.status());
        assertEquals("", response.paymentId());
        assertEquals("", response.redirectUrl());
    }

    @Test
    @DisplayName("Should support record equals and hashCode")
    void testPaypalCreatePaymentResponse_EqualsAndHashCode() {
        // Arrange
        PaypalCreatePaymentResponse response1 = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("PAYID-123")
                .redirectUrl("https://paypal.com/url1")
                .build();
        
        PaypalCreatePaymentResponse response2 = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("PAYID-123")
                .redirectUrl("https://paypal.com/url1")
                .build();
        
        PaypalCreatePaymentResponse response3 = PaypalCreatePaymentResponse.builder()
                .status("FAILED")
                .paymentId("PAYID-456")
                .redirectUrl("https://paypal.com/url2")
                .build();

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    @DisplayName("Should support record toString")
    void testPaypalCreatePaymentResponse_ToString() {
        // Arrange
        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("PAYID-123456789")
                .redirectUrl("https://www.paypal.com/checkoutnow")
                .build();

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("CREATED"));
        assertTrue(toString.contains("PAYID-123456789"));
        assertTrue(toString.contains("https://www.paypal.com/checkoutnow"));
        assertTrue(toString.contains("PaypalCreatePaymentResponse"));
    }
}