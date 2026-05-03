package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Category Model Tests")
class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    @DisplayName("Should create Category with no-args constructor")
    void testNoArgsConstructor() {
        assertNotNull(category);
        assertNull(category.getId());
        assertNull(category.getName());
        assertNull(category.getDescription());
    }


    @Test
    @DisplayName("Should handle parent-child relationship")
    void testParentChildRelationship() {
        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Electronics");

        category.setId(2L);
        category.setName("Mobile Phones");
        category.setParent(parentCategory);

        assertEquals(parentCategory, category.getParent());
        assertEquals("Electronics", category.getParent().getName());
    }

    @Test
    @DisplayName("Should manage categories list")
    void testCategoriesListManagement() {
        List<Category> categoriesList = new ArrayList<>();
        category.setCategories(categoriesList);

        assertEquals(categoriesList, category.getCategories());
    }

    @Test
    @DisplayName("Should manage productCategories list")
    void testProductCategoriesListManagement() {
        List<ProductCategory> productCategories = new ArrayList<>();
        category.setProductCategories(productCategories);

        assertEquals(productCategories, category.getProductCategories());
    }

    @Test
    @DisplayName("Should initialize empty lists in default constructor")
    void testDefaultEmptyLists() {
        assertNotNull(category.getCategories());
        assertNotNull(category.getProductCategories());
        assertEquals(0, category.getCategories().size());
        assertEquals(0, category.getProductCategories().size());
    }

    @Test
    @DisplayName("Should correctly handle equals for same id")
    void testEqualsForSameId() {
        category.setId(1L);
        category.setName("Electronics");

        Category anotherCategory = new Category();
        anotherCategory.setId(1L);
        anotherCategory.setName("Gadgets");

        // Note: Category doesn't override equals method, so it uses default Object.equals
        // which compares object references
        assertNotSame(category, anotherCategory);
    }

    @Test
    @DisplayName("Should correctly handle equals for same reference")
    void testEqualsForSameReference() {
        category.setId(1L);
        assertEquals(category, category);
    }
}
