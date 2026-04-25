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
    @DisplayName("ErrorCode constants should be defined correctly")
    void testErrorCodeConstants() {
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isEqualTo("PAYMENT_PROVIDER_NOT_FOUND");
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND)
            .isNotBlank();
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