package com.yas.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;
    private InitPaymentRequestVm initRequest;
    private CapturePaymentRequestVm captureRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        initRequest = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(new BigDecimal("99.99"))
                .checkoutId("checkout_123")
                .build();

        captureRequest = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("EC-12345678")
                .build();
    }

    @Test
    void initPayment_Success() throws Exception {
        InitPaymentResponseVm response = InitPaymentResponseVm.builder()
                .paymentId("pay_123")
                .status("SUCCESS")
                .redirectUrl("https://paypal.com")
                .build();

        when(paymentService.initPayment(any())).thenReturn(response);

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("pay_123"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void initPayment_BadRequest_WhenInvalidData() throws Exception {
        InitPaymentRequestVm invalidRequest = InitPaymentRequestVm.builder().build();

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void capturePayment_Success() throws Exception {
        CapturePaymentResponseVm response = CapturePaymentResponseVm.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("99.99"))
                .build();

        when(paymentService.capturePayment(any())).thenReturn(response);

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(captureRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));
    }

    @Test
    void capturePayment_BadRequest_WhenInvalidData() throws Exception {
        CapturePaymentRequestVm invalidRequest = CapturePaymentRequestVm.builder().build();

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelPayment_ReturnsSuccessMessage() throws Exception {
        mockMvc.perform(get("/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment cancelled"));
    }
}