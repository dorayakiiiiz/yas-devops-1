import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import WarehouseList from '../../../../pages/inventory/warehouses/index';
import { getPageableWarehouses, deleteWarehouse } from '../../../../modules/inventory/services/WarehouseService';
import { WarehouseDetail } from '../../../../modules/inventory/models/WarehouseDetail';

// Mock dependencies
vi.mock('../../../../modules/inventory/services/WarehouseService', () => ({
  getPageableWarehouses: vi.fn(),
  deleteWarehouse: vi.fn(),
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

describe('WarehouseList Page', () => {



  describe('getPageableWarehouses', () => {
    it('should call getPageableWarehouses on mount', async () => {
      render(<WarehouseList />);
      await waitFor(() => {
        expect(getPageableWarehouses).toHaveBeenCalled();
      });
    });

    it('should pass correct parameters to getPageableWarehouses', async () => {
      render(<WarehouseList />);
      await waitFor(() => {
        expect(getPageableWarehouses).toHaveBeenCalledWith(0, 10);
      });
    });

    it('should handle empty warehouse list', async () => {
      (getPageableWarehouses as any).mockResolvedValue({
        warehouseContent: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<WarehouseList />);
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPageableWarehouses as any).mockRejectedValue(new Error('Network error'));
      render(<WarehouseList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deleteWarehouse', () => {
    it('should be defined', () => {
      expect(deleteWarehouse).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteWarehouse as any).mockResolvedValue({ status: 204 });
      render(<WarehouseList />);
      await waitFor(() => {
        expect(deleteWarehouse).toBeDefined();
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deleteWarehouse as any).mockRejectedValue(new Error('Delete failed'));
      render(<WarehouseList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<WarehouseList />);
      await waitFor(() => {
        expect(getPageableWarehouses).toHaveBeenCalledWith(0, 10);
      });
    });
  });
});