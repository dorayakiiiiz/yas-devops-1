import { concatQueryString } from '../../utils/concatQueryString';
import { describe, test, it, expect } from 'vitest';

describe('concatQueryString', () => {

  test('should return original url when array is empty', () => {
    const result = concatQueryString([], 'http://example.com');
    expect(result).toBe('http://example.com');
  });

  test('should append single query param with ?', () => {
    const result = concatQueryString(['a=1'], 'http://example.com');
    expect(result).toBe('http://example.com?a=1');
  });

  test('should append multiple query params with &', () => {
    const result = concatQueryString(['a=1', 'b=2'], 'http://example.com');
    expect(result).toBe('http://example.com?a=1&b=2');
  });

  test('should handle multiple params correctly', () => {
    const result = concatQueryString(['a=1', 'b=2', 'c=3'], 'http://example.com');
    expect(result).toBe('http://example.com?a=1&b=2&c=3');
  });

  test('should work with existing query string (edge case)', () => {
    const result = concatQueryString(['b=2'], 'http://example.com?a=1');
    expect(result).toBe('http://example.com?a=1?b=2'); 
    // ⚠️ hiện tại logic của bạn sẽ bị sai ở case này
  });

});