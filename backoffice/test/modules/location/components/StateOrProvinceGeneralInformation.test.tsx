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


  });


  describe('Register Options', () => {
    it('should register name field with required validation', () => {
      render(<StateOrProvinceGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', {
        required: { value: true, message: 'State Or Province name is required' },
      });
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


  describe('Edge Cases', () => {

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


});