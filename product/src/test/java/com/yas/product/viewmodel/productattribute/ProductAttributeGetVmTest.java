package com.yas.product.viewmodel.productattribute;

import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductAttributeGetVmTest {

    @Test
    void fromModel_WithProductAttributeGroup_ShouldReturnVmWithGroupName() {
        // Arrange
        Long attributeId = 1L;
        String attributeName = "Color";
        String groupName = "Appearance";
        
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName(groupName);
        
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(attributeId);
        productAttribute.setName(attributeName);
        productAttribute.setProductAttributeGroup(group);

        // Act
        ProductAttributeGetVm result = ProductAttributeGetVm.fromModel(productAttribute);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(attributeId);
        assertThat(result.name()).isEqualTo(attributeName);
        assertThat(result.productAttributeGroup()).isEqualTo(groupName);
    }

    @Test
    void fromModel_WithoutProductAttributeGroup_ShouldReturnVmWithNullGroup() {
        // Arrange
        Long attributeId = 2L;
        String attributeName = "Weight";
        
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(attributeId);
        productAttribute.setName(attributeName);
        productAttribute.setProductAttributeGroup(null);

        // Act
        ProductAttributeGetVm result = ProductAttributeGetVm.fromModel(productAttribute);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(attributeId);
        assertThat(result.name()).isEqualTo(attributeName);
        assertThat(result.productAttributeGroup()).isNull();
    }

    @Test
    void fromModel_WithEmptyStringAttributes_ShouldHandleCorrectly() {
        // Arrange
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(3L);
        productAttribute.setName("");
        productAttribute.setProductAttributeGroup(null);

        // Act
        ProductAttributeGetVm result = ProductAttributeGetVm.fromModel(productAttribute);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.name()).isEmpty();
        assertThat(result.productAttributeGroup()).isNull();
    }

    @Test
    void record_ShouldBeImmutable() {
        // Arrange & Act
        ProductAttributeGetVm vm = new ProductAttributeGetVm(1L, "Size", "Dimensions");

        // Assert
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Size");
        assertThat(vm.productAttributeGroup()).isEqualTo("Dimensions");
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Arrange
        ProductAttributeGetVm vm1 = new ProductAttributeGetVm(1L, "Color", "Appearance");
        ProductAttributeGetVm vm2 = new ProductAttributeGetVm(1L, "Color", "Appearance");
        ProductAttributeGetVm vm3 = new ProductAttributeGetVm(2L, "Size", "Dimensions");

        // Assert
        assertThat(vm1).isEqualTo(vm2);
        assertThat(vm1.hashCode()).isEqualTo(vm2.hashCode());
        assertThat(vm1).isNotEqualTo(vm3);
    }
}