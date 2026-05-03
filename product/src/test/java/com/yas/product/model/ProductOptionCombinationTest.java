package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductOptionCombination Model Tests")
class ProductOptionCombinationTest {

    private ProductOptionCombination combination;
    private Product product;
    private ProductOption productOption;

    @BeforeEach
    void setUp() {
        combination = new ProductOptionCombination();
        product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");

        productOption = new ProductOption();
        productOption.setId(1L);
        productOption.setName("Size");
    }

    @Test
    @DisplayName("Should create ProductOptionCombination with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(combination);
        assertNull(combination.getId());
        assertNull(combination.getProduct());
        assertNull(combination.getProductOption());
    }

    @Test
    @DisplayName("Should create ProductOptionCombination with all-args constructor")
    void testAllArgsConstructor() {
        ProductOptionCombination newCombination = new ProductOptionCombination(1L, product, productOption, 1, "10");

        assertEquals(1L, newCombination.getId());
        assertEquals(product, newCombination.getProduct());
        assertEquals(productOption, newCombination.getProductOption());
        assertEquals(1, newCombination.getDisplayOrder());
        assertEquals("10", newCombination.getValue());
    }

    @Test
    @DisplayName("Should set and get productOptionCombination properties")
    void testSettersAndGetters() {
        combination.setId(1L);
        combination.setProduct(product);
        combination.setProductOption(productOption);
        combination.setDisplayOrder(1);
        combination.setValue("10");

        assertEquals(1L, combination.getId());
        assertEquals(product, combination.getProduct());
        assertEquals(productOption, combination.getProductOption());
        assertEquals(1, combination.getDisplayOrder());
        assertEquals("10", combination.getValue());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        combination = ProductOptionCombination.builder()
            .id(1L)
            .product(product)
            .productOption(productOption)
            .displayOrder(1)
            .value("10")
            .build();

        assertEquals(1L, combination.getId());
        assertEquals(product, combination.getProduct());
        assertEquals(productOption, combination.getProductOption());
        assertEquals(1, combination.getDisplayOrder());
        assertEquals("10", combination.getValue());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same combination")
    void testEqualsForSameCombination() {
        combination.setId(1L);
        combination.setProduct(product);
        combination.setProductOption(productOption);

        ProductOptionCombination anotherCombination = new ProductOptionCombination();
        anotherCombination.setId(1L);
        anotherCombination.setProduct(product);
        anotherCombination.setProductOption(productOption);

        assertEquals(combination, anotherCombination);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different combinations")
    void testEqualsForDifferentCombinations() {
        combination.setId(1L);
        combination.setProduct(product);

        ProductOptionCombination anotherCombination = new ProductOptionCombination();
        anotherCombination.setId(2L);
        anotherCombination.setProduct(product);

        assertNotEquals(combination, anotherCombination);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        combination.setId(null);
        combination.setProduct(product);

        ProductOptionCombination anotherCombination = new ProductOptionCombination();
        anotherCombination.setId(null);
        anotherCombination.setProduct(product);

        assertNotEquals(combination, anotherCombination);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        combination.setId(1L);
        ProductOptionCombination anotherCombination = new ProductOptionCombination();
        anotherCombination.setId(1L);

        assertEquals(combination.hashCode(), anotherCombination.hashCode());
    }

    @Test
    @DisplayName("Should handle multiple combinations for same product")
    void testMultipleCombinationsForProduct() {
        ProductOptionCombination combination1 = ProductOptionCombination.builder()
            .id(1L)
            .product(product)
            .productOption(productOption)
            .value("10")
            .build();

        ProductOption colorOption = new ProductOption();
        colorOption.setId(2L);
        colorOption.setName("Color");

        ProductOptionCombination combination2 = ProductOptionCombination.builder()
            .id(2L)
            .product(product)
            .productOption(colorOption)
            .value("Blue")
            .build();

        assertEquals(product, combination1.getProduct());
        assertEquals(product, combination2.getProduct());
        assertNotEquals(combination1.getValue(), combination2.getValue());
    }

    @Test
    @DisplayName("Should handle different display orders")
    void testDisplayOrder() {
        combination.setDisplayOrder(0);
        assertEquals(0, combination.getDisplayOrder());

        combination.setDisplayOrder(5);
        assertEquals(5, combination.getDisplayOrder());

        combination.setDisplayOrder(100);
        assertEquals(100, combination.getDisplayOrder());
    }
}
