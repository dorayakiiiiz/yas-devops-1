package com.yas.product.model.attribute;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductAttributeValue Model Tests")
class ProductAttributeValueTest {

    private ProductAttributeValue attributeValue;
    private Product product;
    private ProductAttribute attribute;

    @BeforeEach
    void setUp() {
        attributeValue = new ProductAttributeValue();
        product = new Product();
        product.setId(1L);
        product.setName("T-Shirt");

        attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Color");
    }

    @Test
    @DisplayName("Should create ProductAttributeValue with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(attributeValue);
        assertNull(attributeValue.getId());
        assertNull(attributeValue.getProduct());
        assertNull(attributeValue.getProductAttribute());
    }

    @Test
    @DisplayName("Should set and get productAttributeValue properties")
    void testSettersAndGetters() {
        attributeValue.setId(1L);
        attributeValue.setProduct(product);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Red");

        assertEquals(1L, attributeValue.getId());
        assertEquals(product, attributeValue.getProduct());
        assertEquals(attribute, attributeValue.getProductAttribute());
        assertEquals("Red", attributeValue.getValue());
    }

    @Test
    @DisplayName("Should handle different attribute values")
    void testDifferentValues() {
        attributeValue.setId(1L);
        attributeValue.setProduct(product);
        attributeValue.setProductAttribute(attribute);

        attributeValue.setValue("Red");
        assertEquals("Red", attributeValue.getValue());

        attributeValue.setValue("Blue");
        assertEquals("Blue", attributeValue.getValue());

        attributeValue.setValue("Green");
        assertEquals("Green", attributeValue.getValue());
    }

    @Test
    @DisplayName("Should handle null value")
    void testNullValue() {
        attributeValue.setValue(null);
        assertNull(attributeValue.getValue());
    }

    @Test
    @DisplayName("Should handle empty value")
    void testEmptyValue() {
        attributeValue.setValue("");
        assertEquals("", attributeValue.getValue());
    }

    @Test
    @DisplayName("Should handle product relationship")
    void testProductRelationship() {
        attributeValue.setProduct(product);

        assertEquals(product, attributeValue.getProduct());
        assertEquals(1L, attributeValue.getProduct().getId());
    }

    @Test
    @DisplayName("Should handle attribute relationship")
    void testAttributeRelationship() {
        attributeValue.setProductAttribute(attribute);

        assertEquals(attribute, attributeValue.getProductAttribute());
        assertEquals(1L, attributeValue.getProductAttribute().getId());
    }

    @Test
    @DisplayName("Should manage multiple attribute values for same product")
    void testMultipleValuesForProduct() {
        ProductAttributeValue colorValue = new ProductAttributeValue();
        colorValue.setId(1L);
        colorValue.setProduct(product);
        colorValue.setValue("Red");

        ProductAttribute sizeAttribute = new ProductAttribute();
        sizeAttribute.setId(2L);
        sizeAttribute.setName("Size");

        ProductAttributeValue sizeValue = new ProductAttributeValue();
        sizeValue.setId(2L);
        sizeValue.setProduct(product);
        sizeValue.setProductAttribute(sizeAttribute);
        sizeValue.setValue("Large");

        assertEquals(product, colorValue.getProduct());
        assertEquals(product, sizeValue.getProduct());
        assertNotEquals(colorValue.getValue(), sizeValue.getValue());
    }

    @Test
    @DisplayName("Should handle special characters in values")
    void testSpecialCharactersInValues() {
        attributeValue.setValue("Red/Blue");
        assertEquals("Red/Blue", attributeValue.getValue());

        attributeValue.setValue("100% Cotton");
        assertEquals("100% Cotton", attributeValue.getValue());

        attributeValue.setValue("Size: L (Large)");
        assertEquals("Size: L (Large)", attributeValue.getValue());
    }
}
