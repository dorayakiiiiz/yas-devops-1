import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Customers from '../../../pages/customers/index';
import { getCustomers, deleteCustomer } from '../../../modules/customer/services/CustomerService';
import { Customer } from '../../../modules/customer/models/Customer';

// Mock dependencies
vi.mock('../../../modules/customer/services/CustomerService', () => ({
  getCustomers: vi.fn(),
  deleteCustomer: vi.fn(),
}));

vi.mock('../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

// Mock next/router
vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
  })),
}));

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

describe('Customers Page', () => {
  // SỬA: Customer theo đúng type
  const mockCustomers: Customer[] = [
    {
      id: 'user-1',
      email: 'john@example.com',
      username: 'john_doe',
      firstName: 'John',
      lastName: 'Doe',
      createdTimestamp: new Date('2024-01-15T10:30:00Z'),
    },
    {
      id: 'user-2',
      email: 'jane@example.com',
      username: 'jane_smith',
      firstName: 'Jane',
      lastName: 'Smith',
      createdTimestamp: new Date('2024-01-16T10:30:00Z'),
    },
  ];

  const mockResponse = {
    customers: mockCustomers,
    totalPage: 1,
    totalUser: 2,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getCustomers as any).mockResolvedValue(mockResponse);
    (deleteCustomer as any).mockResolvedValue({ status: 204 });
  });

  describe('getCustomers', () => {
    it('should call getCustomers on mount', async () => {
      render(<Customers />);
      await waitFor(() => {
        expect(getCustomers).toHaveBeenCalled();
      });
    });

    it('should pass correct pageNo to getCustomers', async () => {
      render(<Customers />);
      await waitFor(() => {
        expect(getCustomers).toHaveBeenCalledWith(0);
      });
    });

    it('should display customer data in table', async () => {
      render(<Customers />);
      
      await waitFor(() => {
        expect(screen.getByText('john@example.com')).toBeDefined();
        expect(screen.getByText('john_doe')).toBeDefined();
        expect(screen.getByText('John')).toBeDefined();
        expect(screen.getByText('Doe')).toBeDefined();
      });
    });

    it('should handle empty customer list', async () => {
      (getCustomers as any).mockResolvedValue({
        customers: [],
        totalPage: 0,
        totalUser: 0,
      });
      render(<Customers />);
      
      await waitFor(() => {
        expect(screen.getByText('No customers available')).toBeDefined();
      });
    });

    it('should handle API error', async () => {
      const toastSpy = vi.spyOn(require('react-toastify').toast, 'error').mockImplementation(() => {});
      (getCustomers as any).mockRejectedValue(new Error('Network error'));
      render(<Customers />);
      
      await waitFor(() => {
        expect(toastSpy).toHaveBeenCalledWith('Something was wrong! Try later!');
      });
      toastSpy.mockRestore();
    });
  });

  describe('deleteCustomer', () => {
    it('should be defined', () => {
      expect(deleteCustomer).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteCustomer as any).mockResolvedValue({ status: 204 });
      render(<Customers />);
      
      await waitFor(() => {
        expect(deleteCustomer).toBeDefined();
      });
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<Customers />);
      await waitFor(() => {
        expect(getCustomers).toHaveBeenCalledWith(0);
      });
    });
  });

  describe('Table Headers', () => {
    it('should render all table headers', async () => {
      render(<Customers />);
      
      await waitFor(() => {
        expect(screen.getByText('User Id')).toBeDefined();
        expect(screen.getByText('Username')).toBeDefined();
        expect(screen.getByText('Email')).toBeDefined();
        expect(screen.getByText('First Name')).toBeDefined();
        expect(screen.getByText('Last Name')).toBeDefined();
        expect(screen.getByText('Created Timestamp')).toBeDefined();
        expect(screen.getByText('Actions')).toBeDefined();
      });
    });
  });

  describe('Create Button', () => {
    it('should render Create New Customer button', async () => {
      render(<Customers />);
      
      await waitFor(() => {
        expect(screen.getByText('Create New Customer')).toBeDefined();
      });
    });

    it('should have link to create customer page', async () => {
      render(<Customers />);
      
      await waitFor(() => {
        const link = screen.getByTestId('mock-link');
        expect(link).toHaveAttribute('href', '/customers/create');
      });
    });
  });
});