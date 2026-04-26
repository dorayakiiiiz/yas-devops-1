// test/common/components/AuthenticationInfo.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import AuthenticationInfo from '../../../common/components/AuthenticationInfo';
import apiClientService from '../../../common/services/ApiClientService';

// Mock apiClientService
vi.mock('../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
  },
}));

describe('AuthenticationInfo', () => {
  const mockUser = { username: 'john_doe' };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render correctly when user is authenticated', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        expect(screen.getByText('Signed in as:')).toBeDefined();
        expect(screen.getByText('john_doe')).toBeDefined();
        expect(screen.getByText('Logout')).toBeDefined();
      });
    });

    it('should display user profile link correctly', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        const profileLink = screen.getByRole('link', { name: 'john_doe' });
        // Kiểm tra href bằng cách lấy attribute
        expect(profileLink.getAttribute('href')).toBe('/profile');
      });
    });

    it('should display logout link correctly', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        const logoutLink = screen.getByRole('link', { name: 'Logout' });
        expect(logoutLink.getAttribute('href')).toBe('/logout');
      });
    });
  });

  describe('API calls', () => {
    it('should call apiClientService.get with correct URL', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      expect(apiClientService.get).toHaveBeenCalledWith('/authentication/user');
      expect(apiClientService.get).toHaveBeenCalledTimes(1);
    });

    it('should call apiClientService.get only once on mount', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        expect(apiClientService.get).toHaveBeenCalledTimes(1);
      });
    });

    it('should handle API error gracefully', async () => {
      (apiClientService.get as any).mockRejectedValue(new Error('API Error'));

      render(<AuthenticationInfo />);

      await waitFor(() => {
        expect(screen.getByText('Signed in as:')).toBeDefined();
      });
    });
  });

  describe('User data', () => {
    it('should display different usernames correctly', async () => {
      const users = [
        { username: 'alice' },
        { username: 'bob' },
        { username: 'charlie@example.com' },
      ];

      for (const user of users) {
        (apiClientService.get as any).mockResolvedValue({
          json: async () => user,
        });

        const { unmount } = render(<AuthenticationInfo />);

        await waitFor(() => {
          expect(screen.getByText(user.username)).toBeDefined();
        });

        unmount();
        vi.clearAllMocks();
      }
    });

    it('should handle empty username', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => ({ username: '' }),
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        const link = screen.getByRole('link', { name: '' });
        expect(link).toBeDefined();
      });
    });
  });

  describe('Navigation', () => {
    it('should have correct navigation links', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        const links = screen.getAllByRole('link');
        expect(links.length).toBe(2);
        expect(links[0].getAttribute('href')).toBe('/profile');
        expect(links[1].getAttribute('href')).toBe('/logout');
      });
    });
  });

  describe('Link order', () => {
    it('should render links in correct order', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        const links = screen.getAllByRole('link');
        expect(links[0].textContent).toBe('john_doe');
        expect(links[1].textContent).toBe('Logout');
      });
    });
  });

  describe('Text content', () => {
    it('should have correct text content', async () => {
      (apiClientService.get as any).mockResolvedValue({
        json: async () => mockUser,
      });

      render(<AuthenticationInfo />);

      await waitFor(() => {
        const text = screen.getByText(/Signed in as:/).textContent;
        expect(text).toContain('Signed in as:');
        expect(text).toContain('john_doe');
        expect(text).toContain('Logout');
      });
    });
  });
});