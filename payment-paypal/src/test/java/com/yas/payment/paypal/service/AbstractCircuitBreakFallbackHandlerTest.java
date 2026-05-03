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

import java.lang.reflect.Method;

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
    @DisplayName("[handleTypedFallback] Should rethrow RuntimeException, log error and return null (never reached)")
    void testHandleTypedFallback_WithRuntimeException() {
        // GIVEN - Tạo RuntimeException
        RuntimeException testException = new RuntimeException("Typed fallback runtime error");

        // WHEN + THEN - Phải throw exception (không bao giờ reach tới return null)
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            String result = handler.handleTypedFallback(testException);
            fail("Should not reach this line because exception should be thrown");
        });
        
        assertEquals("Typed fallback runtime error", thrown.getMessage());
        
        // Verify log được ghi
        assertFalse(listAppender.list.isEmpty());
        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, logEvent.getLevel());
        assertTrue(logEvent.getFormattedMessage().contains("Circuit breaker records an error"));
        assertTrue(logEvent.getFormattedMessage().contains("Typed fallback runtime error"));
    }


    @Test
    @DisplayName("[handleTypedFallback] Should rethrow Error and log error")
    void testHandleTypedFallback_WithError() {
        // GIVEN - Tạo Error
        Error testError = new StackOverflowError("Stack overflow error");

        // WHEN + THEN - Phải throw Error
        Error thrown = assertThrows(Error.class, () -> {
            handler.handleTypedFallback(testError);
        });
        
        assertEquals("Stack overflow error", thrown.getMessage());
        assertFalse(listAppender.list.isEmpty());
    }


    @Test
    @DisplayName("[handleTypedFallback] Should rethrow IllegalArgumentException")
    void testHandleTypedFallback_WithIllegalArgumentException() {
        // GIVEN - Tạo IllegalArgumentException
        IllegalArgumentException testException = new IllegalArgumentException("Invalid parameter");

        // WHEN + THEN - Phải throw đúng exception type
        assertThrows(IllegalArgumentException.class, () -> {
            handler.handleTypedFallback(testException);
        });
    }



    @Test
    @DisplayName("[handleBodilessFallback] Should rethrow RuntimeException and log error")
    void testHandleBodilessFallback_WithRuntimeException() {
        // GIVEN - Tạo một RuntimeException với message cụ thể
        RuntimeException testException = new RuntimeException("Circuit breaker runtime error");

        // WHEN - Gọi handleBodilessFallback với RuntimeException
        // THEN - Phải throw ra đúng exception đó
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            handler.handleBodilessFallback(testException);
        });
        
        // Verify exception message được giữ nguyên
        assertEquals("Circuit breaker runtime error", thrown.getMessage());
        
        // Verify log đã được ghi nhận
        assertFalse(listAppender.list.isEmpty());
        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, logEvent.getLevel());
        assertTrue(logEvent.getFormattedMessage().contains("Circuit breaker records an error"));
        assertTrue(logEvent.getFormattedMessage().contains("Circuit breaker runtime error"));
    }


    @Test
    @DisplayName("[handleBodilessFallback] Should rethrow Error and log error")
    void testHandleBodilessFallback_WithError() {
        // GIVEN - Tạo một Error
        Error testError = new OutOfMemoryError("Out of memory error");

        // WHEN + THEN - Phải throw ra đúng Error đó
        Error thrown = assertThrows(Error.class, () -> {
            handler.handleBodilessFallback(testError);
        });
        
        assertEquals("Out of memory error", thrown.getMessage());
        assertFalse(listAppender.list.isEmpty());
    }

    @Test
    @DisplayName("[handleBodilessFallback] Should rethrow IllegalArgumentException")
    void testHandleBodilessFallback_WithIllegalArgumentException() {
        // GIVEN - Tạo IllegalArgumentException
        IllegalArgumentException testException = new IllegalArgumentException("Invalid argument");

        // WHEN + THEN - Phải throw ra đúng exception type
        assertThrows(IllegalArgumentException.class, () -> {
            handler.handleBodilessFallback(testException);
        });
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
        
        // Verify log was called
        assertFalse(listAppender.list.isEmpty());
        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, logEvent.getLevel());
        assertTrue(logEvent.getFormattedMessage().contains("Circuit breaker records an error"));
    }

    @Test
    @DisplayName("Should handle bodiless fallback with checked exception")
    void testHandleBodilessFallback_WithCheckedException() {
        // Arrange
        Exception testException = new Exception("Checked exception");

        // Act & Assert
        Exception thrown = assertThrows(Exception.class, () -> {
            handler.handleBodilessFallback(testException);
        });
        
        assertEquals("Checked exception", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle bodiless fallback with null throwable")
    void testHandleBodilessFallback_WithNullThrowable() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            handler.handleBodilessFallback(null);
        });
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
        
        // Verify log was called
        assertFalse(listAppender.list.isEmpty());
        ILoggingEvent logEvent = listAppender.list.get(0);
        assertEquals(Level.ERROR, logEvent.getLevel());
        assertTrue(logEvent.getFormattedMessage().contains("Circuit breaker records an error"));
    }

    @Test
    @DisplayName("Should handle typed fallback with checked exception")
    void testHandleTypedFallback_WithCheckedException() {
        // Arrange
        Exception testException = new Exception("Checked exception in typed fallback");

        // Act & Assert
        Exception thrown = assertThrows(Exception.class, () -> {
            handler.handleTypedFallback(testException);
        });
        
        assertEquals("Checked exception in typed fallback", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle typed fallback with null throwable")
    void testHandleTypedFallback_WithNullThrowable() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            handler.handleTypedFallback(null);
        });
    }

    @Test
    @DisplayName("Should handle typed fallback with different return types - String")
    void testHandleTypedFallback_WithStringReturnType() {
        // Arrange
        RuntimeException testException = new RuntimeException("String return type error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            handler.handleTypedFallback(testException);
        });
        
        assertEquals("String return type error", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle typed fallback with different return types - Integer")
    void testHandleTypedFallback_WithIntegerReturnType() {
        // Arrange
        IntegerTestHandler integerHandler = new IntegerTestHandler();
        RuntimeException testException = new RuntimeException("Integer return type error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            integerHandler.handleTypedFallback(testException);
        });
        
        assertEquals("Integer return type error", thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle typed fallback with custom object return type")
    void testHandleTypedFallback_WithCustomObjectReturnType() {
        // Arrange
        ConcretePaymentService paymentService = new ConcretePaymentService();
        RuntimeException testException = new RuntimeException("Custom object return type error");

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            paymentService.handlePaymentFallback(testException);
        });
        
        assertEquals("Custom object return type error", thrown.getMessage());
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
        assertFalse(listAppender.list.isEmpty());
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
        assertFalse(listAppender.list.isEmpty());
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
        assertFalse(listAppender.list.isEmpty());
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
        
        ILoggingEvent firstLog = listAppender.list.get(0);
        ILoggingEvent secondLog = listAppender.list.get(1);
        
        assertTrue(firstLog.getFormattedMessage().contains("First error"));
        assertTrue(secondLog.getFormattedMessage().contains("Second error"));
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
        assertFalse(listAppender.list.isEmpty());
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
        assertFalse(listAppender.list.isEmpty());
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
            // Access private method via reflection
            try {
                Method method = AbstractCircuitBreakFallbackHandler.class.getDeclaredMethod("handleError", Throwable.class);
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
    
    // Handler that doesn't throw (for testing return null scenario)
    private static class NoThrowTestHandler extends AbstractCircuitBreakFallbackHandler {
        @Override
        protected <T> T handleTypedFallback(Throwable throwable) throws Throwable {
            // Override to test return null
            return null;
        }
    }

    // Dummy class for return type
    private static class PaymentResponse {
        private String status;
        
        public PaymentResponse(String status) {
            this.status = status;
        }
    }
}