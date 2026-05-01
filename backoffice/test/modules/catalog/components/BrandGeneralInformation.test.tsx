import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import BrandGeneralInformation from '../../../../modules/catalog/components/BrandGeneralInformation';
import slugify from 'slugify';

// Mock slugify
vi.mock('slugify', () => ({
  default: vi.fn((str) => str.toLowerCase().replace(/\s/g, '-')),
}));

// Mock Input and Switch components
vi.mock('../../../common/items/Input', () => ({
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
  Switch: ({ labelText, field, defaultChecked, register }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        type="checkbox"
        id={field}
        data-testid={`switch-${field}`}
        defaultChecked={defaultChecked}
        onChange={(e) => register && register(field).onChange(e)}
      />
    </div>
  ),
}));

describe('BrandGeneralInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    brand: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (slugify as any).mockClear();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(screen.getByLabelText('Name')).toBeDefined();
      expect(screen.getByLabelText('Slug')).toBeDefined();
      expect(screen.getByLabelText('Publish')).toBeDefined();
    });

    it('should render Name input with correct label', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Name')).toBeDefined();
      expect(screen.getByTestId('input-name')).toBeDefined();
    });

    it('should render Slug input with correct label', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Slug')).toBeDefined();
      expect(screen.getByTestId('input-slug')).toBeDefined();
    });

    it('should render Publish switch with correct label', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(screen.getByText('Publish')).toBeDefined();
      expect(screen.getByTestId('switch-isPublish')).toBeDefined();
    });

  });

  describe('Default Values', () => {

    it('should render with empty fields when brand is undefined', () => {
      render(<BrandGeneralInformation {...defaultProps} brand={undefined} />);

      const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
      const slugInput = screen.getByTestId('input-slug') as HTMLInputElement;

      expect(nameInput.defaultValue).toBeUndefined();
      expect(slugInput.defaultValue).toBeUndefined();
    });
  });

  describe('Register Options', () => {
    it('should register name field with required validation', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', expect.objectContaining({
        required: { value: true, message: 'Brand name is required' },
      }));
    });

    it('should register slug field with required and pattern validation', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('slug', expect.objectContaining({
        required: { value: true, message: 'Slug brand is required' },
        pattern: expect.objectContaining({
          value: expect.any(RegExp),
          message: expect.stringContaining('Slug must not contain special characters'),
        }),
      }));
    });

    it('should register isPublish field', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isPublish', undefined);
    });
  });

  describe('onNameChange Handler', () => {

    it('should trigger validation for slug, name, and isPublish after name change', async () => {
      (slugify as any).mockReturnValue('test-brand');

      render(<BrandGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'Test Brand' } });

      await waitFor(() => {
        expect(mockTrigger).toHaveBeenCalledWith('slug');
        expect(mockTrigger).toHaveBeenCalledWith('name');
        expect(mockTrigger).toHaveBeenCalledWith('isPublish');
        expect(mockTrigger).toHaveBeenCalledTimes(3);
      });
    });

    it('should handle empty name input', async () => {
      (slugify as any).mockReturnValue('');

      render(<BrandGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: '' } });

      await waitFor(() => {
        expect(slugify).toHaveBeenCalledWith('', { lower: true, strict: true });
        expect(mockSetValue).toHaveBeenCalledWith('slug', '');
      });
    });

    it('should handle name with special characters', async () => {
      (slugify as any).mockReturnValue('test-brand-123');

      render(<BrandGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      fireEvent.change(nameInput, { target: { value: 'Test Brand 123!' } });

      await waitFor(() => {
        expect(slugify).toHaveBeenCalledWith('Test Brand 123!', { lower: true, strict: true });
        expect(mockSetValue).toHaveBeenCalledWith('slug', 'test-brand-123');
      });
    });

    it('should handle multiple rapid changes', async () => {
      (slugify as any)
        .mockReturnValueOnce('nike')
        .mockReturnValueOnce('adidas')
        .mockReturnValueOnce('puma');

      render(<BrandGeneralInformation {...defaultProps} />);

      const nameInput = screen.getByTestId('input-name');
      
      fireEvent.change(nameInput, { target: { value: 'Nike' } });
      fireEvent.change(nameInput, { target: { value: 'Adidas' } });
      fireEvent.change(nameInput, { target: { value: 'Puma' } });

      await waitFor(() => {
        expect(mockSetValue).toHaveBeenLastCalledWith('slug', 'puma');
        expect(mockTrigger).toHaveBeenCalledTimes(9); // 3 changes × 3 triggers
      });
    });
  });

  describe('Form Integration', () => {
    it('should call register with correct field names', () => {
      render(<BrandGeneralInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('name', expect.any(Object));
      expect(mockRegister).toHaveBeenCalledWith('slug', expect.any(Object));
      expect(mockRegister).toHaveBeenCalledWith('isPublish', undefined);
    });
  });
});