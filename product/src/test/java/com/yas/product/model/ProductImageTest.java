package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductImage Model Tests")
class ProductImageTest {

    private ProductImage productImage;
    private Product product;

    @BeforeEach
    void setUp() {
        productImage = new ProductImage();
        product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
    }

    @Test
    @DisplayName("Should create ProductImage with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(productImage);
        assertNull(productImage.getId());
        assertNull(productImage.getImageId());
        assertNull(productImage.getProduct());
    }

    @Test
    @DisplayName("Should create ProductImage with all-args constructor")
    void testAllArgsConstructor() {
        ProductImage newProductImage = new ProductImage(1L, 100L, product);

        assertEquals(1L, newProductImage.getId());
        assertEquals(100L, newProductImage.getImageId());
        assertEquals(product, newProductImage.getProduct());
    }

    @Test
    @DisplayName("Should set and get productImage properties")
    void testSettersAndGetters() {
        productImage.setId(1L);
        productImage.setImageId(100L);
        productImage.setProduct(product);

        assertEquals(1L, productImage.getId());
        assertEquals(100L, productImage.getImageId());
        assertEquals(product, productImage.getProduct());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        productImage = ProductImage.builder()
            .id(1L)
            .imageId(100L)
            .product(product)
            .build();

        assertEquals(1L, productImage.getId());
        assertEquals(100L, productImage.getImageId());
        assertEquals(product, productImage.getProduct());
    }

    @Test
    @DisplayName("Should handle multiple images for same product")
    void testMultipleImagesForProduct() {
        ProductImage image1 = ProductImage.builder()
            .id(1L)
            .imageId(100L)
            .product(product)
            .build();

        ProductImage image2 = ProductImage.builder()
            .id(2L)
            .imageId(101L)
            .product(product)
            .build();

        assertEquals(product, image1.getProduct());
        assertEquals(product, image2.getProduct());
        assertNotEquals(image1.getImageId(), image2.getImageId());
    }

    @Test
    @DisplayName("Should handle null imageId")
    void testNullImageId() {
        productImage.setImageId(null);
        assertNull(productImage.getImageId());
    }

    @Test
    @DisplayName("Should handle null product")
    void testNullProduct() {
        productImage.setProduct(null);
        assertNull(productImage.getProduct());
    }
}
