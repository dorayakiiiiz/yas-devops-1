package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Product Model Tests")
class ProductTest {

    private Product product;
    private Brand brand;

    @BeforeEach
    void setUp() {
        product = new Product();
        brand = new Brand();
        brand.setId(1L);
        brand.setName("Nike");
    }

    @Test
    @DisplayName("Should create Product with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getSku());
    }

    @Test
    @DisplayName("Should set and get product properties")
    void testSettersAndGetters() {
        product.setId(1L);
        product.setName("Running Shoes");
        product.setShortDescription("High quality running shoes");
        product.setDescription("Premium running shoes for athletes");
        product.setSpecification("Size: 10, Color: Blue");
        product.setSku("RUN-SHOE-001");
        product.setGtin("1234567890");
        product.setSlug("running-shoes");
        product.setPrice(99.99);
        product.setHasOptions(true);
        product.setAllowedToOrder(true);
        product.setPublished(true);
        product.setFeatured(true);
        product.setVisibleIndividually(true);
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(100L);
        product.setTaxClassId(1L);
        product.setMetaTitle("Running Shoes");
        product.setMetaKeyword("shoes,running");
        product.setMetaDescription("Buy running shoes");
        product.setThumbnailMediaId(100L);
        product.setWeight(0.5);
        product.setLength(30.0);
        product.setWidth(10.0);
        product.setHeight(12.0);
        product.setBrand(brand);
        product.setTaxIncluded(true);

        assertEquals(1L, product.getId());
        assertEquals("Running Shoes", product.getName());
        assertEquals("High quality running shoes", product.getShortDescription());
        assertEquals("Premium running shoes for athletes", product.getDescription());
        assertEquals("RUN-SHOE-001", product.getSku());
        assertEquals(99.99, product.getPrice());
        assertTrue(product.isHasOptions());
        assertTrue(product.isAllowedToOrder());
        assertTrue(product.isPublished());
        assertEquals(brand, product.getBrand());
    }

    @Test
    @DisplayName("Should initialize default collections")
    void testDefaultCollections() {
        assertNotNull(product.getRelatedProducts());
        assertNotNull(product.getProductCategories());
        assertNotNull(product.getAttributeValues());
        assertNotNull(product.getProductImages());
        assertNotNull(product.getProducts());

        assertEquals(0, product.getRelatedProducts().size());
        assertEquals(0, product.getProductCategories().size());
        assertEquals(0, product.getProducts().size());
    }

    @Test
    @DisplayName("Should manage related products")
    void testRelatedProducts() {
        ProductRelated related = new ProductRelated();
        related.setId(1L);

        product.getRelatedProducts().add(related);

        assertEquals(1, product.getRelatedProducts().size());
        assertEquals(related, product.getRelatedProducts().get(0));
    }

    @Test
    @DisplayName("Should manage product categories")
    void testProductCategories() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(1L);

        product.getProductCategories().add(productCategory);

        assertEquals(1, product.getProductCategories().size());
        assertEquals(productCategory, product.getProductCategories().get(0));
    }

    @Test
    @DisplayName("Should manage product images")
    void testProductImages() {
        ProductImage productImage = new ProductImage();
        productImage.setId(1L);
        productImage.setImageId(100L);

        product.getProductImages().add(productImage);

        assertEquals(1, product.getProductImages().size());
        assertEquals(productImage, product.getProductImages().get(0));
    }

    @Test
    @DisplayName("Should handle parent product relationship")
    void testParentProductRelationship() {
        Product parentProduct = new Product();
        parentProduct.setId(1L);
        parentProduct.setName("Parent Product");

        product.setId(2L);
        product.setName("Child Product");
        product.setParent(parentProduct);

        assertEquals(parentProduct, product.getParent());
    }

    @Test
    @DisplayName("Should manage child products")
    void testChildProducts() {
        Product childProduct = new Product();
        childProduct.setId(2L);
        childProduct.setName("Variant 1");

        product.setId(1L);
        product.setName("Parent Product");
        product.getProducts().add(childProduct);

        assertEquals(1, product.getProducts().size());
        assertEquals(childProduct, product.getProducts().get(0));
    }

    @Test
    @DisplayName("Should correctly implement equals method for same product")
    void testEqualsForSameProduct() {
        product.setId(1L);
        product.setName("Running Shoes");

        Product anotherProduct = new Product();
        anotherProduct.setId(1L);
        anotherProduct.setName("Different Name");

        assertEquals(product, anotherProduct);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different products")
    void testEqualsForDifferentProducts() {
        product.setId(1L);
        product.setName("Running Shoes");

        Product anotherProduct = new Product();
        anotherProduct.setId(2L);
        anotherProduct.setName("Running Shoes");

        assertNotEquals(product, anotherProduct);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        product.setId(null);

        Product anotherProduct = new Product();
        anotherProduct.setId(null);

        assertNotEquals(product, anotherProduct);
    }

    @Test
    @DisplayName("Should correctly implement equals method for self comparison")
    void testEqualsSelfComparison() {
        product.setId(1L);
        assertEquals(product, product);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different types")
    void testEqualsForDifferentTypes() {
        product.setId(1L);
        assertNotEquals(product, "Running Shoes");
        assertNotEquals(product, null);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        product.setId(1L);
        Product anotherProduct = new Product();
        anotherProduct.setId(1L);

        assertEquals(product.hashCode(), anotherProduct.hashCode());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        product = Product.builder()
            .id(1L)
            .name("Running Shoes")
            .sku("RUN-SHOE-001")
            .price(99.99)
            .isPublished(true)
            .brand(brand)
            .build();

        assertEquals(1L, product.getId());
        assertEquals("Running Shoes", product.getName());
        assertEquals("RUN-SHOE-001", product.getSku());
        assertEquals(99.99, product.getPrice());
        assertTrue(product.isPublished());
        assertEquals(brand, product.getBrand());
    }

    @Test
    @DisplayName("Should have non-null default related products from builder")
    void testBuilderDefaultRelatedProducts() {
        product = Product.builder()
            .id(1L)
            .name("Running Shoes")
            .build();

        assertNotNull(product.getRelatedProducts());
        assertEquals(0, product.getRelatedProducts().size());
    }

    @Test
    @DisplayName("Should handle enum type DimensionUnit")
    void testDimensionUnit() {
        product.setDimensionUnit(com.yas.product.model.enumeration.DimensionUnit.CM);
        assertEquals(com.yas.product.model.enumeration.DimensionUnit.CM, product.getDimensionUnit());
    }
}
