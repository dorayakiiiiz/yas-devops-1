import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TaxClassGeneralInformation from '../../../../modules/tax/components/TaxClassGeneralInformation';
import { TaxClass } from '../../../../modules/tax/models/TaxClass';

// Mock Input component
vi.mock('common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, registerOptions, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        onChange={(e) => {
          if (registerOptions?.onChange) {
            registerOptions.onChange(e);
          }
          if (register) {
            register(field, registerOptions).onChange(e);
          }
        }}
        onBlur={() => register && register(field, registerOptions).onBlur()}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  CheckBox: ({ labelText, field, register, defaultChecked }: any) => (
    <div>
      <label htmlFor={field}>
        <input
          id={field}
          type="checkbox"
          data-testid={`checkbox-${field}`}
          defaultChecked={defaultChecked}
          onChange={(e) => register && register(field).onChange(e)}
        />
        {labelText}
      </label>
    </div>
  ),
}));

describe('TaxClassGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockTaxClass: TaxClass = {
    id: 1,
    name: 'Standard Tax',
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    taxClass: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render Name input field', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByTestId('input-name')).toBeDefined();
    });

    it('should render Name label correctly', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const label = screen.getByText('Name');
      expect(label).toBeDefined();
      expect(label).toHaveAttribute('for', 'name');
    });
  });

  describe('Default Values', () => {
    it('should populate Name field with taxClass data when provided', () => {
      render(<TaxClassGeneralInformation {...defaultProps} taxClass={mockTaxClass} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      expect(nameInput.defaultValue).toBe('Standard Tax');
    });

    it('should render with empty Name field when taxClass is undefined', () => {
      render(<TaxClassGeneralInformation {...defaultProps} taxClass={undefined} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      expect(nameInput.defaultValue).toBeUndefined();
    });

    it('should handle taxClass with empty name', () => {
      const emptyNameTaxClass: TaxClass = {
        id: 1,
        name: '',
      };

      render(<TaxClassGeneralInformation {...defaultProps} taxClass={emptyNameTaxClass} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      expect(nameInput.defaultValue).toBe('');
    });
  });

  describe('Register Options', () => {
    it('should register name field with required validation', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', {
        required: { value: true, message: 'Tax Class name is required' },
      });
    });

    it('should call register exactly 1 time', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(1);
    });
  });


  describe('Form Interaction', () => {
    it('should handle name input change', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'VAT 10%' } });

      expect(nameInput).toHaveValue('VAT 10%');
    });

    it('should handle empty name input', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: '' } });

      expect(nameInput).toHaveValue('');
    });

    it('should handle long name input', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const longName = 'A'.repeat(200);
      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: longName } });

      expect(nameInput).toHaveValue(longName);
    });

    it('should handle special characters in name', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const specialName = 'Tax @#$% Class 123!';
      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: specialName } });

      expect(nameInput).toHaveValue(specialName);
    });
  });

  describe('Props Validation', () => {
    it('should accept partial taxClass data', () => {
      const partialTaxClass = {
        id: 1,
        name: 'Partial Tax',
      };

      render(<TaxClassGeneralInformation {...defaultProps} taxClass={partialTaxClass} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      expect(nameInput.defaultValue).toBe('Partial Tax');
    });
  });

  describe('Accessibility', () => {
    it('should have label associated with name input', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const label = screen.getByText('Name');
      const input = screen.getByTestId('input-name');

      expect(label).toHaveAttribute('for', 'name');
      expect(input).toHaveAttribute('id', 'name');
    });
  });

  describe('Edge Cases', () => {
    it('should work correctly without taxClass prop', () => {
      render(<TaxClassGeneralInformation {...defaultProps} taxClass={undefined} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'New Tax Class' } });

      expect(nameInput).toHaveValue('New Tax Class');
    });

    it('should handle very long tax class name', () => {
      const longNameTaxClass: TaxClass = {
        id: 1,
        name: 'A'.repeat(500),
      };

      render(<TaxClassGeneralInformation {...defaultProps} taxClass={longNameTaxClass} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      expect(nameInput.defaultValue).toBe('A'.repeat(500));
    });
  });
});