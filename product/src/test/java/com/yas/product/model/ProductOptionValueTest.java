package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductOptionValue Model Tests")
class ProductOptionValueTest {

    private ProductOptionValue optionValue;
    private Product product;
    private ProductOption productOption;

    @BeforeEach
    void setUp() {
        optionValue = new ProductOptionValue();
        product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");

        productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setName("Size");
    }

    @Test
    @DisplayName("Should create ProductOptionValue with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(optionValue);
        assertNull(optionValue.getId());
        assertNull(optionValue.getProduct());
        assertNull(optionValue.getProductOption());
    }

    @Test
    @DisplayName("Should create ProductOptionValue with all-args constructor")
    void testAllArgsConstructor() {
        ProductOptionValue newOptionValue = new ProductOptionValue(1L, product, productOption, "dropdown", 1, "10");

        assertEquals(1L, newOptionValue.getId());
        assertEquals(product, newOptionValue.getProduct());
        assertEquals(productOption, newOptionValue.getProductOption());
        assertEquals("dropdown", newOptionValue.getDisplayType());
        assertEquals(1, newOptionValue.getDisplayOrder());
        assertEquals("10", newOptionValue.getValue());
    }

    @Test
    @DisplayName("Should set and get productOptionValue properties")
    void testSettersAndGetters() {
        optionValue.setId(1L);
        optionValue.setProduct(product);
        optionValue.setProductOption(productOption);
        optionValue.setDisplayType("dropdown");
        optionValue.setDisplayOrder(1);
        optionValue.setValue("10");

        assertEquals(1L, optionValue.getId());
        assertEquals(product, optionValue.getProduct());
        assertEquals(productOption, optionValue.getProductOption());
        assertEquals("dropdown", optionValue.getDisplayType());
        assertEquals(1, optionValue.getDisplayOrder());
        assertEquals("10", optionValue.getValue());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        optionValue = ProductOptionValue.builder()
            .id(1L)
            .product(product)
            .productOption(productOption)
            .displayType("dropdown")
            .displayOrder(1)
            .value("10")
            .build();

        assertEquals(1L, optionValue.getId());
        assertEquals(product, optionValue.getProduct());
        assertEquals(productOption, optionValue.getProductOption());
        assertEquals("dropdown", optionValue.getDisplayType());
        assertEquals(1, optionValue.getDisplayOrder());
        assertEquals("10", optionValue.getValue());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same option value")
    void testEqualsForSameOptionValue() {
        optionValue.setId(1L);
        optionValue.setProduct(product);

        ProductOptionValue anotherOptionValue = new ProductOptionValue();
        anotherOptionValue.setId(1L);
        anotherOptionValue.setProduct(product);

        assertEquals(optionValue, anotherOptionValue);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different option values")
    void testEqualsForDifferentOptionValues() {
        optionValue.setId(1L);
        optionValue.setProduct(product);

        ProductOptionValue anotherOptionValue = new ProductOptionValue();
        anotherOptionValue.setId(2L);
        anotherOptionValue.setProduct(product);

        assertNotEquals(optionValue, anotherOptionValue);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        optionValue.setId(null);
        optionValue.setProduct(product);

        ProductOptionValue anotherOptionValue = new ProductOptionValue();
        anotherOptionValue.setId(null);
        anotherOptionValue.setProduct(product);

        assertNotEquals(optionValue, anotherOptionValue);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        optionValue.setId(1L);
        ProductOptionValue anotherOptionValue = new ProductOptionValue();
        anotherOptionValue.setId(1L);

        assertEquals(optionValue.hashCode(), anotherOptionValue.hashCode());
    }

    @Test
    @DisplayName("Should handle different display types")
    void testDisplayTypes() {
        optionValue.setDisplayType("dropdown");
        assertEquals("dropdown", optionValue.getDisplayType());

        optionValue.setDisplayType("radio");
        assertEquals("radio", optionValue.getDisplayType());

        optionValue.setDisplayType("checkbox");
        assertEquals("checkbox", optionValue.getDisplayType());
    }

    @Test
    @DisplayName("Should handle different values for same option")
    void testMultipleValuesForOption() {
        ProductOptionValue value1 = ProductOptionValue.builder()
            .id(1L)
            .product(product)
            .productOption(productOption)
            .value("10")
            .displayOrder(1)
            .build();

        ProductOptionValue value2 = ProductOptionValue.builder()
            .id(2L)
            .product(product)
            .productOption(productOption)
            .value("12")
            .displayOrder(2)
            .build();

        assertEquals(product, value1.getProduct());
        assertEquals(product, value2.getProduct());
        assertEquals(productOption, value1.getProductOption());
        assertEquals(productOption, value2.getProductOption());
        assertNotEquals(value1.getValue(), value2.getValue());
    }
}
