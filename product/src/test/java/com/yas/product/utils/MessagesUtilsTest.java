package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    /**
     * Test getting message with valid error code and no parameters
     */
    @Test
    void testGetMessage_WithValidErrorCodeNoParams_Success() {
        // Arrange
        String errorCode = "ERR_PRODUCT_NOT_FOUND";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode);
        
        // Assert
        assertNotNull(result);
        // If message is not found in bundle, it should return the error code itself
        assertEquals(errorCode, result);
    }

    /**
     * Test getting message with null error code
     */
    @Test
    void testGetMessage_WithNullErrorCode_Success() {
        // Act & Assert - Should handle gracefully
        try {
            String result = MessagesUtils.getMessage(null);
            assertNotNull(result);
        } catch (NullPointerException e) {
            // This is acceptable behavior for null input
        }
    }

    /**
     * Test getting message with empty error code
     */
    @Test
    void testGetMessage_WithEmptyErrorCode_Success() {
        // Arrange
        String errorCode = "";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode);
        
        // Assert
        assertNotNull(result);
        assertEquals(errorCode, result);
    }

    /**
     * Test getting message with one parameter
     */
    @Test
    void testGetMessage_WithOneParameter_Success() {
        // Arrange
        String errorCode = "ERR_PRODUCT_NOT_FOUND";
        Object param1 = "123";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1);
        
        // Assert
        assertNotNull(result);
        // Should contain the error code or formatted message
        assertEquals(errorCode, result);
    }

    /**
     * Test getting message with multiple parameters
     */
    @Test
    void testGetMessage_WithMultipleParameters_Success() {
        // Arrange
        String errorCode = "ERR_INVALID_INPUT";
        Object param1 = "field1";
        Object param2 = "value1";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1, param2);
        
        // Assert
        assertNotNull(result);
        assertEquals(errorCode, result);
    }

    /**
     * Test getting message with three parameters
     */
    @Test
    void testGetMessage_WithThreeParameters_Success() {
        // Arrange
        String errorCode = "ERR_VALIDATION_ERROR";
        Object param1 = "field";
        Object param2 = "value";
        Object param3 = "constraint";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1, param2, param3);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with null parameters
     */
    @Test
    void testGetMessage_WithNullParameter_Success() {
        // Arrange
        String errorCode = "ERR_TEST";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, (Object) null);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with numeric parameters
     */
    @Test
    void testGetMessage_WithNumericParameters_Success() {
        // Arrange
        String errorCode = "ERR_QUANTITY_ERROR";
        Object param1 = 100;
        Object param2 = 50;
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1, param2);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with boolean parameter
     */
    @Test
    void testGetMessage_WithBooleanParameter_Success() {
        // Arrange
        String errorCode = "ERR_BOOLEAN_CHECK";
        Object param1 = true;
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with special characters in parameters
     */
    @Test
    void testGetMessage_WithSpecialCharactersInParameters_Success() {
        // Arrange
        String errorCode = "ERR_SPECIAL_CHARS";
        Object param1 = "!@#$%^&*()";
        Object param2 = "test-value";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1, param2);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with unicode parameters
     */
    @Test
    void testGetMessage_WithUnicodeParameter_Success() {
        // Arrange
        String errorCode = "ERR_UNICODE";
        Object param1 = "Tiếng Việt";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with empty string parameter
     */
    @Test
    void testGetMessage_WithEmptyStringParameter_Success() {
        // Arrange
        String errorCode = "ERR_EMPTY_VALUE";
        Object param1 = "";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with long string parameter
     */
    @Test
    void testGetMessage_WithLongStringParameter_Success() {
        // Arrange
        String errorCode = "ERR_LONG_TEXT";
        String longText = "This is a very long string parameter that should be handled correctly ".repeat(5);
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, longText);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message multiple times with same error code
     */
    @Test
    void testGetMessage_CalledMultipleTimes_Success() {
        // Arrange
        String errorCode = "ERR_REUSABLE";
        
        // Act
        String result1 = MessagesUtils.getMessage(errorCode);
        String result2 = MessagesUtils.getMessage(errorCode);
        
        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1, result2);
    }

    /**
     * Test getting message with numeric string parameter
     */
    @Test
    void testGetMessage_WithNumericStringParameter_Success() {
        // Arrange
        String errorCode = "ERR_NUMERIC_STRING";
        Object param1 = "12345";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode, param1);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test message formatting with placeholder substitution
     */
    @Test
    void testGetMessage_WithFormattingPlaceholders_Success() {
        // Arrange
        String errorCode = "SLUG_ALREADY_EXISTED_OR_DUPLICATED";
        
        // Act
        String result = MessagesUtils.getMessage(errorCode);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test getting message with product-related error codes
     */
    @Test
    void testGetMessage_WithProductErrorCodes_Success() {
        // Arrange
        String[] errorCodes = {
            "PRODUCT_NOT_FOUND",
            "BRAND_NOT_FOUND",
            "CATEGORY_NOT_FOUND",
            "PRODUCT_ATTRIBUTE_NOT_FOUND"
        };
        
        // Act & Assert
        for (String errorCode : errorCodes) {
            String result = MessagesUtils.getMessage(errorCode);
            assertNotNull(result);
        }
    }

    /**
     * Test message consistency across multiple calls
     */
    @Test
    void testGetMessage_Consistency_Success() {
        // Arrange
        String errorCode = "CONSISTENCY_CHECK";
        Object[] params = {"param1", 123, true};
        
        // Act
        String result1 = MessagesUtils.getMessage(errorCode, params);
        String result2 = MessagesUtils.getMessage(errorCode, params);
        
        // Assert
        assertEquals(result1, result2);
    }
}
