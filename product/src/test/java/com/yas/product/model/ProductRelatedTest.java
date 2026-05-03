package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductRelated Model Tests")
class ProductRelatedTest {

    private ProductRelated productRelated;
    private Product mainProduct;
    private Product relatedProduct;

    @BeforeEach
    void setUp() {
        productRelated = new ProductRelated();
        
        mainProduct = new Product();
        mainProduct.setId(1L);
        mainProduct.setName("Running Shoes");

        relatedProduct = new Product();
        relatedProduct.setId(2L);
        relatedProduct.setName("Running Socks");
    }

    @Test
    @DisplayName("Should create ProductRelated with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(productRelated);
        assertNull(productRelated.getId());
        assertNull(productRelated.getProduct());
        assertNull(productRelated.getRelatedProduct());
    }

    @Test
    @DisplayName("Should create ProductRelated with all-args constructor")
    void testAllArgsConstructor() {
        ProductRelated newRelated = new ProductRelated(1L, mainProduct, relatedProduct);

        assertEquals(1L, newRelated.getId());
        assertEquals(mainProduct, newRelated.getProduct());
        assertEquals(relatedProduct, newRelated.getRelatedProduct());
    }

    @Test
    @DisplayName("Should set and get productRelated properties")
    void testSettersAndGetters() {
        productRelated.setId(1L);
        productRelated.setProduct(mainProduct);
        productRelated.setRelatedProduct(relatedProduct);

        assertEquals(1L, productRelated.getId());
        assertEquals(mainProduct, productRelated.getProduct());
        assertEquals(relatedProduct, productRelated.getRelatedProduct());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        productRelated = ProductRelated.builder()
            .id(1L)
            .product(mainProduct)
            .relatedProduct(relatedProduct)
            .build();

        assertEquals(1L, productRelated.getId());
        assertEquals(mainProduct, productRelated.getProduct());
        assertEquals(relatedProduct, productRelated.getRelatedProduct());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same relation")
    void testEqualsForSameRelation() {
        productRelated.setId(1L);
        productRelated.setProduct(mainProduct);
        productRelated.setRelatedProduct(relatedProduct);

        ProductRelated anotherRelated = new ProductRelated();
        anotherRelated.setId(1L);
        anotherRelated.setProduct(mainProduct);
        anotherRelated.setRelatedProduct(relatedProduct);

        assertEquals(productRelated, anotherRelated);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different relations")
    void testEqualsForDifferentRelations() {
        productRelated.setId(1L);
        productRelated.setProduct(mainProduct);

        ProductRelated anotherRelated = new ProductRelated();
        anotherRelated.setId(2L);
        anotherRelated.setProduct(mainProduct);

        assertNotEquals(productRelated, anotherRelated);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        productRelated.setId(null);
        productRelated.setProduct(mainProduct);

        ProductRelated anotherRelated = new ProductRelated();
        anotherRelated.setId(null);
        anotherRelated.setProduct(mainProduct);

        assertNotEquals(productRelated, anotherRelated);
    }

    @Test
    @DisplayName("Should correctly implement equals method for self comparison")
    void testEqualsSelfComparison() {
        productRelated.setId(1L);
        assertEquals(productRelated, productRelated);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different types")
    void testEqualsForDifferentTypes() {
        productRelated.setId(1L);
        assertNotEquals(productRelated, "Related");
        assertNotEquals(productRelated, null);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        productRelated.setId(1L);
        ProductRelated anotherRelated = new ProductRelated();
        anotherRelated.setId(1L);

        assertEquals(productRelated.hashCode(), anotherRelated.hashCode());
    }

    @Test
    @DisplayName("Should handle multiple related products")
    void testMultipleRelatedProducts() {
        ProductRelated related1 = ProductRelated.builder()
            .id(1L)
            .product(mainProduct)
            .relatedProduct(relatedProduct)
            .build();

        Product anotherRelatedProduct = new Product();
        anotherRelatedProduct.setId(3L);
        anotherRelatedProduct.setName("Running Jacket");

        ProductRelated related2 = ProductRelated.builder()
            .id(2L)
            .product(mainProduct)
            .relatedProduct(anotherRelatedProduct)
            .build();

        assertEquals(mainProduct, related1.getProduct());
        assertEquals(mainProduct, related2.getProduct());
        assertNotEquals(related1.getRelatedProduct(), related2.getRelatedProduct());
    }
}
