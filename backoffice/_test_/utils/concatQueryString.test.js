import { concatQueryString } from '../../utils/concatQueryString';

describe('concatQueryString', () => {
  test('returns original url when array is empty', () => {
    const url = 'https://api.example.com/users';
    const result = concatQueryString([], url);
    expect(result).toBe('https://api.example.com/users');
  });

  test('adds single query parameter with question mark', () => {
    const url = 'https://api.example.com/users';
    const result = concatQueryString(['page=1'], url);
    expect(result).toBe('https://api.example.com/users?page=1');
  });

  test('adds multiple query parameters with ampersand', () => {
    const url = 'https://api.example.com/users';
    const result = concatQueryString(['page=1', 'limit=10', 'sort=asc'], url);
    expect(result).toBe('https://api.example.com/users?page=1&limit=10&sort=asc');
  });

  test('works with URL that already has query parameters', () => {
    const url = 'https://api.example.com/users?status=active';
    const result = concatQueryString(['page=1', 'limit=10'], url);
    expect(result).toBe('https://api.example.com/users?status=active?page=1&limit=10');
  });

  test('handles special characters in query parameters', () => {
    const url = 'https://api.example.com/search';
    const result = concatQueryString(['q=hello world', 'category=tech&gaming'], url);
    expect(result).toBe('https://api.example.com/search?q=hello world&category=tech&gaming');
  });

  test('handles array with one empty string', () => {
    const url = 'https://api.example.com/users';
    const result = concatQueryString([''], url);
    expect(result).toBe('https://api.example.com/users?');
  });

  test('handles large number of parameters', () => {
    const url = 'https://api.example.com/users';
    const params = Array.from({ length: 10 }, (_, i) => `param${i}=value${i}`);
    const result = concatQueryString(params, url);
    
    expect(result).toContain('?param0=value0');
    expect(result).toContain('&param9=value9');
    expect(result.split('&').length).toBe(11); 
  });

  test('preserves URL structure', () => {
    const url = 'https://api.example.com/users/123/profile';
    const result = concatQueryString(['view=full', 'edit=false'], url);
    expect(result).toBe('https://api.example.com/users/123/profile?view=full&edit=false');
  });
});