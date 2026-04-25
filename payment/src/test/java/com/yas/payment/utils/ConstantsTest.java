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
    void testErrorCodeConstructorIsPrivate() throws Exception {
        Constructor<Constants.ErrorCode> constructor = Constants.ErrorCode.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
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
    void testMessageConstructorIsPrivate() throws Exception {
        Constructor<Constants.Message> constructor = Constants.Message.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Constants should be final and cannot be extended")
    void testConstantsIsFinal() {
        assertTrue(Modifier.isFinal(Constants.class.getModifiers()));
    }
}