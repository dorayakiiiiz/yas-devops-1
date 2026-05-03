package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductOption Model Tests")
class ProductOptionTest {

    private ProductOption productOption;

    @BeforeEach
    void setUp() {
        productOption = new ProductOption();
    }

    @Test
    @DisplayName("Should create ProductOption with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(productOption);
        assertNull(productOption.getId());
        assertNull(productOption.getName());
    }

    @Test
    @DisplayName("Should set and get productOption properties")
    void testSettersAndGetters() {
        productOption.setId(1L);
        productOption.setName("Size");

        assertEquals(1L, productOption.getId());
        assertEquals("Size", productOption.getName());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same option")
    void testEqualsForSameOption() {
        productOption.setId(1L);
        productOption.setName("Size");

        ProductOption anotherOption = new ProductOption();
        anotherOption.setId(1L);
        anotherOption.setName("Color");

        assertEquals(productOption, anotherOption);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different options")
    void testEqualsForDifferentOptions() {
        productOption.setId(1L);
        productOption.setName("Size");

        ProductOption anotherOption = new ProductOption();
        anotherOption.setId(2L);
        anotherOption.setName("Color");

        assertNotEquals(productOption, anotherOption);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        productOption.setId(null);
        productOption.setName("Size");

        ProductOption anotherOption = new ProductOption();
        anotherOption.setId(null);
        anotherOption.setName("Color");

        assertNotEquals(productOption, anotherOption);
    }

    @Test
    @DisplayName("Should correctly implement equals method for self comparison")
    void testEqualsSelfComparison() {
        productOption.setId(1L);
        assertEquals(productOption, productOption);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different types")
    void testEqualsForDifferentTypes() {
        productOption.setId(1L);
        assertNotEquals(productOption, "Size");
        assertNotEquals(productOption, null);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        productOption.setId(1L);
        ProductOption anotherOption = new ProductOption();
        anotherOption.setId(1L);

        assertEquals(productOption.hashCode(), anotherOption.hashCode());
    }

    @Test
    @DisplayName("Should have same hashCode for different options with same id")
    void testHashCodeForSameId() {
        productOption.setId(1L);
        productOption.setName("Size");

        ProductOption anotherOption = new ProductOption();
        anotherOption.setId(1L);
        anotherOption.setName("Color");

        assertEquals(productOption.hashCode(), anotherOption.hashCode());
    }

    @Test
    @DisplayName("Should handle common product options")
    void testCommonProductOptions() {
        productOption.setId(1L);
        productOption.setName("Size");

        assertEquals("Size", productOption.getName());

        productOption.setName("Color");
        assertEquals("Color", productOption.getName());

        productOption.setName("Material");
        assertEquals("Material", productOption.getName());
    }
}
