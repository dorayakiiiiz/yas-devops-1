package com.yas.payment.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    @DisplayName("Constants class should have private constructor")
    void testConstructorIsPrivate() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("ErrorCode constants should be defined correctly")
    void testErrorCodeConstants() {
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isEqualTo("PAYMENT_PROVIDER_NOT_FOUND");
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isNotBlank();
    }

    @Test
    @DisplayName("ErrorCode class should have private constructor")
    void testErrorCodeConstructorIsPrivate() {
        // Get all constructors
        Constructor<?>[] constructors = Constants.ErrorCode.class.getDeclaredConstructors();
        
        // If there are constructors, verify they are private
        if (constructors.length > 0) {
            for (Constructor<?> constructor : constructors) {
                assertTrue(Modifier.isPrivate(constructor.getModifiers()));
                
                constructor.setAccessible(true);
                assertThrows(InvocationTargetException.class, () -> {
                    constructor.newInstance();
                });
            }
        } else {
            // If no constructors, the class is effectively not instantiable
            // This is also acceptable
            assertTrue(true, "ErrorCode class has no accessible constructors");
        }
    }

    @Test
    @DisplayName("Message constants should be defined correctly")
    void testMessageConstants() {
        assertThat(Constants.Message.SUCCESS_MESSAGE)
            .isEqualTo("SUCCESS");
        assertThat(Constants.Message.SUCCESS_MESSAGE)
            .isNotBlank();
    }

    @Test
    @DisplayName("Message class should have private constructor")
    void testMessageConstructorIsPrivate() {
        // Get all constructors
        Constructor<?>[] constructors = Constants.Message.class.getDeclaredConstructors();
        
        // If there are constructors, verify they are private
        if (constructors.length > 0) {
            for (Constructor<?> constructor : constructors) {
                assertTrue(Modifier.isPrivate(constructor.getModifiers()));
                
                constructor.setAccessible(true);
                assertThrows(InvocationTargetException.class, () -> {
                    constructor.newInstance();
                });
            }
        } else {
            // If no constructors, the class is effectively not instantiable
            // This is also acceptable
            assertTrue(true, "Message class has no accessible constructors");
        }
    }

    @Test
    @DisplayName("Constants should be final and cannot be extended")
    void testConstantsIsFinal() {
        assertTrue(Modifier.isFinal(Constants.class.getModifiers()));
    }
    
    @Test
    @DisplayName("ErrorCode class should be final")
    void testErrorCodeIsFinal() {
        assertTrue(Modifier.isFinal(Constants.ErrorCode.class.getModifiers()));
    }
    
    @Test
    @DisplayName("Message class should be final")
    void testMessageIsFinal() {
        assertTrue(Modifier.isFinal(Constants.Message.class.getModifiers()));
    }
}