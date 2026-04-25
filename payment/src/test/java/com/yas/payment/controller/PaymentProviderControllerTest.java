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
        mockMvc = MockMvcBuilders.standaloneSetup(paymentProviderController).build();
    }

    @Test
    void create_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        CreatePaymentVm createVm = new CreatePaymentVm("Paypal", "paypal_id", "secret", true); 
        PaymentProviderVm responseVm = new PaymentProviderVm("PAYPAL", "Paypal", "Description", 1, 1L, "{}");

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(responseVm);

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createVm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("PAYPAL"));
    }

    @Test
    void update_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        UpdatePaymentVm updateVm = new UpdatePaymentVm("PAYPAL", "New Name", "New Desc", true);
        PaymentProviderVm responseVm = new PaymentProviderVm("PAYPAL", "New Name", "New Desc", 1, 1L, "{}");

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(responseVm);

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PAYPAL"));
    }

    @Test
    void getAll_ShouldReturnList_WhenCallingStorefront() throws Exception {
        PaymentProviderVm provider1 = new PaymentProviderVm("PAYPAL", "Paypal", "Desc", 1, 1L, "{}");
        List<PaymentProviderVm> providers = List.of(provider1);

        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class))).thenReturn(providers);

        mockMvc.perform(get("/storefront/payment-providers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("PAYPAL"));
    }
}