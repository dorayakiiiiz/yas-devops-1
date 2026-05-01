import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import TaxRateGeneralInformation from '../../../../modules/tax/components/TaxRateGeneralInformation';
import { useRouter } from 'next/router';
import { getTaxClasses } from '../../../../modules/tax/services/TaxClassService';
import { getCountries } from '../../../../modules/location/services/CountryService';
import { getStateOrProvincesByCountry } from '../../../../modules/location/services/StateOrProvinceService';

// Mock next/router
vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

// Mock services
vi.mock('../../../../modules/tax/services/TaxClassService', () => ({
  getTaxClasses: vi.fn(),
}));

vi.mock('../../../../modules/location/services/CountryService', () => ({
  getCountries: vi.fn(),
}));

vi.mock('../../../../modules/location/services/StateOrProvinceService', () => ({
  getStateOrProvincesByCountry: vi.fn(),
}));

// Mock components
vi.mock('@commonItems/OptionSelect', () => ({
  OptionSelect: ({ labelText, field, placeholder, options, register, registerOptions, error, defaultValue }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <select
        id={field}
        data-testid={`select-${field}`}
        defaultValue={defaultValue}
        onChange={(e) => {
          if (registerOptions?.onChange) {
            registerOptions.onChange(e);
          }
          if (register) {
            register(field, registerOptions).onChange(e);
          }
        }}
      >
        <option value="">{placeholder}</option>
        {options?.map((opt: any) => (
          <option key={opt.id} value={opt.id}>
            {opt.name}
          </option>
        ))}
      </select>
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

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
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  CheckBox: () => <div>CheckBox Mock</div>,
}));

describe('TaxRateGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockTaxClasses = [
    { id: 1, name: 'Standard Tax' },
    { id: 2, name: 'Reduced Tax' },
  ];

  const mockCountries = [
    { id: 1, name: 'United States' },
    { id: 2, name: 'Canada' },
  ];

  const mockStateOrProvinces = [
    { id: 1, name: 'California', countryId: 1 },
    { id: 2, name: 'Texas', countryId: 1 },
  ];

  const mockTaxRate = {
    id: 1,
    taxClassId: 1,
    countryId: 1,
    stateOrProvinceId: 1,
    rate: 10.5,
    zipCode: '90210',
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    taxRate: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({ query: {} });
    (getTaxClasses as any).mockResolvedValue(mockTaxClasses);
    (getCountries as any).mockResolvedValue(mockCountries);
    (getStateOrProvincesByCountry as any).mockResolvedValue(mockStateOrProvinces);
  });

  describe('Rendering', () => {
    it('should render all form fields', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('Tax Class')).toBeDefined();
        expect(screen.getByText('Country')).toBeDefined();
        expect(screen.getByText('State Or Province')).toBeDefined();
        expect(screen.getByText('Rate')).toBeDefined();
        expect(screen.getByText('Zip Code')).toBeDefined();
      });
    });

    it('should render Tax Class select', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('select-taxClassId')).toBeDefined();
      });
    });

    it('should render Country select', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('select-countryId')).toBeDefined();
      });
    });

    it('should render State Or Province select', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('select-stateOrProvinceId')).toBeDefined();
      });
    });

    it('should render Rate input', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('input-rate')).toBeDefined();
      });
    });

    it('should render Zip Code input', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('input-zipCode')).toBeDefined();
      });
    });
  });

  describe('Initial Data Fetching', () => {
    it('should fetch tax classes on mount', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(getTaxClasses).toHaveBeenCalledTimes(1);
      });
    });

    it('should fetch countries on mount', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(getCountries).toHaveBeenCalledTimes(1);
      });
    });
  });

  describe('Edit Mode', () => {
    beforeEach(() => {
      (useRouter as any).mockReturnValue({ query: { id: '1' } });
    });

    it('should show "No tax rate" when taxRate is not found', () => {
      render(<TaxRateGeneralInformation {...defaultProps} taxRate={undefined} />);

      expect(screen.getByText('No tax rate')).toBeDefined();
    });

  });

  describe('Country Selection', () => {
    it('should fetch state/provinces when country changes', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('select-countryId')).toBeDefined();
      });

      const countrySelect = screen.getByTestId('select-countryId');
      fireEvent.change(countrySelect, { target: { value: '1' } });

      await waitFor(() => {
        expect(getStateOrProvincesByCountry).toHaveBeenCalledWith('1');
      });
    });
  });

  describe('Registration', () => {
    it('should register taxClassId with required validation', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(mockRegister).toHaveBeenCalledWith('taxClassId', {
          required: { value: true, message: 'Please select tax class' },
        });
      });
    });

    it('should register countryId with required validation and onChange', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(mockRegister).toHaveBeenCalledWith('countryId', {
          required: { value: true, message: 'Please select country' },
          onChange: expect.any(Function),
        });
      });
    });

    it('should register stateOrProvinceId with required validation', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(mockRegister).toHaveBeenCalledWith('stateOrProvinceId', {
          required: { value: true, message: 'Please select state or province' },
        });
      });
    });

    it('should register rate field', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(mockRegister).toHaveBeenCalledWith('rate', undefined);
      });
    });

    it('should register zipCode field', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(mockRegister).toHaveBeenCalledWith('zipCode', undefined);
      });
    });
  });

  describe('Form Interaction', () => {
    it('should handle rate input change', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('input-rate')).toBeDefined();
      });

      const rateInput = screen.getByTestId('input-rate');
      fireEvent.change(rateInput, { target: { value: '15.5' } });

      expect(rateInput).toHaveValue('15.5');
    });

    it('should handle zip code input change', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByTestId('input-zipCode')).toBeDefined();
      });

      const zipCodeInput = screen.getByTestId('input-zipCode');
      fireEvent.change(zipCodeInput, { target: { value: '12345' } });

      expect(zipCodeInput).toHaveValue('12345');
    });
  });

  describe('Select Options', () => {
    it('should populate tax class options', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('Standard Tax')).toBeDefined();
        expect(screen.getByText('Reduced Tax')).toBeDefined();
      });
    });

    it('should populate country options', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        expect(screen.getByText('United States')).toBeDefined();
        expect(screen.getByText('Canada')).toBeDefined();
      });
    });
  });

  describe('Accessibility', () => {
    it('should have labels associated with selects', async () => {
      render(<TaxRateGeneralInformation {...defaultProps} />);

      await waitFor(() => {
        const taxClassLabel = screen.getByText('Tax Class');
        const countryLabel = screen.getByText('Country');
        const stateLabel = screen.getByText('State Or Province');

        expect(taxClassLabel).toHaveAttribute('for', 'taxClassId');
        expect(countryLabel).toHaveAttribute('for', 'countryId');
        expect(stateLabel).toHaveAttribute('for', 'stateOrProvinceId');
      });
    });
  });
});