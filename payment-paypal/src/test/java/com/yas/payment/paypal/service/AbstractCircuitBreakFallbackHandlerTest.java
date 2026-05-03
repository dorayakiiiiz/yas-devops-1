package com.yas.payment.paypal.service;

import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

@DisplayName("AbstractCircuitBreakFallbackHandler Tests")
class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler handler;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        handler = new TestCircuitBreakFallbackHandler();
        
        // Setup log appender for testing
        logger = (Logger) LoggerFactory.getLogger(AbstractCircuitBreakFallbackHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Should handle bodiless fallback and rethrow exception")
    void testHandleBodilessFallback_ShouldRethrowException() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test circuit breaker error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            handler.handleBodilessFallback(testException);
        });
        
        assertEquals("Test circuit breaker error", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle typed fallback and return null after rethrowing exception")
    void testHandleTypedFallback_ShouldRethrowExceptionAndReturnNull() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test typed fallback error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            String result = handler.handleTypedFallback(testException);
            fail("Should not reach this line");
        });
        
        assertEquals("Test typed fallback error", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle typed fallback with different return types")
    void testHandleTypedFallback_WithDifferentReturnTypes() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test error");

        // Act & Assert for String return type
        assertThrows(RuntimeException.class, () -> {
            handler.handleTypedFallback(testException);
        });

        // Test with Integer return type
        IntegerTestHandler integerHandler = new IntegerTestHandler();
        assertThrows(RuntimeException.class, () -> {
            integerHandler.handleTypedFallback(testException);
        });
    }

    @Test
    @DisplayName("Should log error when handling fallback")
    void testHandleError_ShouldLogErrorMessage() {
        // Arrange
        String errorMessage = "Database connection failed";
        RuntimeException testException = new RuntimeException(errorMessage);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            handler.exposeHandleError(testException);
        });

        assertEquals(errorMessage, thrown.getMessage());

        // Verify log was called
        assertFalse(listAppender.list.isEmpty());
        
        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, logEvent.getLevel());
        assertTrue(logEvent.getFormattedMessage().contains("Circuit breaker records an error"));
        assertTrue(logEvent.getFormattedMessage().contains(errorMessage));
    }

    @Test
    @DisplayName("Should handle checked exceptions")
    void testHandleError_WithCheckedException() {
        // Arrange
        Exception checkedException = new Exception("Checked exception occurred");

        // Act & Assert
        Exception thrown = assertThrows(Exception.class, () -> {
            handler.exposeHandleError(checkedException);
        });
        
        assertEquals("Checked exception occurred", thrown.getMessage());
    }

    @Test
    @DisplayName("Should preserve original exception type")
    void testHandleError_PreservesOriginalExceptionType() {
        // Arrange
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Invalid argument");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            handler.exposeHandleError(illegalArgException);
        });

        assertEquals("Invalid argument", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle nested exceptions")
    void testHandleError_WithNestedException() {
        // Arrange
        RuntimeException rootCause = new RuntimeException("Root cause");
        RuntimeException nestedException = new RuntimeException("Nested exception", rootCause);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            handler.exposeHandleError(nestedException);
        });

        assertEquals("Nested exception", thrown.getMessage());
        assertEquals(rootCause, thrown.getCause());
    }

    @Test
    @DisplayName("Should handle multiple fallback calls in sequence")
    void testMultipleFallbackCalls() {
        // Arrange
        RuntimeException exception1 = new RuntimeException("First error");
        RuntimeException exception2 = new RuntimeException("Second error");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(exception1));
        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(exception2));
        
        // Verify both errors were logged
        assertEquals(2, listAppender.list.size());
    }

    @Test
    @DisplayName("Should handle Error type")
    void testHandleError_WithError() {
        // Arrange
        Error error = new OutOfMemoryError("Out of memory");

        // Act & Assert
        Error thrown = assertThrows(Error.class, () -> {
            handler.exposeHandleError(error);
        });

        assertEquals("Out of memory", thrown.getMessage());
    }

    @Test
    @DisplayName("Should log error with exception details")
    void testHandleError_ShouldLogExceptionDetails() {
        // Arrange
        NullPointerException npe = new NullPointerException("Null value encountered at line 42");

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            handler.exposeHandleError(npe);
        });

        // Verify log contains exception message
        assertFalse(listAppender.list.isEmpty());
        ILoggingEvent logEvent = listAppender.list.get(0);
        assertTrue(logEvent.getFormattedMessage().contains("Null value encountered at line 42"));
    }

    @Test
    @DisplayName("Should be usable by concrete subclass")
    void testConcreteSubclassUsage() {
        // Arrange
        ConcretePaymentService paymentService = new ConcretePaymentService();
        RuntimeException testException = new RuntimeException("Payment service error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            paymentService.handlePaymentFallback(testException);
        });
        
        assertEquals("Payment service error", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle null throwable gracefully")
    void testHandleError_WithNullThrowable() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            handler.exposeHandleError(null);
        });
    }

    @Test
    @DisplayName("Should log error with correct format")
    void testHandleError_LogFormat() {
        // Arrange
        String errorMessage = "Connection timeout";
        RuntimeException testException = new RuntimeException(errorMessage);

        // Act
        assertThrows(RuntimeException.class, () -> {
            handler.exposeHandleError(testException);
        });

        // Assert log format
        ILoggingEvent logEvent = listAppender.list.get(0);
        String formattedMessage = logEvent.getFormattedMessage();
        
        assertTrue(formattedMessage.startsWith("Circuit breaker records an error. Detail "));
        assertTrue(formattedMessage.contains(errorMessage));
    }

    // Helper class to expose protected methods for testing
    private static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        
        public void handleBodilessFallback(Throwable throwable) throws Throwable {
            super.handleBodilessFallback(throwable);
        }

        public <T> T handleTypedFallback(Throwable throwable) throws Throwable {
            return super.handleTypedFallback(throwable);
        }

        public void exposeHandleError(Throwable throwable) throws Throwable {
            // Access private method via reflection or make it protected in production
            // For testing purposes, we'll use a workaround
            try {
                java.lang.reflect.Method method = AbstractCircuitBreakFallbackHandler.class.getDeclaredMethod("handleError", Throwable.class);
                method.setAccessible(true);
                method.invoke(this, throwable);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Handler with specific return type
    private static class IntegerTestHandler extends AbstractCircuitBreakFallbackHandler {
        public <T> T handleTypedFallback(Throwable throwable) throws Throwable {
            return super.handleTypedFallback(throwable);
        }
    }

    // Concrete implementation simulating PaymentService
    private static class ConcretePaymentService extends AbstractCircuitBreakFallbackHandler {
        public PaymentResponse handlePaymentFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    // Dummy class for return type
    private static class PaymentResponse {
        private String status;
        
        public PaymentResponse(String status) {
            this.status = status;
        }
    }

    @Test
    @DisplayName("Should execute handleBodilessFallback without exception")
    void testHandleBodilessFallback_NoException() throws Throwable {
        // Act & Assert (không exception)
        handler.handleBodilessFallback(new RuntimeException("ignore"));
    }

    @Test
    @DisplayName("Should execute handleTypedFallback and return null when no exception")
    void testHandleTypedFallback_NoException_ReturnNull() throws Throwable {
        // Act
        String result = handler.handleTypedFallback(new RuntimeException("ignore"));
        // Assert
        assertNull(result);
    }
}