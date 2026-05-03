package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.repository.ProductAttributeGroupRepository;
import com.yas.product.repository.ProductAttributeRepository;
import com.yas.product.viewmodel.productattribute.ProductAttributeListGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributePostVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductAttributeServiceTest {

    @Mock
    private ProductAttributeRepository productAttributeRepository;

    @Mock
    private ProductAttributeGroupRepository productAttributeGroupRepository;

    @InjectMocks
    private ProductAttributeService productAttributeService;

    @Test
    void test_retrieve_pageable_product_attributes_successfully() {
        List<ProductAttribute> productAttributes = new ArrayList<>();
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Attribute1");
        productAttributes.add(attribute);
        Page<ProductAttribute> page = new PageImpl<>(productAttributes);
        when(productAttributeRepository.findAll(any(Pageable.class))).thenReturn(page);

        ProductAttributeListGetVm result = productAttributeService.getPageableProductAttributes(0, 10);

        assertEquals(1, result.productAttributeContent().size());
        assertEquals("Attribute1", result.productAttributeContent().get(0).name());
    }

    @Test
    void test_save_new_product_attribute_with_valid_name_and_group_id() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group1");

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("Attribute1");
        productAttribute.setProductAttributeGroup(group);

        when(productAttributeRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        when(productAttributeGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenReturn(productAttribute);

        ProductAttributePostVm postVm = new ProductAttributePostVm("Attribute1", 1L);
        ProductAttribute result = productAttributeService.save(postVm);

        assertEquals("Attribute1", result.getName());
        assertEquals(group, result.getProductAttributeGroup());
    }

    @Test
    void test_save_new_product_attribute_without_group() {
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("New Attribute");

        when(productAttributeRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenReturn(productAttribute);

        ProductAttributePostVm postVm = new ProductAttributePostVm("New Attribute", null);
        ProductAttribute result = productAttributeService.save(postVm);

        assertEquals("New Attribute", result.getName());
        assertNull(result.getProductAttributeGroup());
    }

    @Test
    void test_save_product_attribute_with_duplicated_name() {
        when(productAttributeRepository.findExistedName("Duplicate Name", null)).thenReturn(new ProductAttribute());

        ProductAttributePostVm vm = new ProductAttributePostVm("Duplicate Name", null);
        assertThrows(DuplicatedException.class, () -> productAttributeService.save(vm));
    }

    @Test
    void test_save_product_attribute_with_invalid_group_id_throws_bad_request() {
        when(productAttributeRepository.findExistedName(anyString(), isNull())).thenReturn(null);
        when(productAttributeGroupRepository.findById(999L)).thenReturn(Optional.empty());

        ProductAttributePostVm vm = new ProductAttributePostVm("New Attribute", 999L);
        assertThrows(BadRequestException.class, () -> productAttributeService.save(vm));
    }

    @Test
    void test_update_existing_product_attribute() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group1");

        ProductAttribute existingAttr = new ProductAttribute();
        existingAttr.setId(1L);
        existingAttr.setName("Attribute1");

        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(existingAttr));
        when(productAttributeRepository.findExistedName("Updated Attribute", 1L)).thenReturn(null);
        when(productAttributeGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenAnswer(invocation -> {
            ProductAttribute saved = invocation.getArgument(0);
            saved.setProductAttributeGroup(group);
            return saved;
        });

        ProductAttributePostVm postVm = new ProductAttributePostVm("Updated Attribute", 1L);
        ProductAttribute result = productAttributeService.update(postVm, 1L);

        assertEquals("Updated Attribute", result.getName());
        assertEquals(group, result.getProductAttributeGroup());
    }

    @Test
    void test_update_product_attribute_without_group() {
        ProductAttribute existingAttr = new ProductAttribute();
        existingAttr.setId(1L);
        existingAttr.setName("Old Attribute");

        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(existingAttr));
        when(productAttributeRepository.findExistedName("Updated Attribute", 1L)).thenReturn(null);
        when(productAttributeRepository.save(any(ProductAttribute.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductAttributePostVm postVm = new ProductAttributePostVm("Updated Attribute", null);
        ProductAttribute result = productAttributeService.update(postVm, 1L);

        assertEquals("Updated Attribute", result.getName());
        assertNull(result.getProductAttributeGroup());
    }

    @Test
    void test_update_product_attribute_with_duplicated_name() {
        when(productAttributeRepository.findExistedName("Duplicate Name", 1L)).thenReturn(new ProductAttribute());

        ProductAttributePostVm vm = new ProductAttributePostVm("Duplicate Name", null);
        assertThrows(DuplicatedException.class, () -> productAttributeService.update(vm, 1L));
    }

    @Test
    void test_update_nonexistent_product_attribute_throws_not_found() {
        when(productAttributeRepository.findById(999L)).thenReturn(Optional.empty());

        ProductAttributePostVm vm = new ProductAttributePostVm("New Name", null);
        assertThrows(NotFoundException.class, () -> productAttributeService.update(vm, 999L));
    }

    @Test
    void test_update_product_attribute_with_invalid_group_id_throws_bad_request() {
        ProductAttribute existingAttr = new ProductAttribute();
        existingAttr.setId(1L);
        existingAttr.setName("Old Attribute");

        when(productAttributeRepository.findById(1L)).thenReturn(Optional.of(existingAttr));
        when(productAttributeRepository.findExistedName("Updated Attribute", 1L)).thenReturn(null);
        when(productAttributeGroupRepository.findById(999L)).thenReturn(Optional.empty());

        ProductAttributePostVm vm = new ProductAttributePostVm("Updated Attribute", 999L);
        assertThrows(BadRequestException.class, () -> productAttributeService.update(vm, 1L));
    }
}