package com.yas.product.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProductOptionValueSaveVm Interface Tests")
class ProductOptionValueSaveVmTest {

    @Test
    @DisplayName("Should create implementation of ProductOptionValueSaveVm interface")
    void testProductOptionValueSaveVmInterface() {
        // Create an implementation of the interface
        ProductOptionValueSaveVm vm = new ProductOptionValueSaveVm() {
            @Override
            public Long productOptionId() {
                return 1L;
            }
        };

        assertNotNull(vm);
        assertEquals(1L, vm.productOptionId());
    }

    @Test
    @DisplayName("Should handle null productOptionId")
    void testNullProductOptionId() {
        ProductOptionValueSaveVm vm = new ProductOptionValueSaveVm() {
            @Override
            public Long productOptionId() {
                return null;
            }
        };

        assertNull(vm.productOptionId());
    }

    @Test
    @DisplayName("Should handle different productOptionId values")
    void testDifferentProductOptionIds() {
        ProductOptionValueSaveVm vm1 = new ProductOptionValueSaveVm() {
            @Override
            public Long productOptionId() {
                return 1L;
            }
        };

        ProductOptionValueSaveVm vm2 = new ProductOptionValueSaveVm() {
            @Override
            public Long productOptionId() {
                return 2L;
            }
        };

        assertNotEquals(vm1.productOptionId(), vm2.productOptionId());
    }
}
