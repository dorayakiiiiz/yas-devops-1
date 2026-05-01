import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import EditCustomer from '../../../../../pages/customers/[id]/edit';
import { getCustomer, updateCustomer } from '../../../../../modules/customer/services/CustomerService';
import { useRouter } from 'next/router';
import { ResponseStatus } from '../../../../../constants/Common';

// Mock dependencies
vi.mock('../../../../modules/customer/services/CustomerService', () => ({
  getCustomer: vi.fn(),
  updateCustomer: vi.fn(),
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    replace: vi.fn(),
    query: { id: 'user-1' },
  })),
}));

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('modules/customer/components/CustomerBaseInformation', () => ({
  default: ({ register, errors, customer }: any) => (
    <div data-testid="customer-base-info">
      <input
        data-testid="input-email"
        defaultValue={customer?.email}
        {...register('email', { required: true })}
        placeholder="Email"
      />
      <input
        data-testid="input-firstName"
        defaultValue={customer?.firstName}
        {...register('firstName', { required: true })}
        placeholder="First Name"
      />
      <input
        data-testid="input-lastName"
        defaultValue={customer?.lastName}
        {...register('lastName', { required: true })}
        placeholder="Last Name"
      />
      {errors.email && <span data-testid="error-email">Email is required</span>}
      {errors.firstName && <span data-testid="error-firstName">First name is required</span>}
      {errors.lastName && <span data-testid="error-lastName">Last name is required</span>}
    </div>
  ),
}));

vi.mock('@commonServices/ResponseStatusHandlingService', () => ({
  handleUpdatingResponse: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('EditCustomer Page', () => {
  const mockRouterReplace = vi.fn();
  const mockRouterPush = vi.fn();
  const mockCustomer = {
    id: 'user-1',
    email: 'john@example.com',
    username: 'john_doe',
    firstName: 'John',
    lastName: 'Doe',
    createdTimestamp: new Date('2024-01-15T10:30:00Z'),
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({
      push: mockRouterPush,
      replace: mockRouterReplace,
      query: { id: 'user-1' },
    });
    (getCustomer as any).mockResolvedValue(mockCustomer);
    (updateCustomer as any).mockResolvedValue({ status: ResponseStatus.SUCCESS });
  });

  describe('Rendering', () => {
    it('should render loading state initially', () => {
      (getCustomer as any).mockImplementation(() => new Promise(() => {}));
      render(<EditCustomer />);
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should render edit form after loading', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByText('Update john_doe customer')).toBeDefined();
        expect(screen.getByTestId('customer-base-info')).toBeDefined();
      });
    });

    it('should populate form with customer data', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        const emailInput = screen.getByTestId('input-email') as HTMLInputElement;
        const firstNameInput = screen.getByTestId('input-firstName') as HTMLInputElement;
        const lastNameInput = screen.getByTestId('input-lastName') as HTMLInputElement;
        
        expect(emailInput.defaultValue).toBe('john@example.com');
        expect(firstNameInput.defaultValue).toBe('John');
        expect(lastNameInput.defaultValue).toBe('Doe');
      });
    });

    it('should render Save button', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByText('Save')).toBeDefined();
        expect(screen.getByText('Save')).toHaveAttribute('type', 'submit');
      });
    });

    it('should render Cancel button', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByText('Cancel')).toBeDefined();
      });
    });

    it('should have Cancel button with red background', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        const cancelButton = screen.getByText('Cancel');
        expect(cancelButton).toHaveStyle({ background: 'red', marginLeft: '30px' });
      });
    });

    it('should have Cancel button linked to customers page', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        const cancelLink = links.find(link => link.textContent === 'Cancel');
        expect(cancelLink).toHaveAttribute('href', '/customers');
      });
    });
  });

  describe('getCustomer', () => {
    it('should call getCustomer with id from query', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(getCustomer).toHaveBeenCalledWith('user-1');
      });
    });

    it('should handle customer not found', async () => {
      (getCustomer as any).mockResolvedValue(null);
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByText('No customer')).toBeDefined();
      });
    });

    it('should handle API error when fetching customer', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getCustomer as any).mockRejectedValue(new Error('Network error'));
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });

    it('should not call getCustomer when id is missing', async () => {
      (useRouter as any).mockReturnValue({
        push: mockRouterPush,
        replace: mockRouterReplace,
        query: {},
      });
      render(<EditCustomer />);
      
      expect(getCustomer).not.toHaveBeenCalled();
    });
  });

  describe('Form Submission', () => {
    const fillForm = async () => {
      const emailInput = screen.getByTestId('input-email');
      const firstNameInput = screen.getByTestId('input-firstName');
      const lastNameInput = screen.getByTestId('input-lastName');
      
      fireEvent.change(emailInput, { target: { value: 'updated@example.com' } });
      fireEvent.change(firstNameInput, { target: { value: 'Updated' } });
      fireEvent.change(lastNameInput, { target: { value: 'User' } });
    };

    it('should call updateCustomer with correct data when form is submitted', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-email')).toBeDefined();
      });

      await fillForm();
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(updateCustomer).toHaveBeenCalledWith('user-1', {
          email: 'updated@example.com',
          firstName: 'Updated',
          lastName: 'User',
        });
      });
    });

    it('should redirect to customers list on successful update (status SUCCESS)', async () => {
      (updateCustomer as any).mockResolvedValue({ status: ResponseStatus.SUCCESS });
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-email')).toBeDefined();
      });

      await fillForm();
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).toHaveBeenCalledWith('/customers');
      });
    });

    it('should not redirect on non-success status', async () => {
      (updateCustomer as any).mockResolvedValue({ status: ResponseStatus.BAD_REQUEST });
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-email')).toBeDefined();
      });

      await fillForm();
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).not.toHaveBeenCalled();
      });
    });

    it('should call handleUpdatingResponse after submission', async () => {
      const { handleUpdatingResponse } = await import('@commonServices/ResponseStatusHandlingService');
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-email')).toBeDefined();
      });

      await fillForm();
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(handleUpdatingResponse).toHaveBeenCalled();
      });
    });

    it('should handle API error when updating customer', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (updateCustomer as any).mockRejectedValue(new Error('Update failed'));
      
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-email')).toBeDefined();
      });

      await fillForm();
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Validation', () => {
    it('should show validation errors when submitting empty form', async () => {
      render(<EditCustomer />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-email')).toBeDefined();
      });

      const emailInput = screen.getByTestId('input-email');
      const firstNameInput = screen.getByTestId('input-firstName');
      const lastNameInput = screen.getByTestId('input-lastName');
      
      fireEvent.change(emailInput, { target: { value: '' } });
      fireEvent.change(firstNameInput, { target: { value: '' } });
      fireEvent.change(lastNameInput, { target: { value: '' } });
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(screen.getByTestId('error-email')).toBeDefined();
        expect(screen.getByTestId('error-firstName')).toBeDefined();
        expect(screen.getByTestId('error-lastName')).toBeDefined();
      });
    });
  });

  describe('Edge Cases', () => {
    it('should handle customer with empty fields', async () => {
      const emptyCustomer = {
        id: 'user-2',
        email: '',
        username: 'empty_user',
        firstName: '',
        lastName: '',
        createdTimestamp: new Date(),
      };
      (getCustomer as any).mockResolvedValue(emptyCustomer);
      render(<EditCustomer />);
      
      await waitFor(() => {
        const emailInput = screen.getByTestId('input-email') as HTMLInputElement;
        const firstNameInput = screen.getByTestId('input-firstName') as HTMLInputElement;
        const lastNameInput = screen.getByTestId('input-lastName') as HTMLInputElement;
        
        expect(emailInput.defaultValue).toBe('');
        expect(firstNameInput.defaultValue).toBe('');
        expect(lastNameInput.defaultValue).toBe('');
      });
    });
  });
});