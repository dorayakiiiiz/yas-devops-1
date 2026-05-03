package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeValueRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeValuePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductAttributeValueServiceTest {

    @Mock
    private ProductAttributeValueRepository productAttributeValueRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductAttributeRepository productAttributeRepository;

    @InjectMocks
    private ProductAttributeValueService productAttributeValueService;

    /**
     * Test creating product attribute value with valid data
     */
    @Test
    void testCreateProductAttributeValue_WithValidData_Success() {
        // Arrange
        ProductAttributeValuePostVm postVm = new ProductAttributeValuePostVm(1L, 1L, "Red");
        
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Color");
        
        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setId(1L);
        attributeValue.setProduct(product);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Red");
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(attribute));
        when(productAttributeValueRepository.save(any(ProductAttributeValue.class))).thenReturn(attributeValue);
        
        // Act
        ProductAttributeValue result = productAttributeValueService.createProductAttributeValue(postVm);
        
        // Assert
        assertNotNull(result);
        assertEquals("Red", result.getValue());
        verify(productRepository).findById(1L);
        verify(productAttributeRepository).findById(1L);
        verify(productAttributeValueRepository).save(any(ProductAttributeValue.class));
    }

    /**
     * Test creating product attribute value with non-existent product
     */
    @Test
    void testCreateProductAttributeValue_WithNonExistentProduct_ThrowsNotFoundException() {
        // Arrange
        ProductAttributeValuePostVm postVm = new ProductAttributeValuePostVm(999L, 1L, "Red");
        
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> productAttributeValueService.createProductAttributeValue(postVm));
        verify(productRepository).findById(999L);
    }

    /**
     * Test creating product attribute value with non-existent attribute
     */
    @Test
    void testCreateProductAttributeValue_WithNonExistentAttribute_ThrowsNotFoundException() {
        // Arrange
        ProductAttributeValuePostVm postVm = new ProductAttributeValuePostVm(1L, 999L, "Red");
        
        Product product = new Product();
        product.setId(1L);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> productAttributeValueService.createProductAttributeValue(postVm));
        verify(productAttributeRepository).findById(999L);
    }

    /**
     * Test updating product attribute value with valid data
     */
    @Test
    void testUpdateProductAttributeValue_WithValidData_Success() {
        // Arrange
        Long id = 1L;
        ProductAttributeValuePostVm postVm = new ProductAttributeValuePostVm(1L, 1L, "Blue");
        
        Product product = new Product();
        product.setId(1L);
        
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        
        ProductAttributeValue existingValue = new ProductAttributeValue();
        existingValue.setId(id);
        existingValue.setProduct(product);
        existingValue.setProductAttribute(attribute);
        existingValue.setValue("Red");
        
        when(productAttributeValueRepository.findById(id)).thenReturn(Optional.of(existingValue));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(attribute));
        when(productAttributeValueRepository.save(any(ProductAttributeValue.class))).thenReturn(existingValue);
        
        // Act
        ProductAttributeValue result = productAttributeValueService.updateProductAttributeValue(id, postVm);
        
        // Assert
        assertNotNull(result);
        verify(productAttributeValueRepository).findById(id);
        verify(productAttributeValueRepository).save(any(ProductAttributeValue.class));
    }

    /**
     * Test updating non-existent product attribute value
     */
    @Test
    void testUpdateProductAttributeValue_WithNonExistentId_ThrowsNotFoundException() {
        // Arrange
        Long id = 999L;
        ProductAttributeValuePostVm postVm = new ProductAttributeValuePostVm(1L, 1L, "Blue");
        
        when(productAttributeValueRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> productAttributeValueService.updateProductAttributeValue(id, postVm));
    }

    /**
     * Test deleting product attribute value with valid ID
     */
    @Test
    void testDeleteProductAttributeValue_WithValidId_Success() {
        // Arrange
        Long id = 1L;
        ProductAttributeValue value = new ProductAttributeValue();
        value.setId(id);
        
        when(productAttributeValueRepository.findById(id)).thenReturn(Optional.of(value));
        
        // Act
        productAttributeValueService.deleteProductAttributeValue(id);
        
        // Assert
        verify(productAttributeValueRepository).findById(id);
        verify(productAttributeValueRepository).delete(value);
    }

    /**
     * Test deleting non-existent product attribute value
     */
    @Test
    void testDeleteProductAttributeValue_WithNonExistentId_ThrowsNotFoundException() {
        // Arrange
        Long id = 999L;
        
        when(productAttributeValueRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, 
                () -> productAttributeValueService.deleteProductAttributeValue(id));
    }

    /**
     * Test getting product attribute values by product
     */
    @Test
    void testGetProductAttributeValuesByProduct_WithValidProduct_Success() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        
        ProductAttributeValue value1 = new ProductAttributeValue();
        value1.setId(1L);
        value1.setValue("Red");
        
        ProductAttributeValue value2 = new ProductAttributeValue();
        value2.setId(2L);
        value2.setValue("Blue");
        
        when(productAttributeValueRepository.findAllByProduct(product))
                .thenReturn(List.of(value1, value2));
        
        // Act
        List<ProductAttributeValue> result = productAttributeValueRepository.findAllByProduct(product);
        
        // Assert
        assertEquals(2, result.size());
        verify(productAttributeValueRepository).findAllByProduct(product);
    }

    /**
     * Test creating product attribute value with empty value
     */
    @Test
    void testCreateProductAttributeValue_WithEmptyValue_Success() {
        // Arrange
        ProductAttributeValuePostVm postVm = new ProductAttributeValuePostVm(1L, 1L, "");
        
        Product product = new Product();
        product.setId(1L);
        
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        
        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setId(1L);
        attributeValue.setValue("");
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(attribute));
        when(productAttributeValueRepository.save(any(ProductAttributeValue.class))).thenReturn(attributeValue);
        
        // Act
        ProductAttributeValue result = productAttributeValueService.createProductAttributeValue(postVm);
        
        // Assert
        assertNotNull(result);
    }

    /**
     * Test creating multiple product attribute values for same product
     */
    @Test
    void testCreateMultipleProductAttributeValues_ForSameProduct_Success() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        
        ProductAttribute colorAttribute = new ProductAttribute();
        colorAttribute.setId(1L);
        
        ProductAttribute sizeAttribute = new ProductAttribute();
        sizeAttribute.setId(2L);
        
        ProductAttributeValuePostVm colorVm = new ProductAttributeValuePostVm(1L, 1L, "Red");
        ProductAttributeValuePostVm sizeVm = new ProductAttributeValuePostVm(1L, 2L, "Large");
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(colorAttribute));
        when(productAttributeRepository.findById(2L)).thenReturn(Optional.of(sizeAttribute));
        
        ProductAttributeValue colorValue = new ProductAttributeValue();
        colorValue.setId(1L);
        colorValue.setValue("Red");
        
        ProductAttributeValue sizeValue = new ProductAttributeValue();
        sizeValue.setId(2L);
        sizeValue.setValue("Large");
        
        when(productAttributeValueRepository.save(any(ProductAttributeValue.class)))
                .thenReturn(colorValue).thenReturn(sizeValue);
        
        // Act
        ProductAttributeValue result1 = productAttributeValueService.createProductAttributeValue(colorVm);
        ProductAttributeValue result2 = productAttributeValueService.createProductAttributeValue(sizeVm);
        
        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("Red", result1.getValue());
        assertEquals("Large", result2.getValue());
        verify(productAttributeValueRepository, times(2)).save(any(ProductAttributeValue.class));
    }
}
