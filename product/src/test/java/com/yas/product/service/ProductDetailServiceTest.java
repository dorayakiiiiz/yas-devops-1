package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.constant.Constants;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    /**
     * Test getting product detail by valid product ID
     */
    @Test
    void testGetProductDetailById_WithValidId_Success() {
        // Arrange
        Long productId = 1L;
        Product product = createTestProduct(productId, "Test Product", "test-product", true);
        
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        product.setBrand(brand);

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        productCategory.setProduct(product);
        
        product.setProductCategories(List.of(productCategory));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByParentProductId(anyLong()))
                .thenReturn(new ArrayList<>());

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("Test Product", result.name());
        assertEquals("test-product", result.slug());
        verify(productRepository).findById(productId);
    }

    /**
     * Test getting product detail with non-published product
     */
    @Test
    void testGetProductDetailById_WithUnpublishedProduct_ThrowsNotFoundException() {
        // Arrange
        Long productId = 1L;
        Product unpublishedProduct = createTestProduct(productId, "Test Product", "test-product", false);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(unpublishedProduct));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> productDetailService.getProductDetailById(productId));
        assertEquals(Constants.ErrorCode.PRODUCT_NOT_FOUND, exception.getMessage());
    }

    /**
     * Test getting product detail with non-existent product
     */
    @Test
    void testGetProductDetailById_WithNonExistentId_ThrowsNotFoundException() {
        // Arrange
        Long productId = 999L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
                () -> productDetailService.getProductDetailById(productId));
        assertEquals(Constants.ErrorCode.PRODUCT_NOT_FOUND, exception.getMessage());
    }

    /**
     * Test getting product detail with null brand
     */
    @Test
    void testGetProductDetailById_WithNullBrand_Success() {
        // Arrange
        Long productId = 1L;
        Product product = createTestProduct(productId, "Test Product", "test-product", true);
        product.setBrand(null);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByParentProductId(anyLong()))
                .thenReturn(new ArrayList<>());

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
    }

    /**
     * Test getting product detail with null categories
     */
    @Test
    void testGetProductDetailById_WithNullCategories_Success() {
        // Arrange
        Long productId = 1L;
        Product product = createTestProduct(productId, "Test Product", "test-product", true);
        product.setProductCategories(null);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByParentProductId(anyLong()))
                .thenReturn(new ArrayList<>());

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
    }

    /**
     * Test getting product detail with product variations
     */
    @Test
    void testGetProductDetailById_WithVariations_Success() {
        // Arrange
        Long productId = 1L;
        Product mainProduct = createTestProduct(productId, "Test Product", "test-product", true);

        Long variationId = 2L;
        Product variation = createTestProduct(variationId, "Test Product Variation", "test-product-variation", true);
        variation.setParent(mainProduct);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(mainProduct));
        when(productOptionCombinationRepository.findAllByParentProductId(productId))
                .thenReturn(new ArrayList<>());

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
        verify(productOptionCombinationRepository).findAllByParentProductId(productId);
    }

    /**
     * Test getting product detail with multiple categories
     */
    @Test
    void testGetProductDetailById_WithMultipleCategories_Success() {
        // Arrange
        Long productId = 1L;
        Product product = createTestProduct(productId, "Test Product", "test-product", true);

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setCategory(category1);
        productCategory1.setProduct(product);

        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setCategory(category2);
        productCategory2.setProduct(product);

        product.setProductCategories(List.of(productCategory1, productCategory2));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByParentProductId(anyLong()))
                .thenReturn(new ArrayList<>());

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
    }

    /**
     * Test getting product detail with product combinations
     */
    @Test
    void testGetProductDetailById_WithProductOptionCombinations_Success() {
        // Arrange
        Long productId = 1L;
        Product product = createTestProduct(productId, "Test Product", "test-product", true);

        ProductOptionCombination combination1 = new ProductOptionCombination();
        combination1.setId(1L);
        combination1.setValue("Size-M");

        ProductOptionCombination combination2 = new ProductOptionCombination();
        combination2.setId(2L);
        combination2.setValue("Color-Red");

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByParentProductId(productId))
                .thenReturn(List.of(combination1, combination2));

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.id());
        verify(productOptionCombinationRepository).findAllByParentProductId(productId);
    }

    // Helper methods
    private Product createTestProduct(Long id, String name, String slug, boolean isPublished) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSlug(slug);
        product.setPublished(isPublished);
        product.setPrice(99.99);
        product.setProductCategories(new ArrayList<>());
        return product;
    }
}
