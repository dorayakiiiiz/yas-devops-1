import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProfileForm from '../../../../modules/customer/components/ProfileForm';
import { useRouter } from 'next/router';

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

// Mock Input component
vi.mock('../../../common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, disabled, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        disabled={disabled}
        onChange={(e) => register && register(field).onChange(e)}
        onBlur={() => register && register(field).onBlur()}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

describe('ProfileForm', () => {
  const mockRegister = vi.fn((field) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const defaultProps = {
    customer: undefined,
    register: mockRegister,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByText('Username')).toBeDefined();
      expect(screen.getByText('First name')).toBeDefined();
      expect(screen.getByText('Last name')).toBeDefined();
      expect(screen.getByText('Email')).toBeDefined();
    });

    it('should render Username input', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByTestId('input-username')).toBeDefined();
    });

    it('should render First name input', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByTestId('input-firstName')).toBeDefined();
    });

    it('should render Last name input', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByTestId('input-lastName')).toBeDefined();
    });

    it('should render Email input', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByTestId('input-email')).toBeDefined();
    });

    it('should render Update button', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByText('Update')).toBeDefined();
      expect(screen.getByRole('button', { name: 'Update' })).toBeDefined();
    });

    it('should render Cancel button', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByText('Cancel')).toBeDefined();
      expect(screen.getByRole('button', { name: 'Cancel' })).toBeDefined();
    });

    it('should have Update button with type submit', () => {
      render(<ProfileForm {...defaultProps} />);

      const updateButton = screen.getByText('Update');
      expect(updateButton).toHaveAttribute('type', 'submit');
    });

    it('should have Update button with btn-primary class', () => {
      render(<ProfileForm {...defaultProps} />);

      const updateButton = screen.getByText('Update');
      expect(updateButton).toHaveClass('btn', 'btn-primary');
    });

    it('should have Cancel button with btn-secondary and m-3 classes', () => {
      render(<ProfileForm {...defaultProps} />);

      const cancelButton = screen.getByText('Cancel');
      expect(cancelButton).toHaveClass('btn', 'btn-secondary', 'm-3');
    });
  });

  describe('Default Values', () => {
    it('should populate fields with customer data when customer prop is provided', () => {
      const customer = {
        id: 1,
        username: 'john_doe',
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        isPublish: true,
        fullName: 'John Doe',
        createdDate: '2024-01-01',
      };

      render(<ProfileForm {...defaultProps} customer={customer} />);

      const usernameInput = screen.getByTestId('input-username') as HTMLInputElement;
      const firstNameInput = screen.getByTestId('input-firstName') as HTMLInputElement;
      const lastNameInput = screen.getByTestId('input-lastName') as HTMLInputElement;
      const emailInput = screen.getByTestId('input-email') as HTMLInputElement;

      expect(usernameInput.defaultValue).toBe('john_doe');
      expect(firstNameInput.defaultValue).toBe('John');
      expect(lastNameInput.defaultValue).toBe('Doe');
      expect(emailInput.defaultValue).toBe('john@example.com');
    });

    it('should render with empty fields when customer is undefined', () => {
      render(<ProfileForm {...defaultProps} customer={undefined} />);

      const usernameInput = screen.getByTestId('input-username') as HTMLInputElement;
      const firstNameInput = screen.getByTestId('input-firstName') as HTMLInputElement;
      const lastNameInput = screen.getByTestId('input-lastName') as HTMLInputElement;
      const emailInput = screen.getByTestId('input-email') as HTMLInputElement;

      expect(usernameInput.defaultValue).toBeUndefined();
      expect(firstNameInput.defaultValue).toBeUndefined();
      expect(lastNameInput.defaultValue).toBeUndefined();
      expect(emailInput.defaultValue).toBeUndefined();
    });

    it('should handle customer with empty string values', () => {
      const customer = {
        id: 1,
        username: '',
        firstName: '',
        lastName: '',
        email: '',
        isPublish: true,
        fullName: '',
        createdDate: '',
      };

      render(<ProfileForm {...defaultProps} customer={customer} />);

      const usernameInput = screen.getByTestId('input-username') as HTMLInputElement;
      expect(usernameInput.defaultValue).toBe('');
    });
  });

  describe('Username Field', () => {
    it('should render username field as disabled', () => {
      render(<ProfileForm {...defaultProps} />);

      const usernameInput = screen.getByTestId('input-username');
      expect(usernameInput).toBeDisabled();
    });

    it('should pass disabled prop to Input component for username', () => {
      render(<ProfileForm {...defaultProps} />);

      // Username input should have disabled attribute
      const usernameInput = screen.getByTestId('input-username');
      expect(usernameInput).toHaveAttribute('disabled');
    });
  });

  describe('Form Fields Registration', () => {
    it('should register username field', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('username');
    });

    it('should register firstName field', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('firstName');
    });

    it('should register lastName field', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('lastName');
    });

    it('should register email field', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('email');
    });

    it('should call register exactly 4 times', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(4);
    });
  });

  describe('Form Interaction', () => {
    it('should handle firstName input change', async () => {
      const user = userEvent.setup();
      render(<ProfileForm {...defaultProps} />);

      const firstNameInput = screen.getByTestId('input-firstName');
      fireEvent.change(firstNameInput, { target: { value: 'Jane' } });

      expect(firstNameInput).toHaveValue('Jane');
    });

    it('should handle lastName input change', async () => {
      render(<ProfileForm {...defaultProps} />);

      const lastNameInput = screen.getByTestId('input-lastName');
      fireEvent.change(lastNameInput, { target: { value: 'Smith' } });

      expect(lastNameInput).toHaveValue('Smith');
    });

    it('should handle email input change', async () => {
      render(<ProfileForm {...defaultProps} />);

      const emailInput = screen.getByTestId('input-email');
      fireEvent.change(emailInput, { target: { value: 'jane@example.com' } });

      expect(emailInput).toHaveValue('jane@example.com');
    });

    it('should not allow username input change (disabled)', () => {
      render(<ProfileForm {...defaultProps} />);

      const usernameInput = screen.getByTestId('input-username') as HTMLInputElement;
      const originalValue = usernameInput.defaultValue;
      
      fireEvent.change(usernameInput, { target: { value: 'new_username' } });
      
      expect(usernameInput).toHaveValue(originalValue || '');
    });
  });

  describe('Cancel Button Link', () => {
    it('should have Cancel button wrapped in Link component', () => {
      render(<ProfileForm {...defaultProps} />);

      const link = screen.getByTestId('mock-link');
      expect(link).toHaveAttribute('href', '/customers');
    });

    it('should navigate to /customers when Cancel is clicked', () => {
      render(<ProfileForm {...defaultProps} />);

      const cancelButton = screen.getByText('Cancel');
      const link = cancelButton.closest('a');
      
      expect(link).toHaveAttribute('href', '/customers');
    });
  });

  describe('Update Button', () => {
    it('should be clickable', async () => {
      const user = userEvent.setup();
      const onSubmit = vi.fn();
      
      render(
        <form onSubmit={onSubmit}>
          <ProfileForm {...defaultProps} />
        </form>
      );

      const updateButton = screen.getByText('Update');
      await user.click(updateButton);
      
      expect(onSubmit).toHaveBeenCalled();
    });

    it('should have correct button text', () => {
      render(<ProfileForm {...defaultProps} />);

      const updateButton = screen.getByText('Update');
      expect(updateButton.textContent).toBe('Update');
    });
  });

  describe('Loading state handling', () => {
    it('should render with minimal props', () => {
      render(<ProfileForm register={mockRegister} />);
      
      expect(screen.getByText('Username')).toBeDefined();
      expect(screen.getByText('First name')).toBeDefined();
      expect(screen.getByText('Last name')).toBeDefined();
      expect(screen.getByText('Email')).toBeDefined();
    });
  });

  describe('Accessibility', () => {
    it('should have labels associated with inputs via htmlFor', () => {
      render(<ProfileForm {...defaultProps} />);

      const usernameLabel = screen.getByText('Username');
      const usernameInput = screen.getByTestId('input-username');
      
      expect(usernameLabel).toHaveAttribute('for', 'username');
      expect(usernameInput).toHaveAttribute('id', 'username');
    });

    it('should have correct id attributes for all inputs', () => {
      render(<ProfileForm {...defaultProps} />);

      expect(screen.getByTestId('input-username')).toHaveAttribute('id', 'username');
      expect(screen.getByTestId('input-firstName')).toHaveAttribute('id', 'firstName');
      expect(screen.getByTestId('input-lastName')).toHaveAttribute('id', 'lastName');
      expect(screen.getByTestId('input-email')).toHaveAttribute('id', 'email');
    });
  });

  describe('Special characters handling', () => {
    it('should handle firstName with special characters', () => {
      render(<ProfileForm {...defaultProps} />);

      const firstNameInput = screen.getByTestId('input-firstName');
      fireEvent.change(firstNameInput, { target: { value: 'John-Doe_123' } });

      expect(firstNameInput).toHaveValue('John-Doe_123');
    });

    it('should handle email with special format', () => {
      render(<ProfileForm {...defaultProps} />);

      const emailInput = screen.getByTestId('input-email');
      fireEvent.change(emailInput, { target: { value: 'user+tag@example.co.uk' } });

      expect(emailInput).toHaveValue('user+tag@example.co.uk');
    });
  });
});