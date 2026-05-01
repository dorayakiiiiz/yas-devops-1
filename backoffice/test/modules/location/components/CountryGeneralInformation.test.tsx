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

    it('should render Code2 input', () => {
      render(<CountryGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-code2')).toBeDefined();
    });

    it('should render Name input', () => {
      render(<CountryGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-name')).toBeDefined();
    });

    it('should render Code3 input', () => {
      render(<CountryGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-code3')).toBeDefined();
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

  describe('Default Values', () => {
    it('should populate fields with country data when country prop is provided', () => {
      render(<CountryGeneralInformation {...defaultProps} country={mockCountry} />);

      const code2Input = screen.getByTestId('input-code2') as HTMLInputElement;
      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const code3Input = screen.getByTestId('input-code3') as HTMLInputElement;

      expect(code2Input.defaultValue).toBe('US');
      expect(nameInput.defaultValue).toBe('United States');
      expect(code3Input.defaultValue).toBe('USA');
    });

    it('should set checkbox defaultChecked values correctly', () => {
      render(<CountryGeneralInformation {...defaultProps} country={mockCountry} />);

      const billingCheckbox = screen.getByTestId('checkbox-isBillingEnabled') as HTMLInputElement;
      const shippingCheckbox = screen.getByTestId('checkbox-isShippingEnabled') as HTMLInputElement;
      const cityCheckbox = screen.getByTestId('checkbox-isCityEnabled') as HTMLInputElement;
      const zipCodeCheckbox = screen.getByTestId('checkbox-isZipCodeEnabled') as HTMLInputElement;
      const districtCheckbox = screen.getByTestId('checkbox-isDistrictEnabled') as HTMLInputElement;

      expect(billingCheckbox.checked).toBe(true);
      expect(shippingCheckbox.checked).toBe(true);
      expect(cityCheckbox.checked).toBe(false);
      expect(zipCodeCheckbox.checked).toBe(true);
      expect(districtCheckbox.checked).toBe(false);
    });

    it('should render with empty fields when country is undefined', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      const code2Input = screen.getByTestId('input-code2') as HTMLInputElement;
      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const code3Input = screen.getByTestId('input-code3') as HTMLInputElement;

      expect(code2Input.defaultValue).toBeUndefined();
      expect(nameInput.defaultValue).toBeUndefined();
      expect(code3Input.defaultValue).toBeUndefined();
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

    it('should register code3 field without validation', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('code3', undefined);
    });

    it('should register isBillingEnabled checkbox', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isBillingEnabled');
    });

    it('should register isShippingEnabled checkbox', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isShippingEnabled');
    });

    it('should register isCityEnabled checkbox', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isCityEnabled');
    });

    it('should register isZipCodeEnabled checkbox', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isZipCodeEnabled');
    });

    it('should register isDistrictEnabled checkbox', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isDistrictEnabled');
    });

    it('should call register 8 times (2 inputs with options + 1 input without + 5 checkboxes)', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(8);
    });
  });


  describe('Form Interaction', () => {
    it('should handle code2 input change', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      const code2Input = screen.getByTestId('input-code2');
      fireEvent.change(code2Input, { target: { value: 'VN' } });

      expect(code2Input).toHaveValue('VN');
    });

    it('should handle name input change', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'Vietnam' } });

      expect(nameInput).toHaveValue('Vietnam');
    });

    it('should handle code3 input change', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      const code3Input = screen.getByTestId('input-code3');
      fireEvent.change(code3Input, { target: { value: 'VNM' } });

      expect(code3Input).toHaveValue('VNM');
    });

    it('should handle checkbox toggle', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      const billingCheckbox = screen.getByTestId('checkbox-isBillingEnabled') as HTMLInputElement;
      fireEvent.click(billingCheckbox);

      expect(billingCheckbox.checked).toBe(true);
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

    it('should have correct id attributes for all inputs', () => {
      render(<CountryGeneralInformation {...defaultProps} />);

      expect(screen.getByTestId('input-code2')).toHaveAttribute('id', 'code2');
      expect(screen.getByTestId('input-name')).toHaveAttribute('id', 'name');
      expect(screen.getByTestId('input-code3')).toHaveAttribute('id', 'code3');
      expect(screen.getByTestId('checkbox-isBillingEnabled')).toHaveAttribute('id', 'isBillingEnabled');
      expect(screen.getByTestId('checkbox-isShippingEnabled')).toHaveAttribute('id', 'isShippingEnabled');
      expect(screen.getByTestId('checkbox-isCityEnabled')).toHaveAttribute('id', 'isCityEnabled');
      expect(screen.getByTestId('checkbox-isZipCodeEnabled')).toHaveAttribute('id', 'isZipCodeEnabled');
      expect(screen.getByTestId('checkbox-isDistrictEnabled')).toHaveAttribute('id', 'isDistrictEnabled');
    });
  });

  describe('Edge Cases', () => {
    it('should handle country with all false checkboxes', () => {
      const allFalseCountry = {
        ...mockCountry,
        isBillingEnabled: false,
        isShippingEnabled: false,
        isCityEnabled: false,
        isZipCodeEnabled: false,
        isDistrictEnabled: false,
      };

      render(<CountryGeneralInformation {...defaultProps} country={allFalseCountry} />);

      const billingCheckbox = screen.getByTestId('checkbox-isBillingEnabled') as HTMLInputElement;
      const shippingCheckbox = screen.getByTestId('checkbox-isShippingEnabled') as HTMLInputElement;
      const cityCheckbox = screen.getByTestId('checkbox-isCityEnabled') as HTMLInputElement;
      const zipCodeCheckbox = screen.getByTestId('checkbox-isZipCodeEnabled') as HTMLInputElement;
      const districtCheckbox = screen.getByTestId('checkbox-isDistrictEnabled') as HTMLInputElement;

      expect(billingCheckbox.checked).toBe(false);
      expect(shippingCheckbox.checked).toBe(false);
      expect(cityCheckbox.checked).toBe(false);
      expect(zipCodeCheckbox.checked).toBe(false);
      expect(districtCheckbox.checked).toBe(false);
    });

    it('should handle country with all true checkboxes', () => {
      const allTrueCountry = {
        ...mockCountry,
        isBillingEnabled: true,
        isShippingEnabled: true,
        isCityEnabled: true,
        isZipCodeEnabled: true,
        isDistrictEnabled: true,
      };

      render(<CountryGeneralInformation {...defaultProps} country={allTrueCountry} />);

      const billingCheckbox = screen.getByTestId('checkbox-isBillingEnabled') as HTMLInputElement;
      const shippingCheckbox = screen.getByTestId('checkbox-isShippingEnabled') as HTMLInputElement;
      const cityCheckbox = screen.getByTestId('checkbox-isCityEnabled') as HTMLInputElement;
      const zipCodeCheckbox = screen.getByTestId('checkbox-isZipCodeEnabled') as HTMLInputElement;
      const districtCheckbox = screen.getByTestId('checkbox-isDistrictEnabled') as HTMLInputElement;

      expect(billingCheckbox.checked).toBe(true);
      expect(shippingCheckbox.checked).toBe(true);
      expect(cityCheckbox.checked).toBe(true);
      expect(zipCodeCheckbox.checked).toBe(true);
      expect(districtCheckbox.checked).toBe(true);
    });

    it('should handle empty string values', () => {
      const emptyStringCountry = {
        ...mockCountry,
        code2: '',
        code3: '',
        name: '',
      };

      render(<CountryGeneralInformation {...defaultProps} country={emptyStringCountry} />);

      const code2Input = screen.getByTestId('input-code2') as HTMLInputElement;
      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const code3Input = screen.getByTestId('input-code3') as HTMLInputElement;

      expect(code2Input.defaultValue).toBe('');
      expect(nameInput.defaultValue).toBe('');
      expect(code3Input.defaultValue).toBe('');
    });
  });
});