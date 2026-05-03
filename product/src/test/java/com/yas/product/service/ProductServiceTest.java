package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductOptionValueRepository productOptionValueRepository;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @Mock
    private ProductRelatedRepository productRelatedRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(
            productRepository,
            mediaService,
            brandRepository,
            productCategoryRepository,
            categoryRepository,
            productImageRepository,
            productOptionRepository,
            productOptionValueRepository,
            productOptionCombinationRepository,
            productRelatedRepository
        );
    }

    @Test
    @DisplayName("Should create ProductService with all dependencies")
    void testProductServiceInitialization() {
        assertNotNull(productService);
    }

    @Test
    @DisplayName("Should handle product with basic properties")
    void testProductWithBasicProperties() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setSku("RUN-001");
        product.setPrice(99.99);
        product.setPublished(true);

        assertEquals(1L, product.getId());
        assertEquals("Running Shoes", product.getName());
        assertEquals("RUN-001", product.getSku());
        assertEquals(99.99, product.getPrice());
        assertTrue(product.isPublished());
    }

    @Test
    @DisplayName("Should handle product with brand relationship")
    void testProductWithBrand() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Nike");

        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setBrand(brand);

        assertEquals(brand, product.getBrand());
        assertEquals("Nike", product.getBrand().getName());
    }

    @Test
    @DisplayName("Should handle product with dimension properties")
    void testProductWithDimensions() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Box");
        product.setDimensionUnit(DimensionUnit.CM);
        product.setLength(30.0);
        product.setWidth(20.0);
        product.setHeight(15.0);
        product.setWeight(2.5);

        assertEquals(DimensionUnit.CM, product.getDimensionUnit());
        assertEquals(30.0, product.getLength());
        assertEquals(20.0, product.getWidth());
        assertEquals(15.0, product.getHeight());
        assertEquals(2.5, product.getWeight());
    }

    @Test
    @DisplayName("Should handle product with SEO properties")
    void testProductWithSEOProperties() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setSlug("running-shoes");
        product.setMetaTitle("Buy Running Shoes Online");
        product.setMetaKeyword("running shoes, sports shoes");
        product.setMetaDescription("Premium quality running shoes");

        assertEquals("running-shoes", product.getSlug());
        assertEquals("Buy Running Shoes Online", product.getMetaTitle());
        assertEquals("running shoes, sports shoes", product.getMetaKeyword());
        assertEquals("Premium quality running shoes", product.getMetaDescription());
    }

    @Test
    @DisplayName("Should handle product stock properties")
    void testProductWithStockProperties() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(100L);

        assertTrue(product.isStockTrackingEnabled());
        assertEquals(100L, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should handle product visibility properties")
    void testProductWithVisibilityProperties() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setPublished(true);
        product.setFeatured(true);
        product.setVisibleIndividually(true);
        product.setAllowedToOrder(true);

        assertTrue(product.isPublished());
        assertTrue(product.isFeatured());
        assertTrue(product.isVisibleIndividually());
        assertTrue(product.isAllowedToOrder());
    }

    @Test
    @DisplayName("Should handle product option properties")
    void testProductWithOptions() {
        Product product = new Product();
        product.setId(1L);
        product.setName("T-Shirt");
        product.setHasOptions(true);

        assertTrue(product.isHasOptions());
    }

    @Test
    @DisplayName("Should handle product parent-child relationship")
    void testProductWithParentChild() {
        Product parentProduct = new Product();
        parentProduct.setId(1L);
        parentProduct.setName("Parent Product");

        Product childProduct = new Product();
        childProduct.setId(2L);
        childProduct.setName("Variant 1");
        childProduct.setParent(parentProduct);

        assertEquals(parentProduct, childProduct.getParent());
    }

    @Test
    @DisplayName("Should handle product categories")
    void testProductWithCategories() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");

        Category category = new Category();
        category.setId(1L);
        category.setName("Shoes");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        productCategory.setCategory(category);

        product.getProductCategories().add(productCategory);

        assertEquals(1, product.getProductCategories().size());
    }

    @Test
    @DisplayName("Should handle product images")
    void testProductWithImages() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");

        ProductImage image = new ProductImage();
        image.setId(1L);
        image.setImageId(100L);
        image.setProduct(product);

        product.getProductImages().add(image);

        assertEquals(1, product.getProductImages().size());
        assertEquals(100L, product.getProductImages().get(0).getImageId());
    }

    @Test
    @DisplayName("Should handle product with null properties safely")
    void testProductWithNullProperties() {
        Product product = new Product();
        
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getBrand());
        assertNull(product.getPrice());
    }

    @Test
    @DisplayName("Should handle product tax properties")
    void testProductWithTaxProperties() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setTaxClassId(1L);
        product.setTaxIncluded(true);

        assertEquals(1L, product.getTaxClassId());
        assertTrue(product.isTaxIncluded());
    }
}
