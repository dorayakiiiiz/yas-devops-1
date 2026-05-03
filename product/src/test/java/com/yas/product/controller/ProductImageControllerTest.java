package com.yas.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.product.model.Product;
import com.yas.product.model.ProductImage;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductImageController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductImageRepository productImageRepository;

    @MockitoBean
    private ProductRepository productRepository;

    /**
     * Test listing all product images
     */
    @Test
    void testListProductImages() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        ProductImage image1 = new ProductImage();
        image1.setId(1L);
        image1.setProduct(product);
        image1.setMediaId(100L);
        image1.setDisplayOrder(1);

        ProductImage image2 = new ProductImage();
        image2.setId(2L);
        image2.setProduct(product);
        image2.setMediaId(101L);
        image2.setDisplayOrder(2);

        when(productImageRepository.findAll()).thenReturn(List.of(image1, image2));

        mockMvc.perform(get("/backoffice/product-images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Test getting product images by product ID
     */
    @Test
    void testGetProductImagesByProductId_WithValidId_Success() throws Exception {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        ProductImage image = new ProductImage();
        image.setId(1L);
        image.setProduct(product);
        image.setMediaId(100L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productImageRepository.findAllByProduct(product)).thenReturn(List.of(image));

        mockMvc.perform(get("/backoffice/product-images/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Test getting product images with non-existent product
     */
    @Test
    void testGetProductImagesByProductId_WithNonExistentProduct_Returns404() throws Exception {
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/backoffice/product-images/product/{productId}", productId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test getting single product image by ID
     */
    @Test
    void testGetProductImageById_WithValidId_Success() throws Exception {
        Long imageId = 1L;
        Product product = new Product();
        product.setId(1L);

        ProductImage image = new ProductImage();
        image.setId(imageId);
        image.setProduct(product);
        image.setMediaId(100L);

        when(productImageRepository.findById(imageId)).thenReturn(Optional.of(image));

        mockMvc.perform(get("/backoffice/product-images/{id}", imageId))
                .andExpect(status().isOk());
    }

    /**
     * Test getting non-existent product image
     */
    @Test
    void testGetProductImageById_WithNonExistentId_Returns404() throws Exception {
        Long imageId = 999L;

        when(productImageRepository.findById(imageId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/backoffice/product-images/{id}", imageId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test deleting product image
     */
    @Test
    void testDeleteProductImage_WithValidId_Success() throws Exception {
        Long imageId = 1L;
        ProductImage image = new ProductImage();
        image.setId(imageId);

        when(productImageRepository.findById(imageId)).thenReturn(Optional.of(image));
        doNothing().when(productImageRepository).delete(image);

        mockMvc.perform(delete("/backoffice/product-images/{id}", imageId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test deleting non-existent product image
     */
    @Test
    void testDeleteProductImage_WithNonExistentId_Returns404() throws Exception {
        Long imageId = 999L;

        when(productImageRepository.findById(imageId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/backoffice/product-images/{id}", imageId))
                .andExpect(status().isNotFound());
    }
}
