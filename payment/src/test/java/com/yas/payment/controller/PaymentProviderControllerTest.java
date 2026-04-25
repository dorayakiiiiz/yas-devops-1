package com.yas.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentProviderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentProviderService paymentProviderService;

    @InjectMocks
    private PaymentProviderController paymentProviderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Khởi tạo MockMvc với controller cần test
        mockMvc = MockMvcBuilders.standaloneSetup(paymentProviderController).build();
    }

    @Test
    void create_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        // Given
        CreatePaymentVm createVm = new CreatePaymentVm(); // Giả định các field (name, v.v.)
        PaymentProviderVm responseVm = new PaymentProviderVm();
        // Giả sử response có ID là 1
        responseVm.setId(1L);

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(responseVm);

        // When & Then
        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void update_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        // Given
        UpdatePaymentVm updateVm = new UpdatePaymentVm();
        PaymentProviderVm responseVm = new PaymentProviderVm();
        responseVm.setId(1L);

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(responseVm);

        // When & Then
        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAll_ShouldReturnList_WhenCallingStorefront() throws Exception {
        // Given
        PaymentProviderVm provider1 = new PaymentProviderVm();
        provider1.setId(1L);
        List<PaymentProviderVm> providers = List.of(provider1);

        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class))).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/storefront/payment-providers")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}