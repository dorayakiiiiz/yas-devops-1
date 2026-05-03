package com.yas.product.model.attribute;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductAttributeGroup Model Tests")
class ProductAttributeGroupTest {

    private ProductAttributeGroup attributeGroup;

    @BeforeEach
    void setUp() {
        attributeGroup = new ProductAttributeGroup();
    }

    @Test
    @DisplayName("Should create ProductAttributeGroup with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(attributeGroup);
        assertNull(attributeGroup.getId());
        assertNull(attributeGroup.getName());
    }

    @Test
    @DisplayName("Should set and get productAttributeGroup properties")
    void testSettersAndGetters() {
        attributeGroup.setId(1L);
        attributeGroup.setName("General");

        assertEquals(1L, attributeGroup.getId());
        assertEquals("General", attributeGroup.getName());
    }

    @Test
    @DisplayName("Should correctly implement equals method for same group")
    void testEqualsForSameGroup() {
        attributeGroup.setId(1L);
        attributeGroup.setName("General");

        ProductAttributeGroup anotherGroup = new ProductAttributeGroup();
        anotherGroup.setId(1L);
        anotherGroup.setName("Different Name");

        assertEquals(attributeGroup, anotherGroup);
    }

    @Test
    @DisplayName("Should correctly implement equals method for different groups")
    void testEqualsForDifferentGroups() {
        attributeGroup.setId(1L);
        attributeGroup.setName("General");

        ProductAttributeGroup anotherGroup = new ProductAttributeGroup();
        anotherGroup.setId(2L);
        anotherGroup.setName("General");

        assertNotEquals(attributeGroup, anotherGroup);
    }

    @Test
    @DisplayName("Should correctly implement equals method for null id")
    void testEqualsForNullId() {
        attributeGroup.setId(null);
        attributeGroup.setName("General");

        ProductAttributeGroup anotherGroup = new ProductAttributeGroup();
        anotherGroup.setId(null);
        anotherGroup.setName("General");

        assertNotEquals(attributeGroup, anotherGroup);
    }

    @Test
    @DisplayName("Should correctly implement hashCode")
    void testHashCode() {
        attributeGroup.setId(1L);
        ProductAttributeGroup anotherGroup = new ProductAttributeGroup();
        anotherGroup.setId(1L);

        assertEquals(attributeGroup.hashCode(), anotherGroup.hashCode());
    }

    @Test
    @DisplayName("Should handle common attribute groups")
    void testCommonAttributeGroups() {
        attributeGroup.setId(1L);
        attributeGroup.setName("General");
        assertEquals("General", attributeGroup.getName());

        attributeGroup.setName("Specifications");
        assertEquals("Specifications", attributeGroup.getName());

        attributeGroup.setName("Technical");
        assertEquals("Technical", attributeGroup.getName());
    }
}
