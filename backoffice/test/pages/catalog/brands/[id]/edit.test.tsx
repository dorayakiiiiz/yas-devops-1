import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import BrandEdit from '../../../../../pages/catalog/brands/[id]/edit';
import { getBrand, editBrand } from '../../../../../modules/catalog/services/BrandService';
import { useRouter } from 'next/router';
import { toastError } from '../../../../../common/services/ToastService';
import { ResponseStatus } from '../../../../../constants/Common';

// Mock dependencies
vi.mock('../../../../modules/catalog/services/BrandService', () => ({
  getBrand: vi.fn(),
  editBrand: vi.fn(),
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    replace: vi.fn(),
    query: { id: '1' },
  })),
}));

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('@catalogComponents/BrandGeneralInformation', () => ({
  default: ({ register, errors, setValue, trigger, brand }: any) => (
    <div data-testid="brand-general-info">
      <input
        data-testid="input-name"
        defaultValue={brand?.name}
        {...register('name', { required: true })}
        placeholder="Name"
      />
      <input
        data-testid="input-slug"
        defaultValue={brand?.slug}
        {...register('slug', { required: true })}
        placeholder="Slug"
      />
      <input
        type="checkbox"
        data-testid="checkbox-isPublish"
        defaultChecked={brand?.isPublish}
        {...register('isPublish')}
      />
      {errors.name && <span data-testid="error-name">Name is required</span>}
    </div>
  ),
}));

vi.mock('@commonServices/ResponseStatusHandlingService', () => ({
  handleUpdatingResponse: vi.fn(),
}));

vi.mock('@commonServices/ToastService', () => ({
  toastError: vi.fn(),
  toastSuccess: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('BrandEdit Page', () => {
  const mockRouterPush = vi.fn();
  const mockRouterReplace = vi.fn();
  const mockBrand = {
    id: 1,
    name: 'Nike',
    slug: 'nike',
    isPublish: true,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({
      push: mockRouterPush,
      replace: mockRouterReplace,
      query: { id: '1' },
    });
    (getBrand as any).mockResolvedValue(mockBrand);
    (editBrand as any).mockResolvedValue({ status: ResponseStatus.SUCCESS });
  });

  describe('Rendering', () => {
    it('should render loading state initially', () => {
      (getBrand as any).mockImplementation(() => new Promise(() => {}));
      render(<BrandEdit />);
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should render edit form after loading', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByText('Edit brand: 1')).toBeDefined();
        expect(screen.getByTestId('brand-general-info')).toBeDefined();
      });
    });

    it('should populate form with brand data', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        const nameInput = screen.getByTestId('input-name') as HTMLInputElement;
        const slugInput = screen.getByTestId('input-slug') as HTMLInputElement;
        const publishCheckbox = screen.getByTestId('checkbox-isPublish') as HTMLInputElement;
        
        expect(nameInput.defaultValue).toBe('Nike');
        expect(slugInput.defaultValue).toBe('nike');
        expect(publishCheckbox.checked).toBe(true);
      });
    });

    it('should render Save button', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByText('Save')).toBeDefined();
        expect(screen.getByText('Save')).toHaveAttribute('type', 'submit');
      });
    });

    it('should render Cancel button', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByText('Cancel')).toBeDefined();
      });
    });

    it('should have Cancel button with red background', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        const cancelButton = screen.getByText('Cancel');
        expect(cancelButton).toHaveStyle({ background: 'red', marginLeft: '30px' });
      });
    });

    it('should have Cancel button linked to brands page', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        const cancelLink = links.find(link => link.textContent === 'Cancel');
        expect(cancelLink).toHaveAttribute('href', '/catalog/brands');
      });
    });
  });

  describe('getBrand', () => {
    it('should call getBrand with id from query', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(getBrand).toHaveBeenCalledWith(1);
      });
    });

    it('should handle brand not found', async () => {
      (getBrand as any).mockResolvedValue({ id: null, detail: 'Brand not found' });
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(toastError).toHaveBeenCalledWith('Brand not found');
        expect(mockRouterPush).toHaveBeenCalledWith('/catalog/brands');
      });
    });

    it('should handle API error when fetching brand', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getBrand as any).mockRejectedValue(new Error('Network error'));
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Form Submission', () => {
    it('should call editBrand with correct data when form is submitted', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Nike Updated' } });
      fireEvent.change(slugInput, { target: { value: 'nike-updated' } });
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(editBrand).toHaveBeenCalledWith(1, {
          id: 0,
          name: 'Nike Updated',
          slug: 'nike-updated',
          isPublish: true,
        });
      });
    });

    it('should call editBrand with isPublish false when checkbox unchecked', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      const publishCheckbox = screen.getByTestId('checkbox-isPublish') as HTMLInputElement;
      const saveButton = screen.getByText('Save');

      fireEvent.change(nameInput, { target: { value: 'Adidas' } });
      fireEvent.change(slugInput, { target: { value: 'adidas' } });
      fireEvent.click(publishCheckbox); // Uncheck
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(editBrand).toHaveBeenCalledWith(1, {
          id: 0,
          name: 'Adidas',
          slug: 'adidas',
          isPublish: false,
        });
      });
    });

    it('should redirect to brand list on successful update (status SUCCESS)', async () => {
      (editBrand as any).mockResolvedValue({ status: ResponseStatus.SUCCESS });
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).toHaveBeenCalledWith('/catalog/brands');
      });
    });

    it('should not redirect on non-success status', async () => {
      (editBrand as any).mockResolvedValue({ status: ResponseStatus.BAD_REQUEST });
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(mockRouterReplace).not.toHaveBeenCalled();
      });
    });

    it('should call handleUpdatingResponse after submission', async () => {
      const { handleUpdatingResponse } = await import('@commonServices/ResponseStatusHandlingService');
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(handleUpdatingResponse).toHaveBeenCalled();
      });
    });

    it('should handle API error when updating brand', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (editBrand as any).mockRejectedValue(new Error('Update failed'));
      
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Validation', () => {
    it('should show validation errors when submitting empty form', async () => {
      render(<BrandEdit />);
      
      await waitFor(() => {
        expect(screen.getByTestId('input-name')).toBeDefined();
      });

      const nameInput = screen.getByTestId('input-name');
      const slugInput = screen.getByTestId('input-slug');
      
      fireEvent.change(nameInput, { target: { value: '' } });
      fireEvent.change(slugInput, { target: { value: '' } });
      
      const saveButton = screen.getByText('Save');
      fireEvent.click(saveButton);

      await waitFor(() => {
        expect(screen.getByTestId('error-name')).toBeDefined();
      });
    });
  });

  describe('Edge Cases', () => {
    it('should handle missing id in query', async () => {
      (useRouter as any).mockReturnValue({
        push: mockRouterPush,
        replace: mockRouterReplace,
        query: {},
      });
      render(<BrandEdit />);
      
      // Should not fetch brand without id
      expect(getBrand).not.toHaveBeenCalled();
    });

    it('should handle brand with isPublish false', async () => {
      const unpublishedBrand = { ...mockBrand, isPublish: false };
      (getBrand as any).mockResolvedValue(unpublishedBrand);
      render(<BrandEdit />);
      
      await waitFor(() => {
        const publishCheckbox = screen.getByTestId('checkbox-isPublish') as HTMLInputElement;
        expect(publishCheckbox.checked).toBe(false);
      });
    });
  });
});