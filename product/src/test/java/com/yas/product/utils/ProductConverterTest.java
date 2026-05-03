package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ProductConverterTest {

    /**
     * Test converting a simple product name to slug
     */
    @Test
    void testToSlug_WithSimpleProductName_Success() {
        // Arrange
        String input = "Laptop Computer";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("laptop-computer", result);
    }

    /**
     * Test converting product name with special characters to slug
     */
    @Test
    void testToSlug_WithSpecialCharacters_Success() {
        // Arrange
        String input = "Apple iPhone 13 Pro Max!";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("apple-iphone-13-pro-max", result);
    }

    /**
     * Test converting product name with multiple spaces to slug
     */
    @Test
    void testToSlug_WithMultipleSpaces_Success() {
        // Arrange
        String input = "Samsung   Galaxy   S21";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("samsung-galaxy-s21", result);
    }

    /**
     * Test converting product name with leading/trailing spaces to slug
     */
    @Test
    void testToSlug_WithLeadingTrailingSpaces_Success() {
        // Arrange
        String input = "  Sony Headphones  ";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("sony-headphones", result);
    }

    /**
     * Test converting product name with uppercase letters to slug
     */
    @Test
    void testToSlug_WithUppercaseLetters_Success() {
        // Arrange
        String input = "DELL XPS LAPTOP";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("dell-xps-laptop", result);
    }

    /**
     * Test converting product name with numbers to slug
     */
    @Test
    void testToSlug_WithNumbers_Success() {
        // Arrange
        String input = "RTX 3080 Ti Graphics Card";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("rtx-3080-ti-graphics-card", result);
    }

    /**
     * Test converting product name with hyphens to slug
     */
    @Test
    void testToSlug_WithHyphens_Success() {
        // Arrange
        String input = "Samsung Galaxy-S21-Ultra";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("samsung-galaxy-s21-ultra", result);
    }

    /**
     * Test converting product name with consecutive special characters
     */
    @Test
    void testToSlug_WithConsecutiveSpecialCharacters_Success() {
        // Arrange
        String input = "Product!!!Name@@@Test";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("product-name-test", result);
    }

    /**
     * Test converting product name with leading hyphens
     */
    @Test
    void testToSlug_WithLeadingHyphens_Success() {
        // Arrange
        String input = "---Product-Name";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        // Leading hyphens should be removed
        assertEquals("product-name", result);
    }

    /**
     * Test converting product name with unicode characters
     */
    @Test
    void testToSlug_WithUnicodeCharacters_Success() {
        // Arrange
        String input = "Product Name Café";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        // Unicode characters should be replaced with hyphens
        assertNotNull(result);
        assertEquals("product-name-caf-", result);
    }

    /**
     * Test converting single word product name to slug
     */
    @Test
    void testToSlug_WithSingleWord_Success() {
        // Arrange
        String input = "Laptop";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("laptop", result);
    }

    /**
     * Test converting product name with mixed case and numbers
     */
    @Test
    void testToSlug_WithMixedCaseAndNumbers_Success() {
        // Arrange
        String input = "iPad Pro 12.9 2021";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("ipad-pro-129-2021", result);
    }

    /**
     * Test converting product name with parentheses
     */
    @Test
    void testToSlug_WithParentheses_Success() {
        // Arrange
        String input = "Product (Premium Edition)";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("product-premium-edition", result);
    }

    /**
     * Test converting product name with ampersand symbol
     */
    @Test
    void testToSlug_WithAmpersand_Success() {
        // Arrange
        String input = "Product & Service";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("product-service", result);
    }

    /**
     * Test converting product name with underscores
     */
    @Test
    void testToSlug_WithUnderscores_Success() {
        // Arrange
        String input = "Product_Name_Test";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("product-name-test", result);
    }

    /**
     * Test converting empty product name to slug
     */
    @Test
    void testToSlug_WithEmptyString_Success() {
        // Arrange
        String input = "";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("", result);
    }

    /**
     * Test converting product name with only spaces to slug
     */
    @Test
    void testToSlug_WithOnlySpaces_Success() {
        // Arrange
        String input = "   ";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("", result);
    }

    /**
     * Test converting product name with mixed special characters and numbers
     */
    @Test
    void testToSlug_WithComplexMixture_Success() {
        // Arrange
        String input = "Dell-XPS 13!! @#$ Plus (2021)";
        
        // Act
        String result = ProductConverter.toSlug(input);
        
        // Assert
        assertEquals("dell-xps-13-plus-2021", result);
    }
}
