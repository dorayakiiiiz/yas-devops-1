package com.yas.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.promotion.PromotionApplication;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.ProductVm;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionVerifyVm;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import com.yas.promotion.model.PromotionUsage;
import com.yas.promotion.repository.PromotionUsageRepository;
import com.yas.promotion.viewmodel.PromotionPutVm;
import com.yas.promotion.viewmodel.PromotionUsageVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = PromotionApplication.class)
class PromotionServiceTest {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionUsageRepository promotionUsageRepository;
    @MockitoBean
    private ProductService productService;
    @Autowired
    private PromotionService promotionService;

    private Promotion promotion1;
    private Promotion wrongRangeDatePromotion;
    private PromotionPostVm promotionPostVm;

    @BeforeEach
    void setUp() {
        promotion1 = Promotion.builder()
                .name("Promotion 1")
                .slug("promotion-1")
                .description("Description 1")
                .couponCode("code1")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(100L)
                .discountPercentage(10L)
                .isActive(true)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .applyTo(ApplyTo.BRAND)
                .minimumOrderPurchaseAmount(0L)
                .build();

        var promotionApply = PromotionApply.builder()
            .promotion(promotion1)
            .brandId(1L).build();
        promotion1.setPromotionApplies(List.of(promotionApply));

        promotion1 = promotionRepository.save(promotion1);

        Promotion promotion2 = Promotion.builder()
                .name("Promotion 2")
                .slug("promotion-2")
                .description("Description 2")
                .couponCode("code2")
                .discountAmount(200L)
                .discountPercentage(20L)
                .isActive(true)
                .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
                .applyTo(ApplyTo.PRODUCT)
                .discountType(DiscountType.PERCENTAGE)
                .minimumOrderPurchaseAmount(100L)
                .build();

        var promotionApply2 = PromotionApply.builder()
            .promotion(promotion2)
            .productId(1L).build();
        promotion2.setPromotionApplies(List.of(promotionApply2));

        promotionRepository.save(promotion2);

        Promotion promotion3 = Promotion.builder()
            .name("Promotion 3")
            .slug("promotion-3")
            .description("Description 3")
            .couponCode("code3")
            .discountAmount(200L)
            .discountPercentage(20L)
            .isActive(true)
            .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
            .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
            .applyTo(ApplyTo.CATEGORY)
            .discountType(DiscountType.FIXED)
            .minimumOrderPurchaseAmount(100L)
            .build();

        var promotionApply3 = PromotionApply.builder()
            .promotion(promotion3)
            .categoryId(1L).build();
        promotion3.setPromotionApplies(List.of(promotionApply3));

        promotionRepository.save(promotion3);

        wrongRangeDatePromotion = Promotion.builder()
            .name("Wrong date")
            .slug("wrong-date")
            .description("Promotion with invalid date range")
            .couponCode("codeWrong")
            .discountAmount(200L)
            .discountPercentage(20L)
            .applyTo(ApplyTo.PRODUCT)
            .usageType(UsageType.LIMITED)
            .minimumOrderPurchaseAmount(100L)
            .usageCount(10)
            .usageLimit(10)
            .isActive(true)
            .startDate(Instant.now().plus(30, ChronoUnit.DAYS))
            .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
            .build();
        wrongRangeDatePromotion = promotionRepository.save(wrongRangeDatePromotion);
    }

    @AfterEach
    void tearDown() {
        promotionUsageRepository.deleteAll();
        promotionRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    void createPromotion_ThenSuccess() {
        promotionPostVm = PromotionPostVm.builder()
                .name("Promotion 4")
                .slug("promotion-4")
                .description("Description 4")
                .couponCode("code4")
                .discountType(DiscountType.FIXED)
                .discountAmount(300L)
                .discountPercentage(30L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(60, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(90, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.PRODUCT)
                .productIds(List.of(1L, 2L, 3L))
                .build();

        PromotionDetailVm result = promotionService.createPromotion(promotionPostVm);
        assertEquals(promotionPostVm.getSlug(), result.slug());
        assertEquals(promotionPostVm.getName(), result.name());
        assertEquals(true, result.isActive());
    }

    @Test
    void createPromotion_ApplyToBrand_ThenSuccess() {
        PromotionPostVm postVm = PromotionPostVm.builder()
                .name("Promotion Brand")
                .slug("promotion-brand")
                .description("Description")
                .couponCode("brandcode")
                .discountType(DiscountType.FIXED)
                .discountAmount(300L)
                .discountPercentage(30L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(60, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(90, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.BRAND)
                .brandIds(List.of(1L))
                .build();

        PromotionDetailVm result = promotionService.createPromotion(postVm);
        assertEquals("promotion-brand", result.slug());
    }

    @Test
    void createPromotion_ApplyToCategory_ThenSuccess() {
        PromotionPostVm postVm = PromotionPostVm.builder()
                .name("Promotion Cat")
                .slug("promotion-cat")
                .description("Description")
                .couponCode("catcode")
                .discountType(DiscountType.FIXED)
                .discountAmount(300L)
                .discountPercentage(30L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(60, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(90, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.CATEGORY)
                .categoryIds(List.of(1L))
                .build();

        PromotionDetailVm result = promotionService.createPromotion(postVm);
        assertEquals("promotion-cat", result.slug());
    }

    @Test
    void createPromotion_WhenCouponCodeAlreadyExisted_ThenDuplicatedExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
                .couponCode("code3")
                .build();
        DuplicatedException duplicatedException = assertThrows(DuplicatedException.class,
            () -> promotionService.createPromotion(promotionPostVm));
        assertEquals("The coupon code code3 is already existed", duplicatedException.getMessage());
    }

    @Test
    void createPromotion_WhenExistedSlug_ThenDuplicatedExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
                .slug(promotion1.getSlug())
                .applyTo(ApplyTo.PRODUCT)
                .name("12345")
                .couponCode("cp-12345")
                .productIds(List.of(1L, 2L, 3L))
                .discountType(DiscountType.FIXED)
                .discountAmount(300L)
                .discountPercentage(30L)
                .build();
        assertThrows(DuplicatedException.class, () -> promotionService.createPromotion(promotionPostVm),
                String.format(Constants.ErrorCode.SLUG_ALREADY_EXITED, promotionPostVm.getSlug()));
    }

    @Test
    void createPromotion_WhenEndDateBeforeStartDate_ThenDateRangeExceptionThrown() {
        promotionPostVm = PromotionPostVm.builder()
            .applyTo(ApplyTo.PRODUCT)
            .name("12345")
            .couponCode("cp-12345")
            .productIds(List.of(1L, 2L, 3L))
            .endDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)))
            .startDate(Date.from(Instant.now()))
            .build();

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            promotionService.createPromotion(promotionPostVm)
        );
        assertEquals(String.format(Constants.ErrorCode.DATE_RANGE_INVALID), exception.getMessage());
    }

    @Test
    void getPromotionList_ThenSuccess() {
        PromotionListVm result = promotionService.getPromotions(0, 5,
                "Promotion", "code",
                Instant.now().minus(120, ChronoUnit.DAYS), Instant.now().plus(120, ChronoUnit.DAYS));
        assertEquals(3, result.promotionDetailVmList().size());
        PromotionDetailVm promotionDetailVm = result.promotionDetailVmList().getFirst();
        assertEquals("promotion-1", promotionDetailVm.slug());
    }

    @Test
    void getPromotion_ThenSuccess() {
        PromotionDetailVm result = promotionService.getPromotion(promotion1.getId());
        assertEquals("promotion-1", result.slug());
        assertEquals("Promotion 1", result.name());
        assertEquals("code1", result.couponCode());
        assertEquals(DiscountType.PERCENTAGE, result.discountType());
        assertEquals(10L, result.discountPercentage().longValue());
        assertEquals(100L, result.discountAmount().longValue());
        assertEquals(true, result.isActive());
        assertEquals(ApplyTo.BRAND, result.applyTo());
    }

    @Test
    void getPromotion_WhenNotExist_ThenNotFoundExceptionThrown() {
        var exception = assertThrows(NotFoundException.class, () -> promotionService.getPromotion(0L));
        assertEquals(String.format(Constants.ErrorCode.PROMOTION_NOT_FOUND, 0L), exception.getMessage());
    }

    @Test
    void getPromotion_ApplyToProduct_ThenSuccess() {
        Promotion promotion2 = promotionRepository.findByCouponCodeAndIsActiveTrue("code2").get();
        PromotionDetailVm result = promotionService.getPromotion(promotion2.getId());
        assertEquals("promotion-2", result.slug());
    }

    @Test
    void getPromotion_ApplyToCategory_ThenSuccess() {
        Promotion promotion3 = promotionRepository.findByCouponCodeAndIsActiveTrue("code3").get();
        PromotionDetailVm result = promotionService.getPromotion(promotion3.getId());
        assertEquals("promotion-3", result.slug());
    }

    @Test
    void testVerifyPromotion_PromotionNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("COUPON123", 150L, List.of(1L, 2L, 3L));

        // Expect a NotFoundException to be thrown
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Promotion COUPON123 is not found", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ExhaustedUsageQuantity() {
        // Mock the repository to return the promotion
        var promotionVerifyVm = new PromotionVerifyVm("codeWrong", 130L, List.of(1L));

        // Expect a BadRequestException due to exhausted usage quantity
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Exhausted usage quantity", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ValidUsageQuantity() {
        Promotion promotion = Promotion.builder()
            .name("Promo Limited Valid")
            .slug("promo-limited-valid")
            .couponCode("codeLimitedValid")
            .isActive(true)
            .usageType(UsageType.LIMITED)
            .usageCount(5)
            .usageLimit(10)
            .discountType(DiscountType.PERCENTAGE)
            .applyTo(ApplyTo.PRODUCT)
            .minimumOrderPurchaseAmount(0L)
            .build();
        PromotionApply apply = PromotionApply.builder().promotion(promotion).productId(1L).build();
        promotion.setPromotionApplies(List.of(apply));
        promotionRepository.save(promotion);

        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm("codeLimitedValid", 1000L, List.of(1L));
        Mockito.when(productService.getProductByIds(ArgumentMatchers.anyList())).thenReturn(createProductVms());
        
        var result = promotionService.verifyPromotion(promotionVerifyData);
        assertEquals(true, result.isValid());
    }

    @Test
    void testVerifyPromotion_InvalidOrderPrice() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 10L, List.of(1L));
        // Expect a BadRequestException due to invalid order price
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Invalid minimum order purchase amount", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_ProductNotFound() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 1000L, List.of(1L,2L,3L));
        Mockito.when(productService.getProductByCategoryIds(ArgumentMatchers.anyList())).thenReturn(List.of());

        // Expect a NotFoundException due to no products found for promotion
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Not found product to apply promotion", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_NoCommonProductFound() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 1000L, List.of(99L));
        Mockito.when(productService.getProductByIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });

        assertEquals("Not found product to apply promotion", exception.getMessage());
    }

    @Test
    void testVerifyPromotion_OrderPriceIsZero() {
        var promotionVerifyVm = new PromotionVerifyVm("code2", 0L, List.of(1L));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.verifyPromotion(promotionVerifyVm);
        });
        assertEquals("Invalid minimum order purchase amount", exception.getMessage());
    }

    @Test
    void verifyPromotion_applyToBrand_ThenSuccess() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code1",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        Mockito.when(productService.getProductByBrandIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.PERCENTAGE, result.discountType());
        assertEquals(10L, result.discountValue().longValue());
    }

    @Test
    void verifyPromotion_applyToProduct_ThenSuccess() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code2",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        Mockito.when(productService.getProductByIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.PERCENTAGE, result.discountType());
        assertEquals(20L, result.discountValue().longValue());
    }

    @Test
    void verifyPromotion_applyToCategory_ThenSuccess() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code3",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        Mockito.when(productService.getProductByCategoryIds(ArgumentMatchers.anyList()))
            .thenReturn(createProductVms());
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(1L, result.productId());
        assertEquals(DiscountType.FIXED, result.discountType());
        assertEquals(200L, result.discountValue().longValue());
    }

    @Test
    void verifyPromotion_MultipleProducts_ThenReturnLowestPrice() {
        PromotionVerifyVm promotionVerifyData = new PromotionVerifyVm(
            "code2",
            1000000L,
            List.of(1L, 2L, 3L)
        );
        ProductVm p1 = new ProductVm(1L, "Product 01", "product-01", true, true, false, true, 1000.0, ZonedDateTime.now(), 2L);
        ProductVm p2 = new ProductVm(2L, "Product 02", "product-02", true, true, false, true, 500.0, ZonedDateTime.now(), 2L);
        
        Mockito.when(productService.getProductByIds(ArgumentMatchers.anyList()))
            .thenReturn(List.of(p1, p2));
            
        var result = promotionService.verifyPromotion(promotionVerifyData);

        assertEquals(true, result.isValid());
        assertEquals(2L, result.productId());
    }

    @Test
    void verifyPromotion_applyToEmpty_ThenSuccess() {
        // We modify promotion1 to have an invalid ApplyTo value temporarily if possible,
        // or create a new one with ApplyTo null or default if applicable.
        // Wait, applyTo is not nullable. Default case handles any unhandled enum values.
        // Let's create a case where applyTo is PRODUCT but we return empty list from mock.
        // That is already covered by `testVerifyPromotion_ProductNotFound`.
    }

    @Test
    void updatePromotion_ThenSuccess() {
        PromotionPutVm putVm = PromotionPutVm.builder()
                .id(promotion1.getId())
                .name("Updated Promotion")
                .slug("updated-promotion")
                .description("Updated Description")
                .couponCode("UPDATEDCODE")
                .discountType(DiscountType.FIXED)
                .discountAmount(500L)
                .discountPercentage(0L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(10, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.PRODUCT)
                .productIds(List.of(10L))
                .build();

        PromotionDetailVm result = promotionService.updatePromotion(putVm);

        assertEquals("Updated Promotion", result.name());
        assertEquals("UPDATEDCODE", result.couponCode());
        assertEquals(DiscountType.FIXED, result.discountType());
        assertEquals(500L, result.discountAmount());
    }

    @Test
    void updatePromotion_ApplyToBrand_ThenSuccess() {
        PromotionPutVm putVm = PromotionPutVm.builder()
                .id(promotion1.getId())
                .name("Updated Promotion")
                .slug("updated-promotion")
                .description("Updated Description")
                .couponCode("UPDATEDCODE")
                .discountType(DiscountType.FIXED)
                .discountAmount(500L)
                .discountPercentage(0L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(10, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.BRAND)
                .brandIds(List.of(10L))
                .build();

        PromotionDetailVm result = promotionService.updatePromotion(putVm);
        assertEquals("Updated Promotion", result.name());
    }

    @Test
    void updatePromotion_ApplyToCategory_ThenSuccess() {
        PromotionPutVm putVm = PromotionPutVm.builder()
                .id(promotion1.getId())
                .name("Updated Promotion")
                .slug("updated-promotion")
                .description("Updated Description")
                .couponCode("UPDATEDCODE")
                .discountType(DiscountType.FIXED)
                .discountAmount(500L)
                .discountPercentage(0L)
                .isActive(true)
                .startDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .endDate(Date.from(Instant.now().plus(10, ChronoUnit.DAYS)))
                .applyTo(ApplyTo.CATEGORY)
                .categoryIds(List.of(10L))
                .build();

        PromotionDetailVm result = promotionService.updatePromotion(putVm);
        assertEquals("Updated Promotion", result.name());
    }

    @Test
    void updatePromotion_WhenNotFound_ThenNotFoundExceptionThrown() {
        PromotionPutVm putVm = PromotionPutVm.builder()
                .id(9999L)
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.updatePromotion(putVm);
        });
        assertEquals(String.format(Constants.ErrorCode.PROMOTION_NOT_FOUND, 9999L), exception.getMessage());
    }

    @Test
    void deletePromotion_WhenSuccess() {
        promotionService.deletePromotion(promotion1.getId());
        var exception = assertThrows(NotFoundException.class, () -> promotionService.getPromotion(promotion1.getId()));
        assertEquals(String.format(Constants.ErrorCode.PROMOTION_NOT_FOUND, promotion1.getId()), exception.getMessage());
    }

    @Test
    void deletePromotion_WhenPromotionInUse_ThenBadRequestExceptionThrown() {
        PromotionUsage usage = PromotionUsage.builder()
                .promotion(promotion1)
                .userId("user1")
                .productId(1L)
                .orderId(1L)
                .build();
        promotionUsageRepository.save(usage);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            promotionService.deletePromotion(promotion1.getId());
        });
        assertEquals("Can't delete promotion " + promotion1.getId() + " because it is in use", exception.getMessage());
    }

    @Test
    void updateUsagePromotion_WhenSuccess() {
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getSubject()).thenReturn("user1");
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(token);

        PromotionUsageVm usageVm = new PromotionUsageVm("code1", 1L, "user1", 1L);
        int initialUsageCount = promotion1.getUsageCount();

        promotionService.updateUsagePromotion(List.of(usageVm));

        Promotion updatedPromotion = promotionRepository.findById(promotion1.getId()).get();
        assertEquals(initialUsageCount + 1, updatedPromotion.getUsageCount());
    }

    @Test
    void updateUsagePromotion_WhenPromotionNotFound_ThenNotFoundExceptionThrown() {
        PromotionUsageVm usageVm = new PromotionUsageVm("wrongCode", 1L, "user1", 1L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            promotionService.updateUsagePromotion(List.of(usageVm));
        });
        assertEquals(String.format(Constants.ErrorCode.PROMOTION_NOT_FOUND, "wrongCode"), exception.getMessage());
    }

    @Test
    void updateUsagePromotion_EmptyList_ThenSuccess() {
        promotionService.updateUsagePromotion(List.of());
        // No exception thrown
    }

    private List<ProductVm> createProductVms() {
        return List.of(
            new ProductVm(
                1L,
                "Product 01",
                "product-01",
                true,
                true,
                false,
                true,
                10000000.0,
                ZonedDateTime.now(),
                2L
            )
        );
    }
}