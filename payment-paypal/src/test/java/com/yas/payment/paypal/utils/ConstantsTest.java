package com.yas.payment.paypal.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

@DisplayName("Constants Tests")
class ConstantsTest {

    // ==================== ErrorCode Tests ====================
    
    @Test
    @DisplayName("Should have correct ErrorCode constants")
    void testErrorCodeConstants() {
        assertEquals("SIGN_IN_REQUIRED", Constants.ErrorCode.SIGN_IN_REQUIRED);
        assertEquals("FORBIDDEN", Constants.ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("Should have final ErrorCode class")
    void testErrorCodeClassIsFinal() {
        assertTrue(Modifier.isFinal(Constants.ErrorCode.class.getModifiers()));
    }

    // ==================== Message Tests ====================

    @Test
    @DisplayName("Should have correct Message constants")
    void testMessageConstants() {
        assertEquals("PAYMENT_FAIL_MESSAGE", Constants.Message.PAYMENT_FAIL_MESSAGE);
        assertEquals("PAYMENT_SUCCESS_MESSAGE", Constants.Message.PAYMENT_SUCCESS_MESSAGE);
    }

    @Test
    @DisplayName("Should have final Message class")
    void testMessageClassIsFinal() {
        assertTrue(Modifier.isFinal(Constants.Message.class.getModifiers()));
    }

    // ==================== Yas Tests ====================

    @Test
    @DisplayName("Should have correct Yas constants")
    void testYasConstants() {
        assertEquals("Yas", Constants.Yas.BRAND_NAME);
    }

    @Test
    @DisplayName("Should have final Yas class")
    void testYasClassIsFinal() {
        assertTrue(Modifier.isFinal(Constants.Yas.class.getModifiers()));
    }

    // ==================== Main Constants Class Tests ====================

    @Test
    @DisplayName("Should have final Constants class")
    void testConstantsClassIsFinal() {
        assertTrue(Modifier.isFinal(Constants.class.getModifiers()));
    }

    // ==================== Cross-Validation Tests ====================

    @Test
    @DisplayName("Should have unique values across different error codes")
    void testErrorCodeValuesAreDifferent() {
        assertNotEquals(Constants.ErrorCode.SIGN_IN_REQUIRED, Constants.ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("Should have unique values across different messages")
    void testMessageValuesAreDifferent() {
        assertNotEquals(Constants.Message.PAYMENT_FAIL_MESSAGE, Constants.Message.PAYMENT_SUCCESS_MESSAGE);
    }

    @Test
    @DisplayName("Should have non-empty string values")
    void testConstantsAreNotEmpty() {
        assertFalse(Constants.ErrorCode.SIGN_IN_REQUIRED.isEmpty());
        assertFalse(Constants.ErrorCode.FORBIDDEN.isEmpty());
        assertFalse(Constants.Message.PAYMENT_FAIL_MESSAGE.isEmpty());
        assertFalse(Constants.Message.PAYMENT_SUCCESS_MESSAGE.isEmpty());
        assertFalse(Constants.Yas.BRAND_NAME.isEmpty());
    }

    @Test
    @DisplayName("Should have all constants as strings")
    void testConstantsTypes() {
        assertTrue(Constants.ErrorCode.SIGN_IN_REQUIRED instanceof String);
        assertTrue(Constants.ErrorCode.FORBIDDEN instanceof String);
        assertTrue(Constants.Message.PAYMENT_FAIL_MESSAGE instanceof String);
        assertTrue(Constants.Message.PAYMENT_SUCCESS_MESSAGE instanceof String);
        assertTrue(Constants.Yas.BRAND_NAME instanceof String);
    }

    // ==================== Nested Class Structure Tests ====================

    @Test
    @DisplayName("Should have all nested classes as final")
    void testNestedClassesAreFinal() {
        assertTrue(Modifier.isFinal(Constants.ErrorCode.class.getModifiers()));
        assertTrue(Modifier.isFinal(Constants.Message.class.getModifiers()));
        assertTrue(Modifier.isFinal(Constants.Yas.class.getModifiers()));
    }


    // ==================== Usage Scenario Tests ====================

    @Test
    @DisplayName("Should be usable in switch statements")
    void testConstantsUsableInSwitch() {
        String errorCode = Constants.ErrorCode.SIGN_IN_REQUIRED;
        String result = "";
        
        switch (errorCode) {
            case Constants.ErrorCode.SIGN_IN_REQUIRED:
                result = "sign_in";
                break;
            case Constants.ErrorCode.FORBIDDEN:
                result = "forbidden";
                break;
            default:
                result = "unknown";
        }
        
        assertEquals("sign_in", result);
    }

    @Test
    @DisplayName("Should be usable in if conditions")
    void testConstantsUsableInConditions() {
        String failMessage = Constants.Message.PAYMENT_FAIL_MESSAGE;
        String successMessage = Constants.Message.PAYMENT_SUCCESS_MESSAGE;
        
        assertTrue(failMessage.equals("PAYMENT_FAIL_MESSAGE"));
        assertTrue(successMessage.equals("PAYMENT_SUCCESS_MESSAGE"));
        assertFalse(failMessage.equals(successMessage));
    }

    @Test
    @DisplayName("Should maintain immutability")
    void testConstantsImmutability() {
        // Verify constants cannot be modified via reflection
        try {
            java.lang.reflect.Field field = Constants.ErrorCode.class.getField("SIGN_IN_REQUIRED");
            assertTrue(Modifier.isFinal(field.getModifiers()));
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertTrue(Modifier.isPublic(field.getModifiers()));
        } catch (NoSuchFieldException e) {
            fail("Field not found");
        }
    }

    @Test
    @DisplayName("Should have correct access modifiers for all constants")
    void testConstantsAccessModifiers() {
        // Check ErrorCode fields
        try {
            java.lang.reflect.Field signInField = Constants.ErrorCode.class.getField("SIGN_IN_REQUIRED");
            int modifiers = signInField.getModifiers();
            assertTrue(Modifier.isPublic(modifiers));
            assertTrue(Modifier.isStatic(modifiers));
            assertTrue(Modifier.isFinal(modifiers));
        } catch (NoSuchFieldException e) {
            fail("Field SIGN_IN_REQUIRED not found");
        }
        
        // Check Message fields
        try {
            java.lang.reflect.Field failField = Constants.Message.class.getField("PAYMENT_FAIL_MESSAGE");
            int modifiers = failField.getModifiers();
            assertTrue(Modifier.isPublic(modifiers));
            assertTrue(Modifier.isStatic(modifiers));
            assertTrue(Modifier.isFinal(modifiers));
        } catch (NoSuchFieldException e) {
            fail("Field PAYMENT_FAIL_MESSAGE not found");
        }
        
        // Check Yas fields
        try {
            java.lang.reflect.Field brandField = Constants.Yas.class.getField("BRAND_NAME");
            int modifiers = brandField.getModifiers();
            assertTrue(Modifier.isPublic(modifiers));
            assertTrue(Modifier.isStatic(modifiers));
            assertTrue(Modifier.isFinal(modifiers));
        } catch (NoSuchFieldException e) {
            fail("Field BRAND_NAME not found");
        }
    }
}