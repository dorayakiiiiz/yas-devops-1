import { describe, it, expect } from 'vitest';
import { concatQueryString } from '../../utils/concatQueryString';

describe('concatQueryString', () => {
  it('should return original url when array is empty', () => {
    const url = 'https://example.com/api/users';
    const result = concatQueryString([], url);
    expect(result).toBe('https://example.com/api/users');
  });

  it('should add single query string with ? prefix', () => {
    const url = 'https://example.com/api/users';
    const result = concatQueryString(['page=1'], url);
    expect(result).toBe('https://example.com/api/users?page=1');
  });

  it('should add multiple query strings with & prefix for subsequent items', () => {
    const url = 'https://example.com/api/users';
    const result = concatQueryString(['page=1', 'limit=10', 'sort=asc'], url);
    expect(result).toBe('https://example.com/api/users?page=1&limit=10&sort=asc');
  });

  it('should work with url that already has query string', () => {
    const url = 'https://example.com/api/users?id=123';
    const result = concatQueryString(['page=1', 'limit=10'], url);
    expect(result).toBe('https://example.com/api/users?id=123?page=1&limit=10');
    // Note: This shows a potential bug - should handle existing query strings
  });

  it('should handle empty string in array', () => {
    const url = 'https://example.com/api/users';
    const result = concatQueryString(['', 'page=1'], url);
    expect(result).toBe('https://example.com/api/users?&page=1');
  });

  it('should handle special characters in query strings', () => {
    const url = 'https://example.com/api/search';
    const result = concatQueryString(['q=hello world', 'filter=name&age'], url);
    expect(result).toBe('https://example.com/api/search?q=hello world&filter=name&age');
  });

  it('should handle array with one empty string', () => {
    const url = 'https://example.com/api/users';
    const result = concatQueryString([''], url);
    expect(result).toBe('https://example.com/api/users?');
  });

  it('should handle large number of query parameters', () => {
    const url = 'https://example.com/api/users';
    const queries = Array.from({ length: 100 }, (_, i) => `param${i}=value${i}`);
    const result = concatQueryString(queries, url);
    
    expect(result).toContain('?param0=value0');
    expect(result).toContain('&param99=value99');
    expect(result.split('&').length).toBe(101); // 1 base url + 100 params
  });
});