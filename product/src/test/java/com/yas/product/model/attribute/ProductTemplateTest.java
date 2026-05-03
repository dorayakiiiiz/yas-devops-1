package com.yas.product.model.attribute;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductTemplate Model Tests")
class ProductTemplateTest {

    private ProductTemplate template;

    @BeforeEach
    void setUp() {
        template = new ProductTemplate();
    }

    @Test
    @DisplayName("Should create ProductTemplate with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(template);
        assertNull(template.getId());
        assertNull(template.getName());
    }

    @Test
    @DisplayName("Should create ProductTemplate with all-args constructor")
    void testAllArgsConstructor() {
        List<ProductAttributeTemplate> attributeTemplates = new ArrayList<>();
        ProductTemplate newTemplate = new ProductTemplate(1L, "Apparel", attributeTemplates);

        assertEquals(1L, newTemplate.getId());
        assertEquals("Apparel", newTemplate.getName());
        assertEquals(attributeTemplates, newTemplate.getProductAttributeTemplates());
    }

    @Test
    @DisplayName("Should set and get productTemplate properties")
    void testSettersAndGetters() {
        template.setId(1L);
        template.setName("Apparel");

        assertEquals(1L, template.getId());
        assertEquals("Apparel", template.getName());
    }

    @Test
    @DisplayName("Should initialize empty productAttributeTemplates list")
    void testDefaultProductAttributeTemplates() {
        assertNotNull(template.getProductAttributeTemplates());
        assertEquals(0, template.getProductAttributeTemplates().size());
    }

    @Test
    @DisplayName("Should manage productAttributeTemplates")
    void testProductAttributeTemplates() {
        List<ProductAttributeTemplate> attributeTemplates = new ArrayList<>();
        template.setProductAttributeTemplates(attributeTemplates);

        assertEquals(attributeTemplates, template.getProductAttributeTemplates());
    }

    @Test
    @DisplayName("Should add productAttributeTemplate to list")
    void testAddProductAttributeTemplate() {
        ProductAttributeTemplate attributeTemplate = new ProductAttributeTemplate();
        attributeTemplate.setId(1L);

        template.getProductAttributeTemplates().add(attributeTemplate);

        assertEquals(1, template.getProductAttributeTemplates().size());
        assertEquals(attributeTemplate, template.getProductAttributeTemplates().get(0));
    }

    @Test
    @DisplayName("Should correctly implement equals method for same template")
    void testEqualsForSameTemplate() {
        template.setId(1L);
        template.setName("Apparel");

        ProductTemplate anotherTemplate = new ProductTemplate();
        anotherTemplate.setId(1L);
        anotherTemplate.setName("Shoes");

        assertEquals(template, anotherTemplate);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different templates")
    void testEqualsForDifferentTemplates() {
        template.setId(1L);
        template.setName("Apparel");

        ProductTemplate anotherTemplate = new ProductTemplate();
        anotherTemplate.setId(2L);
        anotherTemplate.setName("Apparel");

        assertNotEquals(template, anotherTemplate);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        template.setId(null);
        template.setName("Apparel");

        ProductTemplate anotherTemplate = new ProductTemplate();
        anotherTemplate.setId(null);
        anotherTemplate.setName("Apparel");

        assertNotEquals(template, anotherTemplate);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        template.setId(1L);
        ProductTemplate anotherTemplate = new ProductTemplate();
        anotherTemplate.setId(1L);

        assertEquals(template.hashCode(), anotherTemplate.hashCode());
    }

    @Test
    @DisplayName("Should use builder pattern")
    void testBuilderPattern() {
        template = ProductTemplate.builder()
            .id(1L)
            .name("Apparel")
            .build();

        assertEquals(1L, template.getId());
        assertEquals("Apparel", template.getName());
    }

    @Test
    @DisplayName("Should have non-null default productAttributeTemplates from builder")
    void testBuilderDefaultProductAttributeTemplates() {
        template = ProductTemplate.builder()
            .id(1L)
            .name("Apparel")
            .build();

        assertNotNull(template.getProductAttributeTemplates());
        assertEquals(0, template.getProductAttributeTemplates().size());
    }

    @Test
    @DisplayName("Should handle common product templates")
    void testCommonProductTemplates() {
        template.setId(1L);
        template.setName("Apparel");
        assertEquals("Apparel", template.getName());

        template.setName("Shoes");
        assertEquals("Shoes", template.getName());

        template.setName("Electronics");
        assertEquals("Electronics", template.getName());
    }
}
