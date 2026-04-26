import { formatPriceVND, formatPriceUSD } from '../../utils/formatPrice';

describe('formatPriceVND', () => {
  test('formats integer price correctly', () => {
    expect(formatPriceVND(100000)).toBe('100.000₫');
  });

  test('formats price with decimals correctly', () => {
    expect(formatPriceVND(100000.5)).toBe('100.000,5₫');
  });

  test('formats zero price correctly', () => {
    expect(formatPriceVND(0)).toBe('0₫');
  });

  test('formats negative price correctly', () => {
    expect(formatPriceVND(-50000)).toBe('-50.000₫');
  });

  test('formats large price correctly', () => {
    expect(formatPriceVND(1000000000)).toBe('1.000.000.000₫');
  });

  test('formats price with thousand separator', () => {
    expect(formatPriceVND(1234567)).toBe('1.234.567₫');
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
    expect(formatPriceVND(0.01)).toBe('0,01₫');
    expect(formatPriceUSD(0.01)).toBe('$0.01');
  });

  test('handles string numbers', () => {
    expect(formatPriceVND('100000')).toBe('100.000₫');
    expect(formatPriceUSD('100')).toBe('$100.00');
  });
});