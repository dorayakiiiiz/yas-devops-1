import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CountryGeneralInformation from '../../../../modules/location/components/CountryGeneralInformation';

// Mock Input và CheckBox components
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

describe('CountryGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockCountry = {
    id: 1,
    code2: 'US',
    code3: 'USA',
    name: 'United States',
    isBillingEnabled: true,
    isShippingEnabled: true,
    isCityEnabled: false,
    isZipCodeEnabled: true,
    isDistrictEnabled: false,
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    country: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Code2')).toBeDefined();
      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByText('Code3')).toBeDefined();
      expect(screen.getByText('isBillingEnabled')).toBeDefined();
      expect(screen.getByText('IisShippingEnabled')).toBeDefined();
      expect(screen.getByText('isCityEnabled')).toBeDefined();
      expect(screen.getByText('isZipCodeEnabled')).toBeDefined();
      expect(screen.getByText('isDistrictEnabled')).toBeDefined();
    });

    it('should render all checkboxes', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(screen.getByTestId('checkbox-isBillingEnabled')).toBeDefined();
      expect(screen.getByTestId('checkbox-isShippingEnabled')).toBeDefined();
      expect(screen.getByTestId('checkbox-isCityEnabled')).toBeDefined();
      expect(screen.getByTestId('checkbox-isZipCodeEnabled')).toBeDefined();
      expect(screen.getByTestId('checkbox-isDistrictEnabled')).toBeDefined();
    });
  });

  describe('Register Options', () => {
    it('should register code2 field with required validation', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('code2', {
        required: { value: true, message: 'Code2 is required' },
      });
    });

    it('should register name field with required validation', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', {
        required: { value: true, message: 'Country name is required' },
      });
    });

    it('should call register 8 times (2 inputs with options + 1 input without + 5 checkboxes)', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(8);
    });
  });

  describe('Accessibility', () => {
    it('should have labels associated with inputs', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      const code2Label = screen.getByText('Code2');
      const code2Input = screen.getByTestId('input-code2');

      expect(code2Label).toHaveAttribute('for', 'code2');
      expect(code2Input).toHaveAttribute('id', 'code2');
    });
  });

});