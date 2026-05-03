package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
@DisplayName("MediaService Tests")
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private MediaService mediaService;

    private final String mediaServiceUrl = "http://media-service";
    private final String jwtToken = "mock-jwt-token";

    @BeforeEach
    void setUp() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn(jwtToken);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(serviceUrlConfig.media()).thenReturn(mediaServiceUrl);
    }

    @Test
    @DisplayName("Should create MediaService with all dependencies")
    void testMediaServiceInitialization() {
        assertNotNull(mediaService);
    }

    @Test
    @DisplayName("Should handle getMedia with null id")
    void testGetMediaWithNullId() {
        NoFileMediaVm result = mediaService.getMedia(null);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.url());
        assertEquals("", result.caption());
        assertEquals("", result.fileName());
        verify(restClient, never()).get();
    }

    @Test
    @DisplayName("Should call media service API when getMedia with valid id")
    void testGetMediaWithValidId() {
        Long mediaId = 1L;
        URI expectedUri = URI.create(mediaServiceUrl + "/medias/" + mediaId);
        NoFileMediaVm expectedResponse = new NoFileMediaVm(mediaId, "test.jpg", "Test caption", "http://url.com/test.jpg", "test.jpg");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(expectedUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        NoFileMediaVm result = mediaService.getMedia(mediaId);

        assertNotNull(result);
        assertEquals(mediaId, result.id());
        verify(restClient).get();
    }

    @Test
    @DisplayName("Should save file successfully")
    void testSaveFileSuccess() {
        MultipartFile mockFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            MediaType.IMAGE_JPEG_VALUE, 
            "test image content".getBytes()
        );
        String caption = "Test Caption";
        String fileNameOverride = "custom-name.jpg";

        URI expectedUri = URI.create(mediaServiceUrl + "/medias");
        NoFileMediaVm expectedResponse = new NoFileMediaVm(1L, "custom-name.jpg", caption, "http://url.com/custom-name.jpg", fileNameOverride);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(expectedUri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultipartBodyBuilder.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedResponse);

        NoFileMediaVm result = mediaService.saveFile(mockFile, caption, fileNameOverride);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    @DisplayName("Should remove media successfully")
    void testRemoveMediaSuccess() {
        Long mediaId = 1L;
        URI expectedUri = URI.create(mediaServiceUrl + "/medias/" + mediaId);

        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(expectedUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        assertDoesNotThrow(() -> mediaService.removeMedia(mediaId));
        
        verify(restClient).delete();
        verify(requestHeadersSpec).headers(argThat(headersConsumer -> {
            HttpHeaders headers = new HttpHeaders();
            headersConsumer.accept(headers);
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
            return authHeader != null && authHeader.equals("Bearer " + jwtToken);
        }));
    }

    @Test
    @DisplayName("Should handle multiple media objects")
    void testMultipleMediaObjects() {
        NoFileMediaVm media1 = new NoFileMediaVm(1L, "image1.jpg", "url1", "caption1", "override1");
        NoFileMediaVm media2 = new NoFileMediaVm(2L, "image2.jpg", "url2", "caption2", "override2");
        NoFileMediaVm media3 = new NoFileMediaVm(3L, "image3.jpg", "url3", "caption3", "override3");

        assertNotNull(media1);
        assertNotNull(media2);
        assertNotNull(media3);
        assertNotEquals(media1.id(), media2.id());
        assertNotEquals(media2.id(), media3.id());
        assertNotEquals(media1.url(), media2.url());
    }

    @Test
    @DisplayName("Should handle media equals and hashCode")
    void testMediaEqualsAndHashCode() {
        NoFileMediaVm media1 = new NoFileMediaVm(1L, "image.jpg", "url", "caption", "override");
        NoFileMediaVm media2 = new NoFileMediaVm(1L, "image.jpg", "url", "caption", "override");

        assertEquals(media1, media2);
        assertEquals(media1.hashCode(), media2.hashCode());
    }
}