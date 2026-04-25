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
        // Given
        InitPaymentRequestVm requestVm = new InitPaymentRequestVm(); // Giả định các field cần thiết
        InitPaymentResponseVm responseVm = InitPaymentResponseVm.builder()
                .paymentId("PAY123")
                .status("PENDING")
                .build();

        when(paymentService.initPayment(any(InitPaymentRequestVm.class))).thenReturn(responseVm);

        // When & Then
        mockMvc.perform(post("/init")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY123"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void capturePayment_ShouldReturnResponse_WhenValidRequest() throws Exception {
        // Given
        CapturePaymentRequestVm requestVm = new CapturePaymentRequestVm();
        CapturePaymentResponseVm responseVm = CapturePaymentResponseVm.builder()
                .status("COMPLETED")
                .build();

        when(paymentService.capturePayment(any(CapturePaymentRequestVm.class))).thenReturn(responseVm);

        // When & Then
        mockMvc.perform(post("/capture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestVm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void cancelPayment_ShouldReturnMessage() throws Exception {
        // When & Then
        mockMvc.perform(get("/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment cancelled"));
    }
}