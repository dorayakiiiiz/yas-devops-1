package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeTemplate;
import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.repository.ProductAttributeTemplateRepository;
import com.yas.product.repository.ProductTemplateRepository;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.producttemplate.ProductAttributeTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateListGetVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplatePostVm;
import com.yas.product.viewmodel.producttemplate.ProductTemplateVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductTemplateServiceTest {

    @Mock
    private ProductAttributeRepository productAttributeRepository;

    @Mock
    private ProductAttributeTemplateRepository productAttributeTemplateRepository;

    @Mock
    private ProductAttributeGroupRepository productAttributeGroupRepository;

    @Mock
    private ProductTemplateRepository productTemplateRepository;

    @InjectMocks
    private ProductTemplateService productTemplateService;

    private ProductAttribute productAttribute1;
    private ProductAttribute productAttribute2;
    private ProductTemplate productTemplate1;

    @BeforeEach
    void setUp() {
        productAttribute1 = ProductAttribute.builder().id(1L).name("Attribute1").build();
        productAttribute2 = ProductAttribute.builder().id(2L).name("Attribute2").build();
        productTemplate1 = ProductTemplate.builder().id(1L).name("Template1").productAttributeTemplates(new ArrayList<>()).build();
    }

    @Test
    void getPageableProductTemplate_WhenEmpty_thenReturnEmptyList() {
        Page<ProductTemplate> emptyPage = new PageImpl<>(Collections.emptyList());
        
        when(productTemplateRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        ProductTemplateListGetVm result = productTemplateService.getPageableProductTemplate(0, 10);

        assertNotNull(result);
        assertTrue(result.productTemplateVms().isEmpty());
    }

    @Test
    void getProductTemplate_WhenIdNotFound_ThrowsNotFoundException() {
        Long invalidId = 999L;
        when(productTemplateRepository.findById(invalidId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> productTemplateService.getProductTemplate(invalidId));
        assertEquals(Constants.ErrorCode.PRODUCT_TEMPlATE_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void getProductTemplate_WhenIdValid_thenSuccess() {
        when(productTemplateRepository.findById(1L)).thenReturn(Optional.of(productTemplate1));
        when(productAttributeTemplateRepository.findAllByProductTemplateId(1L)).thenReturn(Collections.emptyList());

        ProductTemplateVm result = productTemplateService.getProductTemplate(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Template1", result.name());
    }

    @Test
    void saveProductTemplate_WhenProductAttributesNotFound_ThenThrowBadRequestException() {
        when(productTemplateRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        
        List<ProductAttributeTemplatePostVm> attTemplates = List.of(
            new ProductAttributeTemplatePostVm(999L, 0)
        );
        ProductTemplatePostVm vm = new ProductTemplatePostVm("NewTemplate", attTemplates);
        
        when(productAttributeRepository.findAllById(List.of(999L))).thenReturn(Collections.emptyList());

        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> productTemplateService.saveProductTemplate(vm));
        assertEquals(Constants.ErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void saveProductTemplate_WhenPartialAttributesNotFound_ThenThrowBadRequestException() {
        when(productTemplateRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        
        List<ProductAttributeTemplatePostVm> attTemplates = List.of(
            new ProductAttributeTemplatePostVm(1L, 0),
            new ProductAttributeTemplatePostVm(999L, 1)
        );
        ProductTemplatePostVm vm = new ProductTemplatePostVm("NewTemplate", attTemplates);
        
        when(productAttributeRepository.findAllById(List.of(1L, 999L))).thenReturn(List.of(productAttribute1));

        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> productTemplateService.saveProductTemplate(vm));
        assertEquals(Constants.ErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void saveProductTemplate_WhenValid_thenSuccess() {
        when(productTemplateRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        
        List<ProductAttributeTemplatePostVm> attTemplates = List.of(
            new ProductAttributeTemplatePostVm(1L, 0)
        );
        ProductTemplatePostVm vm = new ProductTemplatePostVm("NewTemplate", attTemplates);
        
        ProductTemplate savedTemplate = ProductTemplate.builder().id(2L).name("NewTemplate").build();
        
        when(productAttributeRepository.findAllById(List.of(1L))).thenReturn(List.of(productAttribute1));
        when(productTemplateRepository.save(any(ProductTemplate.class))).thenReturn(savedTemplate);
        when(productAttributeTemplateRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productTemplateRepository.findById(2L)).thenReturn(Optional.of(savedTemplate));
        when(productAttributeTemplateRepository.findAllByProductTemplateId(2L)).thenReturn(Collections.emptyList());

        ProductTemplateVm result = productTemplateService.saveProductTemplate(vm);

        assertNotNull(result);
        assertEquals("NewTemplate", result.name());
        verify(productTemplateRepository).save(any(ProductTemplate.class));
        verify(productAttributeTemplateRepository).saveAll(anyList());
    }

    @Test
    void updateProductTemplate_WhenIdNotFound_ThenThrowNotFoundException() {
        ProductTemplatePostVm vm = new ProductTemplatePostVm("Updated", null);
        
        when(productTemplateRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> productTemplateService.updateProductTemplate(999L, vm));
        assertEquals(Constants.ErrorCode.PRODUCT_TEMPlATE_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateProductTemplate_WhenValid_thenSuccess() {
        ProductTemplate existingTemplate = ProductTemplate.builder().id(1L).name("OldName").build();
        existingTemplate.setProductAttributeTemplates(new ArrayList<>());
        
        ProductTemplatePostVm vm = new ProductTemplatePostVm("UpdatedName", List.of(
            new ProductAttributeTemplatePostVm(1L, 0)
        ));
        
        when(productTemplateRepository.findById(1L)).thenReturn(Optional.of(existingTemplate));
        when(productAttributeTemplateRepository.findAllByProductTemplateId(1L)).thenReturn(Collections.emptyList());
        when(productAttributeRepository.findAllById(List.of(1L))).thenReturn(List.of(productAttribute1));
        when(productTemplateRepository.save(any(ProductTemplate.class))).thenReturn(existingTemplate);
        when(productAttributeTemplateRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> productTemplateService.updateProductTemplate(1L, vm));
        
        verify(productTemplateRepository).save(any(ProductTemplate.class));
        verify(productAttributeTemplateRepository).saveAll(anyList());
    }

    @Test
    void validateExistedName_WhenNameExists_ThrowsException() {
        when(productTemplateRepository.findExistedName("Existing", 1L)).thenReturn(productTemplate1);

        assertThrows(DuplicatedException.class, 
            () -> productTemplateService.validateExistedName("Existing", 1L));
    }

    @Test
    void validateExistedName_WhenNameDoesNotExist_DoesNotThrowException() {
        when(productTemplateRepository.findExistedName("NewName", 1L)).thenReturn(null);

        assertDoesNotThrow(() -> productTemplateService.validateExistedName("NewName", 1L));
    }

    @Test
    void checkExistedName_ReturnsTrueWhenExists() {
        when(productTemplateRepository.findExistedName("Existing", null)).thenReturn(productTemplate1);

        boolean result = productTemplateService.checkExistedName("Existing", null);

        assertTrue(result);
    }

    @Test
    void checkExistedName_ReturnsFalseWhenNotExists() {
        when(productTemplateRepository.findExistedName("NewName", null)).thenReturn(null);

        boolean result = productTemplateService.checkExistedName("NewName", null);

        assertFalse(result);
    }
}