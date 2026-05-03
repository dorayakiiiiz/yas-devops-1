package com.yas.product.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductConverterTest {

    @Test
    void testToSlug_WithSimpleText_ReturnsLowercaseSlug() {
        // Given
        String input = "Simple Product Name";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("simple-product-name");
        assertThat(result).isLowerCase();
    }

    @Test
    void testToSlug_WithLeadingAndTrailingSpaces_TrimsAndReturnsSlug() {
        // Given
        String input = "  Product Name  ";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("product-name");
        assertThat(result).doesNotStartWith("-");
        assertThat(result).doesNotEndWith("-");
    }

    @Test
    void testToSlug_WithUppercaseLetters_ConvertsToLowercase() {
        // Given
        String input = "PRODUCT NAME WITH UPPERCASE";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("product-name-with-uppercase");
        assertThat(result).isLowerCase();
    }

    @ParameterizedTest
    @CsvSource({
        "'Product Name', 'product-name'",
        "'Hello World', 'hello-world'",
        "'Java Programming', 'java-programming'",
        "'Test   with    spaces', 'test-with-spaces'"
    })
    void testToSlug_WithSpaces_ReplacesWithHyphens(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
        assertThat(result).doesNotContain(" ");
    }

    @ParameterizedTest
    @CsvSource({
        "'product-name', 'product-name'",
        "'product--name', 'product-name'",
        "'product---name', 'product-name'",
        "'----product----name----', 'product-name'"
    })
    void testToSlug_WithMultipleHyphens_ReplacesWithSingleHyphen(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
        assertThat(result).doesNotContain("--");
    }

    @ParameterizedTest
    @CsvSource({
        "'Product@Name', 'product-name'",
        "'Product#Name$', 'product-name'",
        "'Product!@#$%^&*()Name', 'product-name'",
        "'Product_123_Name', 'product-123-name'"
    })
    void testToSlug_WithSpecialCharacters_ReplacesWithHyphens(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
        assertThat(result).matches("^[a-z0-9\\-]+$");
    }

    @ParameterizedTest
    @CsvSource({
        "'123 Product', '123-product'",
        "'Product 456', 'product-456'",
        "'12345', '12345'",
        "'product123name', 'product123name'"
    })
    void testToSlug_WithNumbers_KeepsNumbers(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
        assertThat(result).containsPattern(".*\\d.*");
    }

    @ParameterizedTest
    @CsvSource({
        "'Product.Name', 'product-name'",
        "'Product.Name.With.Dots', 'product-name-with-dots'",
        "'file.txt', 'file-txt'"
    })
    void testToSlug_WithDots_ReplacesWithHyphens(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
        assertThat(result).doesNotContain(".");
    }

    @ParameterizedTest
    @CsvSource({
        "'Product/Name', 'product-name'",
        "'Product\\Name', 'product-name'",
        "'Product|Name', 'product-name'"
    })
    void testToSlug_WithSlashesAndPipes_ReplacesWithHyphens(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "'Điện thoại', 'i-n-tho-i'",  // Vietnamese with diacritics
        "'Café', 'caf'",               // Special e
        "'Müller', 'm-ller'",          // German umlaut
        "'Niño', 'ni-o'",              // Spanish n with tilde
        "' façade', 'fa-ade'"          // French c with cedilla
    })
    void testToSlug_WithAccentedCharacters_ReplacesWithHyphens(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testToSlug_WithMixedAlphanumericAndSpecialChars() {
        // Given
        String input = "My Awesome Product 2024!@# Special $$ Edition";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("my-awesome-product-2024-special-edition");
        assertThat(result).matches("^[a-z0-9\\-]+$");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n", "\t\t   "})
    void testToSlug_WithNullOrEmptyOrWhitespace_ReturnsEmptyOrTrivialSlug(String input) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        // After trim(), if empty string, the regex will produce an empty string
        // Starting hyphen removal will also produce empty string
        assertThat(result).isEmpty();
    }

    @Test
    void testToSlug_WithOnlySpecialCharacters_ReturnsEmptyString() {
        // Given
        String input = "!@#$%^&*()";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        // All characters are replaced with hyphens, then collapsed to empty
        assertThat(result).isEmpty();
    }

    @Test
    void testToSlug_WithOnlyHyphens_ReturnsEmptyString() {
        // Given
        String input = "-----";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testToSlug_WithLeadingHyphens_RemovesLeadingHyphen() {
        // Given
        String input = "-product-name";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("product-name");
        assertThat(result).doesNotStartWith("-");
    }

    @Test
    void testToSlug_WithTrailingHyphens_KeepsTrailingHyphenRemoval() {
        // Given
        String input = "product-name-";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        // The regex will replace, but toSlug only removes leading hyphens, not trailing
        assertThat(result).isEqualTo("product-name");
    }

    @Test
    void testToSlug_WithComplexInput_ReturnsValidSlug() {
        // Given
        String input = "!!!The   ULTIMATE   Product---2024!!!";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("the-ultimate-product-2024");
        assertThat(result).matches("^[a-z0-9\\-]+$");
        assertThat(result).doesNotContain("---");
        assertThat(result).doesNotContain("!!!");
    }

    @ParameterizedTest
    @MethodSource("provideSlugTestCases")
    void testToSlug_WithVariousInputs_ReturnsExpectedSlug(String input, String expected) {
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideSlugTestCases() {
        return Stream.of(
            Arguments.of("Hello World!", "hello-world"),
            Arguments.of("  Hello   World  ", "hello-world"),
            Arguments.of("Hello-World", "hello-world"),
            Arguments.of("Hello---World", "hello-world"),
            Arguments.of("-Hello-World-", "hello-world"),
            Arguments.of("123 Hello 456", "123-hello-456"),
            Arguments.of("Hello@#$%World", "hello-world"),
            Arguments.of("Product (Version 2.0)", "product-version-2-0"),
            Arguments.of("What's up?", "what-s-up"),
            Arguments.of("C++ Programming", "c-programming"),
            Arguments.of("100% Pure", "100-pure"),
            Arguments.of("Price: $19.99", "price-19-99"),
            Arguments.of("Email: test@example.com", "email-test-example-com"),
            Arguments.of("", ""),
            Arguments.of("   ", ""),
            Arguments.of("a", "a"),
            Arguments.of("A", "a")
        );
    }

    @Test
    void testToSlug_ShouldNotContainConsecutiveHyphens() {
        // Given
        String input = "a!!b!!c!!d";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).doesNotContain("--");
    }

    @Test
    void testToSlug_ShouldOnlyContainAllowedCharacters() {
        // Given
        String input = "Allowed: a-z, 0-9, and hyphens";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).matches("^[a-z0-9\\-]*$");
    }

    @Test
    void testToSlug_WithVeryLongInput_HandlesGracefully() {
        // Given
        String input = "a".repeat(1000) + " " + "b".repeat(1000);
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("-");
        assertThat(result.length()).isLessThan(input.length());
    }

    @Test
    void testToSlug_ConsistencyTest() {
        // Given
        String input = "My Product";
        
        // When
        String result1 = ProductConverter.toSlug(input);
        String result2 = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isSameAs(result2); // Should return same value
    }

    @Test
    void testToSlug_WithUnicodeCharacters_HandlesCorrectly() {
        // Given
        String input = "Unicode 中文 日本語 한국어";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        // Non-ASCII characters are replaced with hyphens
        assertThat(result).isEqualTo("unicode");
        assertThat(result).doesNotContain("中文");
    }

    @Test
    void testToSlug_WithEmojis_ReplacesWithHyphens() {
        // Given
        String input = "Product 😀🎉✨ Name";
        
        // When
        String result = ProductConverter.toSlug(input);
        
        // Then
        assertThat(result).isEqualTo("product-name");
        assertThat(result).doesNotContain("😀");
    }
}