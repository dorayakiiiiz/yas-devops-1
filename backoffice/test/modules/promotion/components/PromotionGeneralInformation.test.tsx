import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import PromotionGeneralInformation from '../../../../modules/promotion/components/PromotionGeneralInformation';
import { searchBrands, searchCategories, searchProducts } from '../../../../modules/promotion/services/ProductService';

// Mock dependencies
vi.mock('../../../../modules/promotion/services/ProductService', () => ({
  searchBrands: vi.fn(),
  searchCategories: vi.fn(),
  searchProducts: vi.fn(),
}));

vi.mock('../../../common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, registerOptions, error, type }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        type={type || 'text'}
        {...register(field, registerOptions)}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  Select: ({ labelText, field, placeholder, options, register, registerOptions, error, defaultValue, onChange }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <select
        id={field}
        data-testid={`select-${field}`}
        defaultValue={defaultValue}
        {...register(field, registerOptions)}
        onChange={(e) => {
          if (register) register(field, registerOptions).onChange(e);
          if (onChange) onChange(e);
        }}
      >
        <option value="">{placeholder}</option>
        {options?.map((opt: any) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  TextArea: ({ labelText, field, defaultValue, register, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <textarea id={field} data-testid={`textarea-${field}`} defaultValue={defaultValue} {...register(field)} />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  Switch: ({ labelText, field, defaultChecked, register }: any) => (
    <div>
      <label htmlFor={field}>
        <input
          type="checkbox"
          id={field}
          data-testid={`switch-${field}`}
          defaultChecked={defaultChecked}
          {...register(field)}
        />
        {labelText}
      </label>
    </div>
  ),
  DatePicker: ({ labelText, field, defaultValue, register, registerOptions, error }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        type="date"
        id={field}
        data-testid={`date-${field}`}
        defaultValue={defaultValue}
        {...register(field, registerOptions)}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
}));

vi.mock('../MultipleAutoComplete', () => ({
  default: ({ labelText, options, fetchOptions, onSelect, optionSelectedIds, onRemoveElement, addedOptions }: any) => (
    <div data-testid={`autocomplete-${labelText.toLowerCase()}`}>
      <span>{labelText}</span>
      <button onClick={() => onSelect(options?.[0]?.id)} data-testid="select-option">Select</button>
      {optionSelectedIds.map((id: number) => (
        <div key={id} data-testid={`selected-${id}`}>
          Selected ID: {id}
          <button onClick={() => onRemoveElement(id)}>Remove</button>
        </div>
      ))}
    </div>
  ),
}));

describe('PromotionGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockPromotion = {
    id: 1,
    name: 'Summer Sale',
    slug: 'summer-sale',
    couponCode: 'SUMMER20',
    description: 'Summer discount promotion',
    discountType: 'PERCENTAGE',
    usageType: 'LIMITED',
    applyTo: 'PRODUCT',
    discountPercentage: 20,
    discountAmount: 0,
    usageLimit: 100,
    minimumOrderPurchaseAmount: 50,
    isActive: true,
    startDate: '2024-06-01',
    endDate: '2024-06-30',
    brands: [{ id: 1, name: 'Nike' }],
    categories: [{ id: 1, name: 'Electronics' }],
    products: [{ id: 1, name: 'Laptop' }],
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    promotion: undefined,
    isSubmitting: false,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (searchProducts as any).mockResolvedValue({ productContent: [{ id: 1, name: 'Laptop' }] });
    (searchCategories as any).mockResolvedValue([{ id: 1, name: 'Electronics' }]);
    (searchBrands as any).mockResolvedValue([{ id: 1, name: 'Nike' }]);
  });

  describe('Rendering', () => {
    it('should render all basic fields', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByText('Slug')).toBeDefined();
      expect(screen.getByText('Coupon code')).toBeDefined();
      expect(screen.getByText('Description')).toBeDefined();
      expect(screen.getByText('Discount type')).toBeDefined();
      expect(screen.getByText('Usage type')).toBeDefined();
      expect(screen.getByText('Minimum order purchase amount')).toBeDefined();
      expect(screen.getByText('Active')).toBeDefined();
      expect(screen.getByText('Start date')).toBeDefined();
      expect(screen.getByText('End date')).toBeDefined();
      expect(screen.getByText('Apply to')).toBeDefined();
    });

    it('should render Name input', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-name')).toBeDefined();
    });

    it('should render Slug input', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-slug')).toBeDefined();
    });

    it('should render Coupon code input', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('input-couponCode')).toBeDefined();
    });

    it('should render Description textarea', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(screen.getByTestId('textarea-description')).toBeDefined();
    });
  });

  describe('Discount Type', () => {
    it('should show discount percentage input when PERCENTAGE is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const discountTypeSelect = screen.getByTestId('select-discountType');
      fireEvent.change(discountTypeSelect, { target: { value: 'PERCENTAGE' } });

      await waitFor(() => {
        expect(screen.getByText('Discount percentage')).toBeDefined();
        expect(screen.getByTestId('input-discountPercentage')).toBeDefined();
      });
    });

    it('should show discount amount input when FIXED is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const discountTypeSelect = screen.getByTestId('select-discountType');
      fireEvent.change(discountTypeSelect, { target: { value: 'FIXED' } });

      await waitFor(() => {
        expect(screen.getByText('Discount amount')).toBeDefined();
        expect(screen.getByTestId('input-discountAmount')).toBeDefined();
      });
    });
  });

  describe('Usage Type', () => {
    it('should show usage limit input when LIMITED is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const usageTypeSelect = screen.getByTestId('select-usageType');
      fireEvent.change(usageTypeSelect, { target: { value: 'LIMITED' } });

      await waitFor(() => {
        expect(screen.getByText('Usage limit')).toBeDefined();
        expect(screen.getByTestId('input-usageLimit')).toBeDefined();
      });
    });

    it('should not show usage limit input when UNLIMITED is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const usageTypeSelect = screen.getByTestId('select-usageType');
      fireEvent.change(usageTypeSelect, { target: { value: 'UNLIMITED' } });

      await waitFor(() => {
        expect(screen.queryByText('Usage limit')).toBeNull();
      });
    });
  });

  describe('Apply To', () => {
    it('should show Product autocomplete when PRODUCT is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const applyToSelect = screen.getByTestId('select-applyTo');
      fireEvent.change(applyToSelect, { target: { value: 'PRODUCT' } });

      await waitFor(() => {
        expect(screen.getByTestId('autocomplete-product')).toBeDefined();
      });
    });

    it('should show Category autocomplete when CATEGORY is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const applyToSelect = screen.getByTestId('select-applyTo');
      fireEvent.change(applyToSelect, { target: { value: 'CATEGORY' } });

      await waitFor(() => {
        expect(screen.getByTestId('autocomplete-category')).toBeDefined();
      });
    });

    it('should show Brand autocomplete when BRAND is selected', async () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      
      const applyToSelect = screen.getByTestId('select-applyTo');
      fireEvent.change(applyToSelect, { target: { value: 'BRAND' } });

      await waitFor(() => {
        expect(screen.getByTestId('autocomplete-brand')).toBeDefined();
      });
    });
  });


  describe('Date Conversion', () => {
    it('should convert date to string format', () => {
      const promotionWithDate = {
        ...mockPromotion,
        startDate: new Date('2024-06-01'),
        endDate: new Date('2024-06-30'),
      };
      render(<PromotionGeneralInformation {...defaultProps} promotion={promotionWithDate as any} />);
      
      const startDateInput = screen.getByTestId('date-startDate') as HTMLInputElement;
      const endDateInput = screen.getByTestId('date-endDate') as HTMLInputElement;
      
      expect(startDateInput.defaultValue).toBe('2024-06-01');
      expect(endDateInput.defaultValue).toBe('2024-06-30');
    });
  });

  describe('Register Options', () => {
    it('should register name with required validation', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(mockRegister).toHaveBeenCalledWith('name', expect.objectContaining({
        required: { value: true, message: 'Name is required' },
      }));
    });

    it('should register slug with required validation', () => {
      render(<PromotionGeneralInformation {...defaultProps} />);
      expect(mockRegister).toHaveBeenCalledWith('slug', expect.objectContaining({
        required: { value: true, message: 'Slug is required' },
      }));
    });
  });

  describe('Edge Cases', () => {
    it('should handle undefined promotion', () => {
      render(<PromotionGeneralInformation {...defaultProps} promotion={undefined} />);
      
      expect(screen.getByTestId('input-name')).toBeDefined();
      expect(screen.getByTestId('input-slug')).toBeDefined();
    });
  });
});