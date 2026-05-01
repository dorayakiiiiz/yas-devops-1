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

    it('should render Name label correctly', () => {
      render(<TaxClassGeneralInformation {...defaultProps} />);

      const label = screen.getByText('Name');
      expect(label).toBeDefined();
      expect(label).toHaveAttribute('for', 'name');
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

});