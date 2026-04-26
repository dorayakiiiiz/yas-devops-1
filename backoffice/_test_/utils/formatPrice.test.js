import { formatPriceVND, formatPriceUSD } from '../../utils/formatPrice';

describe('formatPriceVND', () => {
  test('formats integer price correctly', () => {
    const result = formatPriceVND(100000);
    expect(result.trim()).toBe('100.000 ₫');  // Dùng trim()
  });

  test('formats price with decimals correctly', () => {
    const result = formatPriceVND(100000.5);
    expect(result.trim()).toBe('100.001 ₫');
  });

  test('formats zero price correctly', () => {
    const result = formatPriceVND(0);
    expect(result.trim()).toBe('0 ₫');
  });

  test('formats negative price correctly', () => {
    const result = formatPriceVND(-50000);
    expect(result.trim()).toBe('-50.000 ₫');
  });

  test('formats large price correctly', () => {
    const result = formatPriceVND(1000000000);
    expect(result.trim()).toBe('1.000.000.000 ₫');
  });

  test('formats price with thousand separator', () => {
    const result = formatPriceVND(1234567);
    expect(result.trim()).toBe('1.234.567 ₫');
  });
});

describe('formatPriceUSD', () => {
  test('formats integer price correctly', () => {
    expect(formatPriceUSD(100)).toBe('$100.00');
  });

  test('formats price with decimals correctly', () => {
    expect(formatPriceUSD(100.5)).toBe('$100.50');
  });

  test('formats zero price correctly', () => {
    expect(formatPriceUSD(0)).toBe('$0.00');
  });

  test('formats negative price correctly', () => {
    expect(formatPriceUSD(-50.99)).toBe('-$50.99');
  });

  test('formats large price correctly', () => {
    expect(formatPriceUSD(1000000)).toBe('$1,000,000.00');
  });

  test('formats price with thousand separator', () => {
    expect(formatPriceUSD(1234567.89)).toBe('$1,234,567.89');
  });
});

describe('Edge cases', () => {
  test('handles very small numbers', () => {
    const result = formatPriceVND(0.01);
    expect(result.trim()).toBe('0 ₫');
    expect(formatPriceUSD(0.01)).toBe('$0.01');
  });

  test('handles string numbers', () => {
    const result = formatPriceVND('100000');
    expect(result.trim()).toBe('100.000 ₫');
    expect(formatPriceUSD('100')).toBe('$100.00');
  });
});