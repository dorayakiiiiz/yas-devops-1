package com.yas.payment.paypal.service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PayPalHttpClientInitializer Tests")
class PayPalHttpClientInitializerTest {

    private final PayPalHttpClientInitializer initializer = new PayPalHttpClientInitializer();


    @Test
    @DisplayName("Should throw exception when additionalSettings is null")
    void testCreatePaypalClient_NullSettings_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            initializer.createPaypalClient(null);
        });
        
        assertEquals("The additionalSettings can not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when additionalSettings is empty string")
    void testCreatePaypalClient_EmptySettings_ThrowsException() {
        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            initializer.createPaypalClient("");
        });
    }

    @Test
    @DisplayName("Should throw exception when clientId is missing")
    void testCreatePaypalClient_MissingClientId_ThrowsException() {
        // Arrange
        String additionalSettings = """
            {
                "clientSecret": "secret-123",
                "mode": "sandbox"
            }
            """;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            initializer.createPaypalClient(additionalSettings);
        });
    }

    @Test
    @DisplayName("Should throw exception when clientSecret is missing")
    void testCreatePaypalClient_MissingClientSecret_ThrowsException() {
        // Arrange
        String additionalSettings = """
            {
                "clientId": "client-123",
                "mode": "sandbox"
            }
            """;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            initializer.createPaypalClient(additionalSettings);
        });
    }

    @Test
    @DisplayName("Should throw exception when mode is missing")
    void testCreatePaypalClient_MissingMode_ThrowsException() {
        // Arrange
        String additionalSettings = """
            {
                "clientId": "client-123",
                "clientSecret": "secret-456"
            }
            """;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            initializer.createPaypalClient(additionalSettings);
        });
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void testCreatePaypalClient_InvalidJson_ThrowsException() {
        // Arrange
        String invalidJson = "invalid-json-string";

        // Act & Assert
        assertThrows(com.google.gson.JsonSyntaxException.class, () -> {
            initializer.createPaypalClient(invalidJson);
        });
    }

    @Test
    @DisplayName("Should handle mode case sensitivity - sandbox")
    void testCreatePaypalClient_ModeCaseInsensitive_Sandbox() {
        // Arrange
        String additionalSettings = """
            {
                "clientId": "client-123",
                "clientSecret": "secret-456",
                "mode": "SANDBOX"
            }
            """;

        // Act
        PayPalHttpClient client = initializer.createPaypalClient(additionalSettings);

        // Assert
        assertNotNull(client);
        // Note: Mode comparison is case-sensitive in the original code
        // "SANDBOX".equals("sandbox") is false, so it will go to Live environment
        // This test documents this behavior
    }

}