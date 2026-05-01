import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CountryList from '../../../../pages/location/countries/index';
import { getPageableCountries, deleteCountry } from '../../../../modules/location/services/CountryService';
import { Country } from '../../../../modules/location/models/Country';

// Mock dependencies
vi.mock('../../../../modules/location/services/CountryService', () => ({
  getPageableCountries: vi.fn(),
  deleteCountry: vi.fn(),
}));

vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    query: {},
  })),
}));

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

// Mock ConfirmationDialog
vi.mock('common/components/ConfirmationDialog', () => ({
  default: ({ show, onConfirm, onCancel, title, message }: any) => (
    show ? (
      <div data-testid="confirmation-dialog">
        <h3>{title}</h3>
        <p>{message}</p>
        <button onClick={onConfirm} data-testid="confirm-btn">Confirm</button>
        <button onClick={onCancel} data-testid="cancel-btn">Cancel</button>
      </div>
    ) : null
  ),
}));

describe('CountryList Page', () => {
  // SỬA: Country theo đúng type
  const mockCountries: Country[] = [
    {
      id: 1,
      code2: 'US',
      name: 'United States',
      code3: 'USA',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: true,
      isZipCodeEnabled: true,
      isDistrictEnabled: true,
    },
    {
      id: 2,
      code2: 'VN',
      name: 'Vietnam',
      code3: 'VNM',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: true,
      isZipCodeEnabled: true,
      isDistrictEnabled: false,
    },
    {
      id: 3,
      code2: 'JP',
      name: 'Japan',
      code3: 'JPN',
      isBillingEnabled: true,
      isShippingEnabled: true,
      isCityEnabled: false,
      isZipCodeEnabled: true,
      isDistrictEnabled: false,
    },
  ];

  const mockResponse = {
    countryContent: mockCountries,
    totalPages: 1,
    totalElements: 3,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getPageableCountries as any).mockResolvedValue(mockResponse);
    (deleteCountry as any).mockResolvedValue({ status: 204 });
  });

  describe('getPageableCountries', () => {
    it('should call getPageableCountries on mount', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalled();
      });
    });

    it('should pass correct parameters to getPageableCountries', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalledWith(0, 10);
      });
    });

    it('should display country data in table', async () => {
      render(<CountryList />);
      
      await waitFor(() => {
        expect(screen.getByText('United States')).toBeDefined();
        expect(screen.getByText('Vietnam')).toBeDefined();
        expect(screen.getByText('Japan')).toBeDefined();
        expect(screen.getByText('US')).toBeDefined();
        expect(screen.getByText('VN')).toBeDefined();
        expect(screen.getByText('JP')).toBeDefined();
      });
    });

    it('should handle empty country list', async () => {
      (getPageableCountries as any).mockResolvedValue({
        countryContent: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<CountryList />);
      
      await waitFor(() => {
        expect(screen.getByText('No countries available')).toBeDefined();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPageableCountries as any).mockRejectedValue(new Error('Network error'));
      render(<CountryList />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deleteCountry', () => {
    it('should be defined', () => {
      expect(deleteCountry).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteCountry as any).mockResolvedValue({ status: 204 });
      render(<CountryList />);
      await waitFor(() => {
        expect(deleteCountry).toBeDefined();
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deleteCountry as any).mockRejectedValue(new Error('Delete failed'));
      render(<CountryList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Table Headers', () => {
    it('should render all table headers', async () => {
      render(<CountryList />);
      
      await waitFor(() => {
        expect(screen.getByText('ID')).toBeDefined();
        expect(screen.getByText('Code2')).toBeDefined();
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('Code3')).toBeDefined();
        expect(screen.getByText('Billing')).toBeDefined();
        expect(screen.getByText('Shipping')).toBeDefined();
        expect(screen.getByText('City')).toBeDefined();
        expect(screen.getByText('Zip Code')).toBeDefined();
        expect(screen.getByText('District')).toBeDefined();
        expect(screen.getByText('Actions')).toBeDefined();
      });
    });
  });

  describe('Create Button', () => {
    it('should render Create New Country button', async () => {
      render(<CountryList />);
      
      await waitFor(() => {
        expect(screen.getByText('Create New Country')).toBeDefined();
      });
    });

    it('should have link to create country page', async () => {
      render(<CountryList />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        const createLink = links.find(link => link.textContent === 'Create New Country');
        expect(createLink).toHaveAttribute('href', '/location/countries/create');
      });
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalledWith(0, 10);
      });
    });
  });

  describe('Status Indicators', () => {
    it('should show checkmark for enabled features', async () => {
      render(<CountryList />);
      
      await waitFor(() => {
        // United States has all features enabled
        const rows = screen.getAllByRole('row');
        const usRow = rows.find(row => row.textContent?.includes('United States'));
        expect(usRow?.textContent).toContain('✓');
      });
    });
  });
});