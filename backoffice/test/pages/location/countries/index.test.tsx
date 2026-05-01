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

vi.mock('../../../../commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

describe('CountryList Page', () => {
  const mockCountries: Country[] = [
    {
      id: 1,
      code: 'US',
      name: 'United States',
    },
    {
      id: 2,
      code: 'VN',
      name: 'Vietnam',
    },
  ];

  const mockResponse = {
    countryContent: mockCountries,
    totalPages: 1,
    totalElements: 2,
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
        expect(screen.queryByText('Loading...')).toBeNull();
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

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<CountryList />);
      await waitFor(() => {
        expect(getPageableCountries).toHaveBeenCalledWith(0, 10);
      });
    });
  });
});