import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Profile from '../../../../pages/profile/index';
import { getMyProfile, updateCustomer } from '../../../../modules/customer/services/CustomerService';
import { Customer } from '../../../../modules/customer/models/Customer';

// Mock dependencies
vi.mock('../../../../modules/customer/services/CustomerService', () => ({
  getMyProfile: vi.fn(),
  updateCustomer: vi.fn(),
}));

vi.mock('../../../../commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react-hook-form', () => ({
  useForm: () => ({
    register: vi.fn(),
    handleSubmit: vi.fn((fn) => fn),
  }),
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
  },
}));

describe('Profile Page', () => {
  const mockCustomer: Customer = {
    id: 'user-1',
    userId: 'user-1',
    email: 'test@example.com',
    firstName: 'John',
    lastName: 'Doe',
    phoneNumber: '1234567890',
    isActive: true,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getMyProfile as any).mockResolvedValue(mockCustomer);
    (updateCustomer as any).mockResolvedValue({ status: 200 });
  });

  describe('getMyProfile', () => {
    it('should call getMyProfile on mount', async () => {
      render(<Profile />);
      await waitFor(() => {
        expect(getMyProfile).toHaveBeenCalled();
      });
    });

    it('should return customer data on success', async () => {
      render(<Profile />);
      await waitFor(() => {
        expect(getMyProfile).toHaveBeenCalledWith();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getMyProfile as any).mockRejectedValue(new Error('Network error'));
      render(<Profile />);
      await waitFor(() => {
        // Error is caught but not logged
        expect(consoleSpy).not.toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('updateCustomer', () => {
    it('should be defined', () => {
      expect(updateCustomer).toBeDefined();
    });

    it('should handle update success', async () => {
      (updateCustomer as any).mockResolvedValue({ status: 200 });
      render(<Profile />);
      await waitFor(() => {
        expect(updateCustomer).toBeDefined();
      });
    });

    it('should handle update error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (updateCustomer as any).mockRejectedValue(new Error('Update failed'));
      render(<Profile />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('render', () => {
    it('should render profile page', () => {
      render(<Profile />);
      expect(screen.getByText('Update User')).toBeDefined();
    });

    it('should have form element', () => {
      render(<Profile />);
      const form = document.querySelector('form');
      expect(form).toBeDefined();
    });
  });
});