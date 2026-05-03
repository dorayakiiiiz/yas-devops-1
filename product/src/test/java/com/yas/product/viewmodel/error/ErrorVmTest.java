package com.yas.product.viewmodel.error;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ErrorVm Record Tests")
class ErrorVmTest {

    private String statusCode;
    private String title;
    private String detail;
    private List<String> fieldErrors;

    @BeforeEach
    void setUp() {
        statusCode = "400";
        title = "Bad Request";
        detail = "Invalid input provided";
        fieldErrors = new ArrayList<>();
    }

    @Test
    @DisplayName("Should create ErrorVm with all parameters including fieldErrors")
    void testFullConstructor() {
        fieldErrors.add("Field 'name' is required");
        fieldErrors.add("Field 'email' must be valid");

        ErrorVm errorVm = new ErrorVm(statusCode, title, detail, fieldErrors);

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input provided", errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("Field 'name' is required"));
        assertTrue(errorVm.fieldErrors().contains("Field 'email' must be valid"));
    }

    @Test
    @DisplayName("Should create ErrorVm with convenience constructor (without fieldErrors)")
    void testConvenienceConstructor() {
        ErrorVm errorVm = new ErrorVm(statusCode, title, detail);

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input provided", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertEquals(0, errorVm.fieldErrors().size());
    }

    @Test
    @DisplayName("Should have empty fieldErrors list when using convenience constructor")
    void testConvenienceConstructorEmptyFieldErrors() {
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Resource not found");

        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    @DisplayName("Should access statusCode component")
    void testStatusCodeComponent() {
        ErrorVm errorVm = new ErrorVm("500", "Internal Server Error", "Something went wrong");

        assertEquals("500", errorVm.statusCode());
    }

    @Test
    @DisplayName("Should access title component")
    void testTitleComponent() {
        ErrorVm errorVm = new ErrorVm("403", "Forbidden", "Access denied");

        assertEquals("Forbidden", errorVm.title());
    }

    @Test
    @DisplayName("Should access detail component")
    void testDetailComponent() {
        ErrorVm errorVm = new ErrorVm("401", "Unauthorized", "Authentication required");

        assertEquals("Authentication required", errorVm.detail());
    }

    @Test
    @DisplayName("Should access fieldErrors component")
    void testFieldErrorsComponent() {
        List<String> errors = new ArrayList<>();
        errors.add("Error 1");
        errors.add("Error 2");

        ErrorVm errorVm = new ErrorVm("422", "Unprocessable Entity", "Validation failed", errors);

        assertNotNull(errorVm.fieldErrors());
        assertEquals(2, errorVm.fieldErrors().size());
    }

    @Test
    @DisplayName("Should handle null statusCode")
    void testNullStatusCode() {
        ErrorVm errorVm = new ErrorVm(null, "Error", "Details");

        assertNull(errorVm.statusCode());
    }

    @Test
    @DisplayName("Should handle null title")
    void testNullTitle() {
        ErrorVm errorVm = new ErrorVm("400", null, "Details");

        assertNull(errorVm.title());
    }

    @Test
    @DisplayName("Should handle null detail")
    void testNullDetail() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", null);

        assertNull(errorVm.detail());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        ErrorVm errorVm = new ErrorVm("", "", "");

        assertEquals("", errorVm.statusCode());
        assertEquals("", errorVm.title());
        assertEquals("", errorVm.detail());
    }

    @Test
    @DisplayName("Should handle single field error")
    void testSingleFieldError() {
        List<String> errors = new ArrayList<>();
        errors.add("Email is required");

        ErrorVm errorVm = new ErrorVm("400", "Validation Error", "Email field is required", errors);

        assertEquals(1, errorVm.fieldErrors().size());
        assertEquals("Email is required", errorVm.fieldErrors().get(0));
    }

    @Test
    @DisplayName("Should handle multiple field errors")
    void testMultipleFieldErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("First name is required");
        errors.add("Last name is required");
        errors.add("Email must be valid");
        errors.add("Age must be at least 18");

        ErrorVm errorVm = new ErrorVm("422", "Validation Errors", "Multiple validation errors", errors);

        assertEquals(4, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("First name is required"));
        assertTrue(errorVm.fieldErrors().contains("Email must be valid"));
    }

    @Test
    @DisplayName("Should handle empty fieldErrors list")
    void testEmptyFieldErrors() {
        List<String> errors = new ArrayList<>();
        ErrorVm errorVm = new ErrorVm("500", "Server Error", "Internal error", errors);

        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    @DisplayName("Should implement equals for records")
    void testEqualsForRecords() {
        List<String> errors = new ArrayList<>();
        errors.add("Error 1");

        ErrorVm errorVm1 = new ErrorVm("400", "Bad Request", "Details", errors);
        ErrorVm errorVm2 = new ErrorVm("400", "Bad Request", "Details", errors);

        assertEquals(errorVm1, errorVm2);
    }

    @Test
    @DisplayName("Should implement equals for records with different content")
    void testNotEqualsForRecords() {
        ErrorVm errorVm1 = new ErrorVm("400", "Bad Request", "Details");
        ErrorVm errorVm2 = new ErrorVm("500", "Server Error", "Different details");

        assertNotEquals(errorVm1, errorVm2);
    }

    @Test
    @DisplayName("Should implement hashCode for records")
    void testHashCodeForRecords() {
        List<String> errors = new ArrayList<>();
        errors.add("Error 1");

        ErrorVm errorVm1 = new ErrorVm("400", "Bad Request", "Details", errors);
        ErrorVm errorVm2 = new ErrorVm("400", "Bad Request", "Details", errors);

        assertEquals(errorVm1.hashCode(), errorVm2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString for records")
    void testToStringForRecords() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Details");

        assertNotNull(errorVm.toString());
        assertTrue(errorVm.toString().contains("ErrorVm"));
    }

    @Test
    @DisplayName("Should handle common HTTP error status codes")
    void testCommonHttpErrorCodes() {
        // 400 Bad Request
        ErrorVm badRequest = new ErrorVm("400", "Bad Request", "Invalid request");
        assertEquals("400", badRequest.statusCode());

        // 401 Unauthorized
        ErrorVm unauthorized = new ErrorVm("401", "Unauthorized", "Authentication required");
        assertEquals("401", unauthorized.statusCode());

        // 403 Forbidden
        ErrorVm forbidden = new ErrorVm("403", "Forbidden", "Access denied");
        assertEquals("403", forbidden.statusCode());

        // 404 Not Found
        ErrorVm notFound = new ErrorVm("404", "Not Found", "Resource not found");
        assertEquals("404", notFound.statusCode());

        // 422 Unprocessable Entity
        ErrorVm unprocessable = new ErrorVm("422", "Unprocessable Entity", "Validation failed");
        assertEquals("422", unprocessable.statusCode());

        // 500 Internal Server Error
        ErrorVm serverError = new ErrorVm("500", "Internal Server Error", "Server error occurred");
        assertEquals("500", serverError.statusCode());
    }

    @Test
    @DisplayName("Should allow field errors modification when using mutable list")
    void testFieldErrorsModification() {
        List<String> errors = new ArrayList<>();
        errors.add("Error 1");

        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Details", errors);

        // Add another error to the original list
        errors.add("Error 2");

        // The error should be visible in errorVm.fieldErrors()
        assertEquals(2, errorVm.fieldErrors().size());
    }

    @Test
    @DisplayName("Should handle special characters in error messages")
    void testSpecialCharactersInErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("Field 'email@domain' is invalid");
        errors.add("Price must be > 0");
        errors.add("Name contains forbidden symbols: <>\"");

        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Details", errors);

        assertEquals(3, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("Field 'email@domain' is invalid"));
    }

    @Test
    @DisplayName("Should handle very long error messages")
    void testLongErrorMessages() {
        String longDetail = "This is a very long error message " + "x".repeat(1000);
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", longDetail);

        assertEquals(longDetail, errorVm.detail());
        assertTrue(errorVm.detail().length() > 1000);
    }

    @Test
    @DisplayName("Should handle error message with unicode characters")
    void testUnicodeCharactersInErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("Tên không hợp lệ");
        errors.add("Email không đúng định dạng");
        errors.add("年齢は無効です");

        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Validation failed", errors);

        assertEquals(3, errorVm.fieldErrors().size());
        assertTrue(errorVm.fieldErrors().contains("Tên không hợp lệ"));
    }
}
