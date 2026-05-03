package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Brand Model Tests")
class BrandTest {

    private Brand brand;

    @BeforeEach
    void setUp() {
        brand = new Brand();
    }

    @Test
    @DisplayName("Should create Brand with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(brand);
        assertNull(brand.getId());
        assertNull(brand.getName());
        assertNull(brand.getSlug());
    }

    @Test
    @DisplayName("Should set and get brand properties")
    void testSettersAndGetters() {
        brand.setId(1L);
        brand.setName("Nike");
        brand.setSlug("nike");
        brand.setPublished(true);

        assertEquals(1L, brand.getId());
        assertEquals("Nike", brand.getName());
        assertEquals("nike", brand.getSlug());
        assertTrue(brand.isPublished());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same brand")
    void testEqualsForSameBrand() {
        brand.setId(1L);
        brand.setName("Nike");

        Brand anotherBrand = new Brand();
        anotherBrand.setId(1L);
        anotherBrand.setName("Adidas");

        assertEquals(brand, anotherBrand);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different brands")
    void testEqualsForDifferentBrands() {
        brand.setId(1L);
        brand.setName("Nike");

        Brand anotherBrand = new Brand();
        anotherBrand.setId(2L);
        anotherBrand.setName("Adidas");

        assertNotEquals(brand, anotherBrand);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        brand.setId(null);
        brand.setName("Nike");

        Brand anotherBrand = new Brand();
        anotherBrand.setId(null);
        anotherBrand.setName("Adidas");

        assertNotEquals(brand, anotherBrand);
    }

    @Test
    @DisplayName("Should correctly implement equals method for self comparison")
    void testEqualsSelfComparison() {
        brand.setId(1L);
        assertEquals(brand, brand);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different types")
    void testEqualsForDifferentTypes() {
        brand.setId(1L);
        assertNotEquals(brand, "Nike");
        assertNotEquals(brand, null);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        brand.setId(1L);
        Brand anotherBrand = new Brand();
        anotherBrand.setId(1L);

        assertEquals(brand.hashCode(), anotherBrand.hashCode());
    }

    @Test
    @DisplayName("Should have same hashCode for different brands with same id")
    void testHashCodeForSameId() {
        brand.setId(1L);
        brand.setName("Nike");

        Brand anotherBrand = new Brand();
        anotherBrand.setId(1L);
        anotherBrand.setName("Adidas");

        assertEquals(brand.hashCode(), anotherBrand.hashCode());
    }
}
