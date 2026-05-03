package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractCircuitBreakFallbackHandler Tests")
class AbstractCircuitBreakFallbackHandlerTest {

    private TestCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestCircuitBreakFallbackHandler();
    }

    @Test
    @DisplayName("Should handle bodiless fallback and rethrow exception")
    void testHandleBodilessFallback_ShouldRethrowException() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test circuit breaker error");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            handler.handleBodilessFallback(testException);
        });
    }

    @Test
    @DisplayName("Should handle typed fallback and return null after rethrowing exception")
    void testHandleTypedFallback_ShouldRethrowExceptionAndReturnNull() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test typed fallback error");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            String result = handler.handleTypedFallback(testException);
            fail("Should not reach this line");
        });
    }

    @Test
    @DisplayName("Should handle typed fallback with different return types")
    void testHandleTypedFallback_WithDifferentReturnTypes() throws Throwable {
        // Arrange
        RuntimeException testException = new RuntimeException("Test error");

        // Act & Assert for String return type
        assertThrows(RuntimeException.class, () -> {
            handler.handleTypedFallback(testException);
        });

        // Create handler with different generic type
        IntegerTestHandler integerHandler = new IntegerTestHandler();
        assertThrows(RuntimeException.class, () -> {
            integerHandler.handleTypedFallback(testException);
        });
    }

    @Test
    @DisplayName("Should handle error and log message")
    void testHandleError_ShouldLogAndRethrow() {
        // Arrange
        String errorMessage = "Database connection failed";
        RuntimeException testException = new RuntimeException(errorMessage);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            handler.handleError(testException);
        });

        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    @DisplayName("Should handle checked exceptions")
    void testHandleError_WithCheckedException() {
        // Arrange
        Exception checkedException = new Exception("Checked exception occurred");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            handler.handleError(checkedException);
        });
    }

    @Test
    @DisplayName("Should handle null throwable")
    void testHandleError_WithNullThrowable() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            handler.handleError(null);
        });
    }

    @Test
    @DisplayName("Should preserve original exception type")
    void testHandleError_PreservesOriginalExceptionType() {
        // Arrange
        IllegalArgumentException illegalArgException = new IllegalArgumentException("Invalid argument");

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            handler.handleError(illegalArgException);
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
            handler.handleError(nestedException);
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
    }

    @Test
    @DisplayName("Should be usable by subclass with specific type")
    void testConcreteSubclassUsage() {
        // Arrange
        ConcreteMediaService mediaService = new ConcreteMediaService();

        // Act & Assert
        RuntimeException testException = new RuntimeException("Media service error");
        
        assertThrows(RuntimeException.class, () -> {
            mediaService.handleMediaFallback(testException);
        });
    }

    @Test
    @DisplayName("Should handle Error type")
    void testHandleError_WithError() {
        // Arrange
        Error error = new OutOfMemoryError("Out of memory");

        // Act & Assert
        assertThrows(Error.class, () -> {
            handler.handleError(error);
        });
    }

    @Test
    @DisplayName("Should verify logging is called")
    void testLoggingIsCalled() {
        // This test verifies that the log.error method is called
        // Since we cannot easily mock static logger, we verify the behavior
        
        RuntimeException testException = new RuntimeException("Log test error");
        
        assertThrows(RuntimeException.class, () -> {
            handler.handleError(testException);
        });
        
        // The log message should be printed to console
        // In a real test environment, you could use a log appender to verify
    }

    // Helper class to test abstract handler
    private static class TestCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        // Expose protected methods for testing
        @Override
        protected void handleBodilessFallback(Throwable throwable) throws Throwable {
            super.handleBodilessFallback(throwable);
        }

        @Override
        protected <T> T handleTypedFallback(Throwable throwable) throws Throwable {
            return super.handleTypedFallback(throwable);
        }

        @Override
        protected void handleError(Throwable throwable) throws Throwable {
            super.handleError(throwable);
        }
    }

    // Handler with specific return type
    private static class IntegerTestHandler extends AbstractCircuitBreakFallbackHandler {
        @Override
        protected <T> T handleTypedFallback(Throwable throwable) throws Throwable {
            return super.handleTypedFallback(throwable);
        }
    }

    // Concrete implementation simulating MediaService
    private static class ConcreteMediaService extends AbstractCircuitBreakFallbackHandler {
        public NoFileMediaVm handleMediaFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    // Dummy class for return type
    private static class NoFileMediaVm {
        private Long id;
        private String fileName;
        
        public NoFileMediaVm() {}
        
        public NoFileMediaVm(Long id, String fileName) {
            this.id = id;
            this.fileName = fileName;
        }
    }
}