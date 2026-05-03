package com.yas.location.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.service.StateOrProvinceService;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StateOrProvinceController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class StateOrProvinceControllerTest {

    @MockitoBean
    private StateOrProvinceService stateOrProvinceService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateStateOrProvince_whenRequestIsValid_thenReturnOk() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);
        StateOrProvince stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .country(Country.builder().id(1L).build())
            .build();
        given(stateOrProvinceService.createStateOrProvince(stateOrProvincePostVm)).willReturn(
            stateOrProvince);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }

    @Test
    void testCreateStateOrProvince_whenCodeIsOverMaxLength_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("1234567890".repeat(26))
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateStateOrProvince_whenNameIsBlank_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(post(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStateOrProvince_whenRequestIsValid_thenReturnOk() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateStateOrProvince_whenCodeIsOverMaxLength_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("name")
            .code("1234567890".repeat(26))
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateStateOrProvince_whenNameIsBlank_thenReturnBadRequest() throws Exception {
        StateOrProvincePostVm stateOrProvincePostVm = StateOrProvincePostVm.builder()
            .name("")
            .code("code")
            .type("type")
            .countryId(1L)
            .build();

        String request = objectWriter.writeValueAsString(stateOrProvincePostVm);

        this.mockMvc.perform(put(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllStateOrProvinces_whenCountryIdProvided_thenReturnOkAndBody() throws Exception {
        StateOrProvinceVm vm = new StateOrProvinceVm(1L, "Hanoi", "HN", "CITY", 100L);
        given(stateOrProvinceService.getAllByCountryId(100L)).willReturn(List.of(vm));

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL)
                .param("countryId", "100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Hanoi"))
            .andExpect(jsonPath("$[0].countryId").value(100));

        verify(stateOrProvinceService).getAllByCountryId(100L);
    }

    @Test
    void testGetStateOrProvince_whenValidId_thenReturnOkAndBody() throws Exception {
        StateOrProvinceVm vm = new StateOrProvinceVm(5L, "Ho Chi Minh", "HCM", "CITY", 100L);
        given(stateOrProvinceService.findById(5L)).willReturn(vm);

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("Ho Chi Minh"))
            .andExpect(jsonPath("$.countryId").value(100));

        verify(stateOrProvinceService).findById(5L);
    }

    @Test
    void testGetPageableStateOrProvinces_whenValidParams_thenReturnOkAndBody() throws Exception {
        StateOrProvinceVm vm = new StateOrProvinceVm(1L, "Hanoi", "HN", "CITY", 100L);
        StateOrProvinceListGetVm response = new StateOrProvinceListGetVm(List.of(vm), 0, 10, 1, 1, true);
        given(stateOrProvinceService.getPageableStateOrProvinces(0, 10, 100L)).willReturn(response);

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/paging")
                .param("pageNo", "0")
                .param("pageSize", "10")
                .param("countryId", "100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNo").value(0))
            .andExpect(jsonPath("$.stateOrProvinceContent[0].name").value("Hanoi"));

        verify(stateOrProvinceService).getPageableStateOrProvinces(0, 10, 100L);
    }

    @Test
    void testGetStateAndCountryNames_whenIdsProvided_thenReturnOkAndBody() throws Exception {
        StateOrProvinceAndCountryGetNameVm vm = new StateOrProvinceAndCountryGetNameVm(1L, "Hanoi", "Vietnam");
        given(stateOrProvinceService.getStateOrProvinceAndCountryNames(List.of(1L, 2L))).willReturn(List.of(vm));

        this.mockMvc.perform(get(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/state-country-names")
                .param("stateOrProvinceIds", "1", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stateOrProvinceId").value(1))
            .andExpect(jsonPath("$[0].countryName").value("Vietnam"));

        verify(stateOrProvinceService).getStateOrProvinceAndCountryNames(List.of(1L, 2L));
    }

    @Test
    void testDeleteStateOrProvince_whenValidId_thenReturnNoContent() throws Exception {
        this.mockMvc.perform(delete(Constants.ApiConstant.STATE_OR_PROVINCES_URL + "/9"))
            .andExpect(status().isNoContent());

        verify(stateOrProvinceService).delete(9L);
    }
}