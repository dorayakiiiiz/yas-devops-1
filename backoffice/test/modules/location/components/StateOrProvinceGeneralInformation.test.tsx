import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import StateOrProvinceGeneralInformation from '../../../../modules/location/components/StateOrProvinceGeneralInformation';
import slugify from 'slugify';

// Mock slugify
vi.mock('slugify', () => ({
  default: vi.fn((str) => str.toLowerCase().replace(/\s/g, '-')),
}));

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
}));

describe('StateOrProvinceGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockStateOrProvince = {
    id: 1,
    name: 'California',
    code: 'CA',
    type: 'State',
    countryId: 1,
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    stateOrProvince: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByText('Code')).toBeDefined();
      expect(screen.getByText('Type')).toBeDefined();
    });

    it('should render Name input', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-name')).toBeDefined();
    });

    it('should render Code input', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-code')).toBeDefined();
    });

    it('should render Type input', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-type')).toBeDefined();
    });
  });

  describe('Default Values', () => {
    it('should populate fields with stateOrProvince data when prop is provided', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} stateOrProvince={mockStateOrProvince} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const codeInput = screen.getByTestId('input-code') as HTMLInputElement;
      const typeInput = screen.getByTestId('input-type') as HTMLInputElement;

      expect(nameInput.defaultValue).toBe('California');
      expect(codeInput.defaultValue).toBe('CA');
      expect(typeInput.defaultValue).toBe('State');
    });

    it('should render with empty fields when stateOrProvince is undefined', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const codeInput = screen.getByTestId('input-code') as HTMLInputElement;
      const typeInput = screen.getByTestId('input-type') as HTMLInputElement;

      expect(nameInput.defaultValue).toBeUndefined();
      expect(codeInput.defaultValue).toBeUndefined();
      expect(typeInput.defaultValue).toBeUndefined();
    });
  });

  describe('Register Options', () => {
    it('should register name field with required validation', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', {
        required: { value: true, message: 'State Or Province name is required' },
      });
    });

    it('should register code field without validation', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('code', undefined);
    });

    it('should register type field without validation', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('type', undefined);
    });

    it('should call register 3 times', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(3);
    });
  });


  describe('Form Interaction', () => {
    it('should handle name input change', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'Texas' } });

      expect(nameInput).toHaveValue('Texas');
    });

    it('should handle code input change', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const codeInput = screen.getByTestId('input-code');
      fireEvent.change(codeInput, { target: { value: 'TX' } });

      expect(codeInput).toHaveValue('TX');
    });

    it('should handle type input change', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const typeInput = screen.getByTestId('input-type');
      fireEvent.change(typeInput, { target: { value: 'Province' } });

      expect(typeInput).toHaveValue('Province');
    });

    it('should handle empty name input', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: '' } });

      expect(nameInput).toHaveValue('');
    });

    it('should handle empty code input', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const codeInput = screen.getByTestId('input-code');
      fireEvent.change(codeInput, { target: { value: '' } });

      expect(codeInput).toHaveValue('');
    });
  });

  describe('Accessibility', () => {
    it('should have labels associated with inputs via htmlFor', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const nameLabel = screen.getByText('Name');
      const nameInput = screen.getByTestId('input-name');
      
      expect(nameLabel).toHaveAttribute('for', 'name');
      expect(nameInput).toHaveAttribute('id', 'name');
    });

    it('should have correct id attributes for all inputs', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(screen.getByTestId('input-name')).toHaveAttribute('id', 'name');
      expect(screen.getByTestId('input-code')).toHaveAttribute('id', 'code');
      expect(screen.getByTestId('input-type')).toHaveAttribute('id', 'type');
    });
  });

  describe('Edge Cases', () => {
    it('should handle stateOrProvince with empty string values', () => {
      const emptyStringState = {
        ...mockStateOrProvince,
        name: '',
        code: '',
        type: '',
      };

      render(<StateOrProvinceGeneralInformation {...defaultProps} stateOrProvince={emptyStringState} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const codeInput = screen.getByTestId('input-code') as HTMLInputElement;
      const typeInput = screen.getByTestId('input-type') as HTMLInputElement;

      expect(nameInput.defaultValue).toBe('');
      expect(codeInput.defaultValue).toBe('');
      expect(typeInput.defaultValue).toBe('');
    });

    it('should handle special characters in name', () => {
      const specialCharState = {
        ...mockStateOrProvince,
        name: 'New York & Co.',
      };

      render(<StateOrProvinceGeneralInformation {...defaultProps} stateOrProvince={specialCharState} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      expect(nameInput.defaultValue).toBe('New York & Co.');
    });

    it('should handle long text inputs', () => {
      const longName = 'A'.repeat(500);
      const longCode = 'B'.repeat(100);
      const longType = 'C'.repeat(50);

      const longTextState = {
        ...mockStateOrProvince,
        name: longName,
        code: longCode,
        type: longType,
      };

      render(<StateOrProvinceGeneralInformation {...defaultProps} stateOrProvince={longTextState} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const codeInput = screen.getByTestId('input-code') as HTMLInputElement;
      const typeInput = screen.getByTestId('input-type') as HTMLInputElement;

      expect(nameInput.defaultValue).toBe(longName);
      expect(codeInput.defaultValue).toBe(longCode);
      expect(typeInput.defaultValue).toBe(longType);
    });
  });

  describe('Props Validation', () => {
    it('should accept partial stateOrProvince data', () => {
      const partialState = {
        id: 1,
        name: 'Florida',
      };

      render(<StateOrProvinceGeneralInformation {...defaultProps} stateOrProvince={partialState as any} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const codeInput = screen.getByTestId('input-code') as HTMLInputElement;
      const typeInput = screen.getByTestId('input-type') as HTMLInputElement;

      expect(nameInput.defaultValue).toBe('Florida');
      expect(codeInput.defaultValue).toBeUndefined();
      expect(typeInput.defaultValue).toBeUndefined();
    });
  });

  describe('No Props Inputs', () => {
    it('should work correctly without stateOrProvince prop', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      const codeInput = screen.getByTestId('input-code');
      const typeInput = screen.getByTestId('input-type');

      fireEvent.change(nameInput, { target: { value: 'New State' } });
      fireEvent.change(codeInput, { target: { value: 'NS' } });
      fireEvent.change(typeInput, { target: { value: 'Territory' } });

      expect(nameInput).toHaveValue('New State');
      expect(codeInput).toHaveValue('NS');
      expect(typeInput).toHaveValue('Territory');
    });
  });
});