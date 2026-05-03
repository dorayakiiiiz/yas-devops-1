package com.yas.product.model.attribute;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductAttributeTemplate Model Tests")
class ProductAttributeTemplateTest {

    private ProductAttributeTemplate attributeTemplate;
    private ProductAttribute attribute;
    private ProductTemplate template;

    @BeforeEach
    void setUp() {
        attributeTemplate = new ProductAttributeTemplate();
        
        attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Color");

        template = new ProductTemplate();
        template.setId(1L);
        template.setName("Apparel");
    }

    @Test
    @DisplayName("Should create ProductAttributeTemplate with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(attributeTemplate);
        assertNull(attributeTemplate.getId());
        assertNull(attributeTemplate.getProductAttribute());
        assertNull(attributeTemplate.getProductTemplate());
    }

    @Test
    @DisplayName("Should create ProductAttributeTemplate with all-args constructor")
    void testAllArgsConstructor() {
        ProductAttributeTemplate newTemplate = new ProductAttributeTemplate(1L, attribute, template, 1);

        assertEquals(1L, newTemplate.getId());
        assertEquals(attribute, newTemplate.getProductAttribute());
        assertEquals(template, newTemplate.getProductTemplate());
        assertEquals(1, newTemplate.getDisplayOrder());
    }

    @Test
    @DisplayName("Should set and get productAttributeTemplate properties")
    void testSettersAndGetters() {
        attributeTemplate.setId(1L);
        attributeTemplate.setProductAttribute(attribute);
        attributeTemplate.setProductTemplate(template);
        attributeTemplate.setDisplayOrder(1);

        assertEquals(1L, attributeTemplate.getId());
        assertEquals(attribute, attributeTemplate.getProductAttribute());
        assertEquals(template, attributeTemplate.getProductTemplate());
        assertEquals(1, attributeTemplate.getDisplayOrder());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        attributeTemplate = ProductAttributeTemplate.builder()
            .id(1L)
            .productAttribute(attribute)
            .productTemplate(template)
            .displayOrder(1)
            .build();

        assertEquals(1L, attributeTemplate.getId());
        assertEquals(attribute, attributeTemplate.getProductAttribute());
        assertEquals(template, attributeTemplate.getProductTemplate());
        assertEquals(1, attributeTemplate.getDisplayOrder());
    }

    @Test
    @DisplayName("Should handle different display orders")
    void testDisplayOrder() {
        attributeTemplate.setDisplayOrder(0);
        assertEquals(0, attributeTemplate.getDisplayOrder());

        attributeTemplate.setDisplayOrder(5);
        assertEquals(5, attributeTemplate.getDisplayOrder());

        attributeTemplate.setDisplayOrder(100);
        assertEquals(100, attributeTemplate.getDisplayOrder());
    }

    @Test
    @DisplayName("Should manage attribute-template relationship")
    void testAttributeTemplateRelationship() {
        attributeTemplate.setProductAttribute(attribute);
        attributeTemplate.setProductTemplate(template);

        assertEquals(attribute, attributeTemplate.getProductAttribute());
        assertEquals(template, attributeTemplate.getProductTemplate());
    }

    @Test
    @DisplayName("Should handle multiple templates for same attribute")
    void testMultipleTemplatesForAttribute() {
        ProductAttributeTemplate template1 = ProductAttributeTemplate.builder()
            .id(1L)
            .productAttribute(attribute)
            .productTemplate(template)
            .displayOrder(1)
            .build();

        ProductTemplate template2 = new ProductTemplate();
        template2.setId(2L);
        template2.setName("Shoes");

        ProductAttributeTemplate template3 = ProductAttributeTemplate.builder()
            .id(2L)
            .productAttribute(attribute)
            .productTemplate(template2)
            .displayOrder(2)
            .build();

        assertEquals(attribute, template1.getProductAttribute());
        assertEquals(attribute, template3.getProductAttribute());
        assertNotEquals(template1.getProductTemplate(), template3.getProductTemplate());
    }

    @Test
    @DisplayName("Should handle multiple attributes in same template")
    void testMultipleAttributesInTemplate() {
        ProductAttributeTemplate attributeTemplate1 = ProductAttributeTemplate.builder()
            .id(1L)
            .productAttribute(attribute)
            .productTemplate(template)
            .displayOrder(1)
            .build();

        ProductAttribute sizeAttribute = new ProductAttribute();
        sizeAttribute.setId(2L);
        sizeAttribute.setName("Size");

        ProductAttributeTemplate attributeTemplate2 = ProductAttributeTemplate.builder()
            .id(2L)
            .productAttribute(sizeAttribute)
            .productTemplate(template)
            .displayOrder(2)
            .build();

        assertEquals(template, attributeTemplate1.getProductTemplate());
        assertEquals(template, attributeTemplate2.getProductTemplate());
        assertNotEquals(attributeTemplate1.getProductAttribute(), attributeTemplate2.getProductAttribute());
    }
}
