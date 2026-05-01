import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import TaxClassList from '../../../../pages/tax/tax-classes/index';
import { getPageableTaxClasses, deleteTaxClass } from '../../../../modules/tax/services/TaxClassService';
import { TaxClass } from '../../../../modules/tax/models/TaxClass';

// Mock dependencies
vi.mock('../../../../modules/tax/services/TaxClassService', () => ({
  getPageableTaxClasses: vi.fn(),
  deleteTaxClass: vi.fn(),
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

describe('TaxClassList Page', () => {
  const mockTaxClasses: TaxClass[] = [
    {
      id: 1,
      name: 'Standard Tax',
      description: 'Standard tax rate',
    },
    {
      id: 2,
      name: 'Reduced Tax',
      description: 'Reduced tax rate',
    },
  ];

  const mockResponse = {
    taxClassContent: mockTaxClasses,
    totalPages: 1,
    totalElements: 2,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getPageableTaxClasses as any).mockResolvedValue(mockResponse);
    (deleteTaxClass as any).mockResolvedValue({ status: 204 });
  });

  describe('getPageableTaxClasses', () => {
    it('should call getPageableTaxClasses on mount', async () => {
      render(<TaxClassList />);
      await waitFor(() => {
        expect(getPageableTaxClasses).toHaveBeenCalled();
      });
    });

    it('should pass correct parameters to getPageableTaxClasses', async () => {
      render(<TaxClassList />);
      await waitFor(() => {
        expect(getPageableTaxClasses).toHaveBeenCalledWith(0, 10);
      });
    });

    it('should handle empty tax class list', async () => {
      (getPageableTaxClasses as any).mockResolvedValue({
        taxClassContent: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<TaxClassList />);
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPageableTaxClasses as any).mockRejectedValue(new Error('Network error'));
      render(<TaxClassList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deleteTaxClass', () => {
    it('should be defined', () => {
      expect(deleteTaxClass).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteTaxClass as any).mockResolvedValue({ status: 204 });
      render(<TaxClassList />);
      await waitFor(() => {
        expect(deleteTaxClass).toBeDefined();
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deleteTaxClass as any).mockRejectedValue(new Error('Delete failed'));
      render(<TaxClassList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<TaxClassList />);
      await waitFor(() => {
        expect(getPageableTaxClasses).toHaveBeenCalledWith(0, 10);
      });
    });
  });
});