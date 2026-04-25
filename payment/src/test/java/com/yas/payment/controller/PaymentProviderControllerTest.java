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

import java.util.Arrays;
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

    private ObjectMapper objectMapper;
    private CreatePaymentVm createRequest;
    private UpdatePaymentVm updateRequest;
    private PaymentProviderVm paymentProviderVm;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(paymentProviderController).build();

        // Sử dụng setter hoặc builder nếu có
        createRequest = new CreatePaymentVm();
        createRequest.setName("PayPal");
        createRequest.setEnabled(true);
        createRequest.setConfigureUrl("https://paypal.com/configure");
        createRequest.setLandingViewComponentName("PaypalComponent");
        createRequest.setAdditionalSettings("{\"mode\":\"sandbox\"}");
        createRequest.setMediaId(1L);

        updateRequest = new UpdatePaymentVm();
        updateRequest.setId("provider_123");
        updateRequest.setName("PayPal Express");
        updateRequest.setEnabled(true);
        updateRequest.setConfigureUrl("https://paypal.com/configure");
        updateRequest.setLandingViewComponentName("PaypalComponent");
        updateRequest.setAdditionalSettings("{\"mode\":\"live\"}");
        updateRequest.setMediaId(1L);

        paymentProviderVm = new PaymentProviderVm();
        paymentProviderVm.setId("provider_123");
        paymentProviderVm.setName("PayPal");
        paymentProviderVm.setEnabled(true);
        paymentProviderVm.setConfigureUrl("https://paypal.com/configure");
        paymentProviderVm.setLandingViewComponentName("PaypalComponent");
        paymentProviderVm.setAdditionalSettings("{\"mode\":\"sandbox\"}");
        paymentProviderVm.setMediaId(1L);
    }

    @Test
    void create_ShouldReturnCreatedPaymentProvider() throws Exception {
        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(paymentProviderVm);

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("provider_123"))
                .andExpect(jsonPath("$.name").value("PayPal"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        CreatePaymentVm invalidRequest = new CreatePaymentVm();

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturnUpdatedPaymentProvider() throws Exception {
        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(paymentProviderVm);

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("provider_123"))
                .andExpect(jsonPath("$.name").value("PayPal"));
    }

    @Test
    void update_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        UpdatePaymentVm invalidRequest = new UpdatePaymentVm();

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_ShouldReturnListOfPaymentProviders() throws Exception {
        List<PaymentProviderVm> providers = Arrays.asList(paymentProviderVm);
        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class))).thenReturn(providers);

        mockMvc.perform(get("/storefront/payment-providers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("provider_123"))
                .andExpect(jsonPath("$[0].name").value("PayPal"));
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoProviders() throws Exception {
        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class))).thenReturn(List.of());

        mockMvc.perform(get("/storefront/payment-providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}