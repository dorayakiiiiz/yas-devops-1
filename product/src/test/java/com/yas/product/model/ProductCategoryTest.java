package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductCategory Model Tests")
class ProductCategoryTest {

    private ProductCategory productCategory;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        productCategory = new ProductCategory();
        product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
    }

    @Test
    @DisplayName("Should create ProductCategory with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(productCategory);
        assertNull(productCategory.getId());
        assertNull(productCategory.getProduct());
        assertNull(productCategory.getCategory());
    }

    @Test
    @DisplayName("Should create ProductCategory with all-args constructor")
    void testAllArgsConstructor() {
        ProductCategory newProductCategory = new ProductCategory(1L, product, category, 1, true);

        assertEquals(1L, newProductCategory.getId());
        assertEquals(product, newProductCategory.getProduct());
        assertEquals(category, newProductCategory.getCategory());
        assertEquals(1, newProductCategory.getDisplayOrder());
        assertTrue(newProductCategory.isFeaturedProduct());
    }

    @Test
    @DisplayName("Should set and get productCategory properties")
    void testSettersAndGetters() {
        productCategory.setId(1L);
        productCategory.setProduct(product);
        productCategory.setCategory(category);
        productCategory.setDisplayOrder(1);
        productCategory.setFeaturedProduct(true);

        assertEquals(1L, productCategory.getId());
        assertEquals(product, productCategory.getProduct());
        assertEquals(category, productCategory.getCategory());
        assertEquals(1, productCategory.getDisplayOrder());
        assertTrue(productCategory.isFeaturedProduct());
    }

    @Test
    @DisplayName("Should set displayOrder to zero by default")
    void testDefaultDisplayOrder() {
        productCategory.setDisplayOrder(0);
        assertEquals(0, productCategory.getDisplayOrder());
    }

    @Test
    @DisplayName("Should set featured product flag")
    void testFeaturedProduct() {
        productCategory.setFeaturedProduct(true);
        assertTrue(productCategory.isFeaturedProduct());

        productCategory.setFeaturedProduct(false);
        assertFalse(productCategory.isFeaturedProduct());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        productCategory = ProductCategory.builder()
            .id(1L)
            .product(product)
            .category(category)
            .displayOrder(1)
            .isFeaturedProduct(true)
            .build();

        assertEquals(1L, productCategory.getId());
        assertEquals(product, productCategory.getProduct());
        assertEquals(category, productCategory.getCategory());
        assertEquals(1, productCategory.getDisplayOrder());
        assertTrue(productCategory.isFeaturedProduct());
    }
}
