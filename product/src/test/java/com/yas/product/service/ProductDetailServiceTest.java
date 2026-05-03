package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDetailService Tests")
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    private ProductDetailService productDetailService;

    @BeforeEach
    void setUp() {
        productDetailService = new ProductDetailService(
            productRepository,
            mediaService,
            productOptionCombinationRepository
        );
    }

    @Test
    @DisplayName("Should create ProductDetailService with all dependencies")
    void testProductDetailServiceInitialization() {
        assertNotNull(productDetailService);
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found")
    void testGetProductDetailByIdNotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productDetailService.getProductDetailById(1L);
        });
    }

    @Test
    @DisplayName("Should throw NotFoundException when product is not published")
    void testGetProductDetailByIdNotPublished() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Not Published Product");
        product.setPublished(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productDetailService.getProductDetailById(1L);
        });
    }

    @Test
    @DisplayName("Should call mediaService for thumbnail")
    void testGetProductDetailCallsMediaService() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Running Shoes");
        product.setPublished(true);
        product.setPrice(99.99);
        product.setThumbnailMediaId(100L);
        product.setHasOptions(false);
        product.setAttributeValues(new ArrayList<>());
        product.setProducts(new ArrayList<>());
        product.setProductCategories(new ArrayList<>());
        product.setProductImages(new ArrayList<>());

        NoFileMediaVm mediaVm = new NoFileMediaVm(100L, "image.jpg", "url", "caption", "");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(100L)).thenReturn(mediaVm);

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        // Assert
        assertNotNull(result);
        verify(mediaService, times(1)).getMedia(100L);
    }
}
