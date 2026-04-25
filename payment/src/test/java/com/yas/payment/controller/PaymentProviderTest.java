package com.yas.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void initPayment_ShouldReturnResponse_WhenValidRequest() throws Exception {
        //InitPaymentRequestVm là Record (String, BigDecimal, String)
        InitPaymentRequestVm requestVm = new InitPaymentRequestVm("ORDER_001", new BigDecimal("100.00"), "USD"); 
        
        InitPaymentResponseVm responseVm = InitPaymentResponseVm.builder()
                .paymentId("PAY123")
                .status("PENDING")
                .build();

        when(paymentService.initPayment(any(InitPaymentRequestVm.class))).thenReturn(responseVm);

        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY123"));
    }

    @Test
    void capturePayment_ShouldReturnResponse_WhenValidRequest() throws Exception {
        CapturePaymentRequestVm requestVm = new CapturePaymentRequestVm("PAYMENT_ID", "PAYER_ID");
        CapturePaymentResponseVm responseVm = CapturePaymentResponseVm.builder()
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class))).thenReturn(responseVm);

        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVm)))
                .andExpect(status().isOk());
    }

    @Test
    void cancelPayment_ShouldReturnMessage() throws Exception {
        mockMvc.perform(get("/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment cancelled"));
    }
}