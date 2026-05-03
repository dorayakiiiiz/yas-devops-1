package com.yas.product.model.attribute;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductAttribute Model Tests")
class ProductAttributeTest {

    private ProductAttribute attribute;
    private ProductAttributeGroup attributeGroup;

    @BeforeEach
    void setUp() {
        attribute = new ProductAttribute();
        attributeGroup = new ProductAttributeGroup();
        attributeGroup.setId(1L);
        attributeGroup.setName("General");
    }

    @Test
    @DisplayName("Should create ProductAttribute with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(attribute);
        assertNull(attribute.getId());
        assertNull(attribute.getName());
    }

    @Test
    @DisplayName("Should create ProductAttribute with all-args constructor")
    void testAllArgsConstructor() {
        ProductAttribute newAttribute = new ProductAttribute(1L, "Color", attributeGroup, new ArrayList<>(), new ArrayList<>());

        assertEquals(1L, newAttribute.getId());
        assertEquals("Color", newAttribute.getName());
        assertEquals(attributeGroup, newAttribute.getProductAttributeGroup());
    }

    @Test
    @DisplayName("Should set and get productAttribute properties")
    void testSettersAndGetters() {
        attribute.setId(1L);
        attribute.setName("Color");
        attribute.setProductAttributeGroup(attributeGroup);

        assertEquals(1L, attribute.getId());
        assertEquals("Color", attribute.getName());
        assertEquals(attributeGroup, attribute.getProductAttributeGroup());
    }

    @Test
    @DisplayName("Should initialize empty collections")
    void testDefaultCollections() {
        assertNotNull(attribute.getProductAttributeTemplates());
        assertNotNull(attribute.getAttributeValues());
        assertEquals(0, attribute.getProductAttributeTemplates().size());
        assertEquals(0, attribute.getAttributeValues().size());
    }

    @Test
    @DisplayName("Should manage productAttributeTemplates")
    void testProductAttributeTemplates() {
        List<ProductAttributeTemplate> templates = new ArrayList<>();
        attribute.setProductAttributeTemplates(templates);

        assertEquals(templates, attribute.getProductAttributeTemplates());
    }

    @Test
    @DisplayName("Should manage attributeValues")
    void testAttributeValues() {
        List<ProductAttributeValue> values = new ArrayList<>();
        attribute.setAttributeValues(values);

        assertEquals(values, attribute.getAttributeValues());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same attribute")
    void testEqualsForSameAttribute() {
        attribute.setId(1L);
        attribute.setName("Color");

        ProductAttribute anotherAttribute = new ProductAttribute();
        anotherAttribute.setId(1L);
        anotherAttribute.setName("Size");

        assertEquals(attribute, anotherAttribute);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different attributes")
    void testEqualsForDifferentAttributes() {
        attribute.setId(1L);
        attribute.setName("Color");

        ProductAttribute anotherAttribute = new ProductAttribute();
        anotherAttribute.setId(2L);
        anotherAttribute.setName("Color");

        assertNotEquals(attribute, anotherAttribute);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        attribute.setId(null);
        attribute.setName("Color");

        ProductAttribute anotherAttribute = new ProductAttribute();
        anotherAttribute.setId(null);
        anotherAttribute.setName("Color");

        assertNotEquals(attribute, anotherAttribute);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        attribute.setId(1L);
        ProductAttribute anotherAttribute = new ProductAttribute();
        anotherAttribute.setId(1L);

        assertEquals(attribute.hashCode(), anotherAttribute.hashCode());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        attribute = ProductAttribute.builder()
            .id(1L)
            .name("Color")
            .productAttributeGroup(attributeGroup)
            .build();

        assertEquals(1L, attribute.getId());
        assertEquals("Color", attribute.getName());
        assertEquals(attributeGroup, attribute.getProductAttributeGroup());
    }
}
