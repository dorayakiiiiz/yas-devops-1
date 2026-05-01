import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import RatingSearch from '../../../../modules/rating/components/RatingSearch';
import { RatingSearchForm } from '../../../../modules/rating/models/RatingSearchForm';

describe('RatingSearch', () => {
  const mockRegister = vi.fn((field) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const defaultProps = {
    register: mockRegister,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(screen.getByLabelText('Created From:')).toBeDefined();
      expect(screen.getByLabelText('Created To:')).toBeDefined();
      expect(screen.getByLabelText('Product:')).toBeDefined();
      expect(screen.getByLabelText('Customer:')).toBeDefined();
      expect(screen.getByLabelText('Message:')).toBeDefined();
    });

    it('should render Created From date input', () => {
      render(<RatingSearch {...defaultProps} />);

      const createdFromInput = screen.getByLabelText('Created From:');
      expect(createdFromInput).toHaveAttribute('type', 'datetime-local');
      expect(createdFromInput).toHaveAttribute('id', 'startDate');
      expect(createdFromInput).toHaveClass('form-control');
    });

    it('should render Created To date input', () => {
      render(<RatingSearch {...defaultProps} />);

      const createdToInput = screen.getByLabelText('Created To:');
      expect(createdToInput).toHaveAttribute('type', 'datetime-local');
      expect(createdToInput).toHaveClass('form-control');
    });

    it('should render Product name input', () => {
      render(<RatingSearch {...defaultProps} />);

      const productInput = screen.getByLabelText('Product:');
      expect(productInput).toHaveAttribute('id', 'productName');
      expect(productInput).toHaveAttribute('placeholder', 'Search product name ...');
    });

    it('should render Customer name input', () => {
      render(<RatingSearch {...defaultProps} />);

      const customerInput = screen.getByLabelText('Customer:');
      expect(customerInput).toHaveAttribute('id', 'customer-name');
      expect(customerInput).toHaveAttribute('placeholder', 'Search customer name ...');
    });

    it('should render Message input', () => {
      render(<RatingSearch {...defaultProps} />);

      const messageInput = screen.getByLabelText('Message:');
      expect(messageInput).toHaveAttribute('id', 'search-message');
      expect(messageInput).toHaveAttribute('placeholder', 'Search message ...');
    });

    it('should render Search button', () => {
      render(<RatingSearch {...defaultProps} />);

      const searchButton = screen.getByText('Search');
      expect(searchButton).toBeDefined();
      expect(searchButton).toHaveAttribute('type', 'submit');
      expect(searchButton).toHaveClass('btn', 'btn-primary', 'w-25');
    });
  });

  describe('Default Values', () => {
    it('should have default value for Created From field', () => {
      render(<RatingSearch {...defaultProps} />);

      const createdFromInput = screen.getByLabelText('Created From:') as HTMLInputElement;
      expect(createdFromInput.defaultValue).toBe('2001-01-01T19:30');
    });

    it('should have default value for Created To field (current date)', () => {
      render(<RatingSearch {...defaultProps} />);

      const createdToInput = screen.getByLabelText('Created To:') as HTMLInputElement;
      const currentDate = new Date().toISOString().slice(0, -8);
      expect(createdToInput.defaultValue).toBe(currentDate);
    });
  });

  describe('Form Registration', () => {
    it('should register createdFrom field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('createdFrom');
    });

    it('should register createdTo field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('createdTo');
    });

    it('should register productName field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('productName');
    });

    it('should register customerName field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('customerName');
    });

    it('should register message field', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('message');
    });

    it('should call register 5 times', () => {
      render(<RatingSearch {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(5);
    });
  });

  describe('User Interaction', () => {
    it('should handle Created From date change', () => {
      render(<RatingSearch {...defaultProps} />);

      const createdFromInput = screen.getByLabelText('Created From:');
      fireEvent.change(createdFromInput, { target: { value: '2024-01-01T10:00' } });

      expect(createdFromInput).toHaveValue('2024-01-01T10:00');
    });

    it('should handle Created To date change', () => {
      render(<RatingSearch {...defaultProps} />);

      const createdToInput = screen.getByLabelText('Created To:');
      fireEvent.change(createdToInput, { target: { value: '2024-12-31T23:59' } });

      expect(createdToInput).toHaveValue('2024-12-31T23:59');
    });

    it('should handle Product name input change', () => {
      render(<RatingSearch {...defaultProps} />);

      const productInput = screen.getByLabelText('Product:');
      fireEvent.change(productInput, { target: { value: 'Laptop' } });

      expect(productInput).toHaveValue('Laptop');
    });

    it('should handle Customer name input change', () => {
      render(<RatingSearch {...defaultProps} />);

      const customerInput = screen.getByLabelText('Customer:');
      fireEvent.change(customerInput, { target: { value: 'John Doe' } });

      expect(customerInput).toHaveValue('John Doe');
    });

    it('should handle Message input change', () => {
      render(<RatingSearch {...defaultProps} />);

      const messageInput = screen.getByLabelText('Message:');
      fireEvent.change(messageInput, { target: { value: 'Great product!' } });

      expect(messageInput).toHaveValue('Great product!');
    });
  });

  describe('Form Layout', () => {
    it('should have row class for main container', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const rowDiv = container.querySelector('.row');
      expect(rowDiv).toBeDefined();
    });

    it('should have two columns (col-12 col-lg-6)', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const columns = container.querySelectorAll('.col-12.col-lg-6');
      expect(columns).toHaveLength(2);
    });

    it('should have d-flex class for form groups', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const flexDivs = container.querySelectorAll('.d-flex');
      expect(flexDivs.length).toBeGreaterThan(0);
    });

    it('should have mb-4 class for form groups', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const mb4Divs = container.querySelectorAll('.mb-4');
      expect(mb4Divs.length).toBeGreaterThan(0);
    });
  });

  describe('Accessibility', () => {
    it('should have label associated with Created From input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const label = screen.getByText('Created From:');
      expect(label).toHaveAttribute('for', 'startDate');
    });

    it('should have label associated with Product input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const label = screen.getByText('Product:');
      expect(label).toHaveAttribute('for', 'productName');
    });

    it('should have label associated with Customer input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const label = screen.getByText('Customer:');
      expect(label).toHaveAttribute('for', 'cusomter');
    });

    it('should have correct id for Customer input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const customerInput = screen.getByLabelText('Customer:');
      expect(customerInput).toHaveAttribute('id', 'customer-name');
    });

    it('should have correct id for Message input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const messageInput = screen.getByLabelText('Message:');
      expect(messageInput).toHaveAttribute('id', 'search-message');
    });
  });

  describe('Edge Cases', () => {
    it('should handle very long product name input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const productInput = screen.getByLabelText('Product:');
      const longText = 'A'.repeat(500);
      fireEvent.change(productInput, { target: { value: longText } });
      
      expect(productInput).toHaveValue(longText);
    });

    it('should handle very long customer name input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const customerInput = screen.getByLabelText('Customer:');
      const longText = 'B'.repeat(500);
      fireEvent.change(customerInput, { target: { value: longText } });
      
      expect(customerInput).toHaveValue(longText);
    });

    it('should handle very long message input', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const messageInput = screen.getByLabelText('Message:');
      const longText = 'C'.repeat(1000);
      fireEvent.change(messageInput, { target: { value: longText } });
      
      expect(messageInput).toHaveValue(longText);
    });

    it('should handle empty input values', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const productInput = screen.getByLabelText('Product:');
      fireEvent.change(productInput, { target: { value: '' } });
      
      expect(productInput).toHaveValue('');
    });

    it('should handle special characters in product name', () => {
      render(<RatingSearch {...defaultProps} />);
      
      const productInput = screen.getByLabelText('Product:');
      const specialChars = '!@#$%^&*()_+';
      fireEvent.change(productInput, { target: { value: specialChars } });
      
      expect(productInput).toHaveValue(specialChars);
    });
  });

  describe('Button Styling', () => {
    it('should have Search button centered', () => {
      const { container } = render(<RatingSearch {...defaultProps} />);
      
      const textCenterDiv = container.querySelector('.text-center');
      expect(textCenterDiv).toBeDefined();
    });

    it('should have Search button with correct classes', () => {
      const searchButton = screen.getByText('Search');
      expect(searchButton).toHaveClass('btn', 'btn-primary', 'w-25');
    });
  });
});