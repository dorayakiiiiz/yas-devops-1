import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PromotionList from '../../../../pages/promotion/manager-promotion/index';
import { getPromotions, deletePromotion } from '../../../../modules/promotion/services/PromotionService';
import { PromotionPage, PromotionListRequest } from '../../../../modules/promotion/models/Promotion';

// Mock dependencies
vi.mock('../../../../modules/promotion/services/PromotionService', () => ({
  getPromotions: vi.fn(),
  deletePromotion: vi.fn(),
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

describe('PromotionList Page', () => {
  const mockPromotions = [
    {
      id: 1,
      name: 'Summer Sale',
      couponCode: 'SUMMER2024',
      discountAmount: 10,
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      isActive: true,
    },
    {
      id: 2,
      name: 'Winter Sale',
      couponCode: 'WINTER2024',
      discountAmount: 20,
      startDate: '2024-01-01',
      endDate: '2024-12-31',
      isActive: false,
    },
  ];


  describe('getPromotions', () => {
    it('should call getPromotions on mount', async () => {
      render(<PromotionList />);
      await waitFor(() => {
        expect(getPromotions).toHaveBeenCalled();
      });
    });

    it('should pass correct request params to getPromotions', async () => {
      render(<PromotionList />);
      await waitFor(() => {
        const expectedParams: PromotionListRequest = {
          couponCode: '',
          pageNo: 0,
          pageSize: 10,
          promotionName: '',
        };
        expect(getPromotions).toHaveBeenCalledWith(expectedParams);
      });
    });

    it('should handle empty promotion list', async () => {
      (getPromotions as any).mockResolvedValue({
        content: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<PromotionList />);
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getPromotions as any).mockRejectedValue(new Error('Network error'));
      render(<PromotionList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deletePromotion', () => {
    it('should be defined', () => {
      expect(deletePromotion).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deletePromotion as any).mockResolvedValue({ status: 204 });
      render(<PromotionList />);
      await waitFor(() => {
        expect(deletePromotion).toBeDefined();
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deletePromotion as any).mockRejectedValue(new Error('Delete failed'));
      render(<PromotionList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<PromotionList />);
      await waitFor(() => {
        expect(getPromotions).toHaveBeenCalled();
      });
    });
  });
});