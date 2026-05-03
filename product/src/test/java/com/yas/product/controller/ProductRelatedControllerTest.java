package com.yas.product.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.product.model.Product;
import com.yas.product.model.ProductRelated;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProductRelatedController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductRelatedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRelatedRepository productRelatedRepository;

    @MockitoBean
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test getting all related products for a product
     */
    @Test
    void testGetRelatedProducts_WithValidProductId_Success() throws Exception {
        Long productId = 1L;
        Product mainProduct = new Product();
        mainProduct.setId(productId);
        mainProduct.setName("Main Product");

        Product relatedProduct1 = new Product();
        relatedProduct1.setId(2L);
        relatedProduct1.setName("Related Product 1");

        Product relatedProduct2 = new Product();
        relatedProduct2.setId(3L);
        relatedProduct2.setName("Related Product 2");

        ProductRelated relation1 = new ProductRelated();
        relation1.setId(1L);
        relation1.setProduct(mainProduct);
        relation1.setRelatedProduct(relatedProduct1);

        ProductRelated relation2 = new ProductRelated();
        relation2.setId(2L);
        relation2.setProduct(mainProduct);
        relation2.setRelatedProduct(relatedProduct2);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mainProduct));
        when(productRelatedRepository.findAllByProduct(mainProduct))
                .thenReturn(List.of(relation1, relation2));

        mockMvc.perform(get("/backoffice/products/{productId}/related-products", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Test getting related products for non-existent product
     */
    @Test
    void testGetRelatedProducts_WithNonExistentProduct_Returns404() throws Exception {
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/backoffice/products/{productId}/related-products", productId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test getting related products with empty list
     */
    @Test
    void testGetRelatedProducts_WithNoRelatedProducts_ReturnsEmptyList() throws Exception {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRelatedRepository.findAllByProduct(product)).thenReturn(List.of());

        mockMvc.perform(get("/backoffice/products/{productId}/related-products", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Test adding a related product
     */
    @Test
    void testAddRelatedProduct_WithValidProducts_Success() throws Exception {
        Long productId = 1L;
        Long relatedProductId = 2L;

        Product mainProduct = new Product();
        mainProduct.setId(productId);

        Product relatedProduct = new Product();
        relatedProduct.setId(relatedProductId);

        ProductRelated relation = new ProductRelated();
        relation.setId(1L);
        relation.setProduct(mainProduct);
        relation.setRelatedProduct(relatedProduct);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mainProduct));
        when(productRepository.findById(relatedProductId)).thenReturn(Optional.of(relatedProduct));
        when(productRelatedRepository.save(any(ProductRelated.class))).thenReturn(relation);

        mockMvc.perform(post("/backoffice/products/{productId}/related-products/{relatedProductId}",
                productId, relatedProductId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Test adding related product when main product doesn't exist
     */
    @Test
    void testAddRelatedProduct_WithNonExistentMainProduct_Returns404() throws Exception {
        Long productId = 999L;
        Long relatedProductId = 2L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/backoffice/products/{productId}/related-products/{relatedProductId}",
                productId, relatedProductId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test adding related product when related product doesn't exist
     */
    @Test
    void testAddRelatedProduct_WithNonExistentRelatedProduct_Returns404() throws Exception {
        Long productId = 1L;
        Long relatedProductId = 999L;

        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.findById(relatedProductId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/backoffice/products/{productId}/related-products/{relatedProductId}",
                productId, relatedProductId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test removing a related product
     */
    @Test
    void testRemoveRelatedProduct_WithValidId_Success() throws Exception {
        Long relationId = 1L;
        ProductRelated relation = new ProductRelated();
        relation.setId(relationId);

        when(productRelatedRepository.findById(relationId)).thenReturn(Optional.of(relation));
        doNothing().when(productRelatedRepository).delete(relation);

        mockMvc.perform(delete("/backoffice/products/related-products/{relationId}", relationId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test removing non-existent related product
     */
    @Test
    void testRemoveRelatedProduct_WithNonExistentId_Returns404() throws Exception {
        Long relationId = 999L;

        when(productRelatedRepository.findById(relationId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/backoffice/products/related-products/{relationId}", relationId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test getting related products with pagination
     */
    @Test
    void testGetRelatedProductsWithPagination_Success() throws Exception {
        Long productId = 1L;
        Product mainProduct = new Product();
        mainProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(mainProduct));
        when(productRelatedRepository.findAllByProduct(mainProduct)).thenReturn(List.of());

        mockMvc.perform(get("/storefront/products/{productId}/related-products", productId)
                .param("pageNo", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    /**
     * Test getting all product relations
     */
    @Test
    void testListAllProductRelations_Success() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        ProductRelated relation = new ProductRelated();
        relation.setId(1L);
        relation.setProduct(product1);
        relation.setRelatedProduct(product2);

        when(productRelatedRepository.findAll()).thenReturn(List.of(relation));

        mockMvc.perform(get("/backoffice/products/relations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    /**
     * Test preventing self-reference relationship
     */
    @Test
    void testAddRelatedProduct_WithSameProductId_ReturnsBadRequest() throws Exception {
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        mockMvc.perform(post("/backoffice/products/{productId}/related-products/{relatedProductId}",
                productId, productId))
                .andExpect(status().isBadRequest());
    }
}
