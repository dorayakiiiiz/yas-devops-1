package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.IntegrationTestConfiguration;
import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
class ProductServiceEdgeCasesIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductOptionValueRepository productOptionValueRepository;

    @Autowired
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @MockitoBean
    private MediaService mediaService;

    private Brand testBrand;
    private Category testCategory;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {
        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
        when(mediaService.getMedia(1L)).thenReturn(noFileMediaVm);
        initializeTestData();
    }

    private void initializeTestData() {
        testBrand = new Brand();
        testBrand.setName("Test Brand");
        testBrand.setSlug("test-brand");
        testBrand.setPublished(true);
        testBrand = brandRepository.save(testBrand);

        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setSlug("test-category");
        testCategory.setDisplayOrder((short) 1);
        testCategory = categoryRepository.save(testCategory);
    }

    @AfterEach
    void tearDown() {
        productOptionCombinationRepository.deleteAll();
        productOptionValueRepository.deleteAll();
        productOptionRepository.deleteAll();
        productImageRepository.deleteAll();
        productCategoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
    }

    /**
     * Test creating product with minimum required fields
     */
    @DisplayName("Create product with minimum required fields - Success")
    @Test
    void createProduct_WithMinimumRequiredFields_Success() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
                "Minimal Product", "minimal-product", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU001", null,
                null, DimensionUnit.CM, null, null, null, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act
        var result = productService.createProduct(productPostVm);

        // Assert
        assertNotNull(result);
        assertEquals("Minimal Product", result.name());
    }

    /**
     * Test creating product with invalid dimensions (length < width)
     */
    @DisplayName("Create product with invalid dimensions - Should throw BadRequestException")
    @Test
    void createProduct_WithInvalidDimensions_ThrowsBadRequestException() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
                "Invalid Dimensions Product", "invalid-dims", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU002", null,
                10d, DimensionUnit.CM, 5d, 20d, 10d, // length < width
                29.99, true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(productPostVm));
    }

    /**
     * Test creating product with duplicate SKU
     */
    @DisplayName("Create product with duplicate SKU - Should throw BadRequestException")
    @Test
    void createProduct_WithDuplicateSku_ThrowsBadRequestException() {
        // Arrange - Create first product
        ProductPostVm firstProduct = new ProductPostVm(
                "Product 1", "product-1", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "DUPLICATE_SKU", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        productService.createProduct(firstProduct);

        // Try to create second product with same SKU
        ProductPostVm secondProduct = new ProductPostVm(
                "Product 2", "product-2", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "DUPLICATE_SKU", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(secondProduct));
    }

    /**
     * Test creating product with duplicate slug
     */
    @DisplayName("Create product with duplicate slug - Should throw BadRequestException")
    @Test
    void createProduct_WithDuplicateSlug_ThrowsBadRequestException() {
        // Arrange - Create first product
        ProductPostVm firstProduct = new ProductPostVm(
                "Product Unique 1", "product-unique", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU_UNIQUE_1", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        productService.createProduct(firstProduct);

        // Try to create second product with same slug
        ProductPostVm secondProduct = new ProductPostVm(
                "Product Unique 2", "product-unique", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU_UNIQUE_2", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(secondProduct));
    }

    /**
     * Test creating product with multiple categories
     */
    @DisplayName("Create product with multiple categories - Success")
    @Test
    void createProduct_WithMultipleCategories_Success() {
        // Arrange
        Category category2 = new Category();
        category2.setName("Category 2");
        category2.setSlug("category-2");
        category2 = categoryRepository.save(category2);

        ProductPostVm productPostVm = new ProductPostVm(
                "Multi-Category Product", "multi-cat-product", testBrand.getId(),
                List.of(testCategory.getId(), category2.getId()), null, null,
                null, "SKU_MULTI_CAT", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act
        var result = productService.createProduct(productPostVm);

        // Assert
        assertNotNull(result);
        assertEquals("Multi-Category Product", result.name());
    }

    /**
     * Test product visibility settings
     */
    @DisplayName("Create product with different visibility settings - Success")
    @Test
    void createProduct_WithVisibilitySettings_Success() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
                "Visible Product", "visible-product", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU_VISIBLE", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, true, true, true,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act
        var result = productService.createProduct(productPostVm);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPublished());
        assertTrue(result.isAllowedToOrder());
        assertTrue(result.isFeatured());
        assertTrue(result.isVisibleIndividually());
        assertTrue(result.stockTrackingEnabled());
    }

    /**
     * Test product with dimension units
     */
    @DisplayName("Create product with different dimension units - Success")
    @Test
    void createProduct_WithDifferentDimensionUnits_Success() {
        // Arrange - Test with MM unit
        ProductPostVm productPostVm = new ProductPostVm(
                "MM Dimensions Product", "mm-dims-product", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU_MM_DIMS", null,
                100d, DimensionUnit.MM, 100d, 100d, 100d, 29.99,
                true, true, false, true, false,
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act
        var result = productService.createProduct(productPostVm);

        // Assert
        assertNotNull(result);
        assertEquals("MM Dimensions Product", result.name());
    }

    /**
     * Test product search/filter functionality
     */
    @DisplayName("Search products by name - Success")
    @Test
    void searchProducts_ByName_Success() {
        // Arrange
        Product product1 = Product.builder()
                .name("Search Test Product 1")
                .slug("search-test-1")
                .isPublished(true)
                .isVisibleIndividually(true)
                .brand(testBrand)
                .price(99.99)
                .thumbnailMediaId(1L)
                .build();

        Product product2 = Product.builder()
                .name("Another Product")
                .slug("another-product")
                .isPublished(true)
                .isVisibleIndividually(true)
                .brand(testBrand)
                .price(49.99)
                .thumbnailMediaId(1L)
                .build();

        productRepository.saveAll(List.of(product1, product2));

        // Act
        var result = productService.getProductsWithFilter(0, 10, "Search", testBrand.getName());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.productList().size());
    }

    /**
     * Test product retrieval by brand
     */
    @DisplayName("Get products by brand - Success")
    @Test
    void getProductsByBrand_WithValidBrand_Success() {
        // Arrange
        Product product = Product.builder()
                .name("Brand Test Product")
                .slug("brand-test-product")
                .isPublished(true)
                .isVisibleIndividually(true)
                .brand(testBrand)
                .price(79.99)
                .thumbnailMediaId(1L)
                .build();

        productRepository.save(product);

        // Act
        var result = productService.getProductsByBrand(testBrand.getSlug());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Test stock tracking settings
     */
    @DisplayName("Create product with stock tracking - Success")
    @Test
    void createProduct_WithStockTracking_Success() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
                "Stock Tracked Product", "stock-tracked", testBrand.getId(),
                List.of(testCategory.getId()), null, null,
                null, "SKU_STOCK", null,
                10d, DimensionUnit.CM, 10d, 10d, 10d, 29.99,
                true, true, false, true, true, // stockTrackingEnabled = true
                null, null, null, null,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), null
        );

        // Act
        var result = productService.createProduct(productPostVm);

        // Assert
        assertNotNull(result);
        assertTrue(result.stockTrackingEnabled());
    }
}
