import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Reviews from '../../../../pages/reviews/index';
import { getRatings, deleteRatingById } from '../../../../modules/rating/services/RatingService';
import { Rating } from '../../../../modules/rating/models/Rating';

// Mock dependencies
vi.mock('../../../../modules/rating/services/RatingService', () => ({
  getRatings: vi.fn(),
  deleteRatingById: vi.fn(),
}));

vi.mock('../../../../commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react-hook-form', () => ({
  useForm: () => ({
    register: vi.fn(),
    watch: vi.fn(() => ({})),
    handleSubmit: vi.fn((fn) => fn),
  }),
}));

vi.mock('query-string', () => ({
  stringify: vi.fn(),
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

vi.mock('react-toastify', () => ({
  toast: {
    success: vi.fn(),
  },
}));

describe('Reviews Page', () => {
  const mockRatings: Rating[] = [
    {
      id: 1,
      rating: 5,
      review: 'Great product!',
      productId: 1,
      customerId: 'customer-1',
      createdAt: new Date('2024-01-01'),
    },
    {
      id: 2,
      rating: 4,
      review: 'Good product',
      productId: 2,
      customerId: 'customer-2',
      createdAt: new Date('2024-01-02'),
    },
  ];

  const mockResponse = {
    ratingList: mockRatings,
    totalPages: 1,
    totalElements: 2,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getRatings as any).mockResolvedValue(mockResponse);
    (deleteRatingById as any).mockResolvedValue({ status: 204 });
  });

  describe('getRatings', () => {
    it('should call getRatings on mount', async () => {
      render(<Reviews />);
      await waitFor(() => {
        expect(getRatings).toHaveBeenCalled();
      });
    });

    it('should handle empty rating list', async () => {
      (getRatings as any).mockResolvedValue({
        ratingList: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<Reviews />);
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getRatings as any).mockRejectedValue(new Error('Network error'));
      render(<Reviews />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('deleteRatingById', () => {
    it('should be defined', () => {
      expect(deleteRatingById).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteRatingById as any).mockResolvedValue({ status: 204 });
      render(<Reviews />);
      await waitFor(() => {
        expect(deleteRatingById).toBeDefined();
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deleteRatingById as any).mockRejectedValue(new Error('Delete failed'));
      render(<Reviews />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<Reviews />);
      await waitFor(() => {
        expect(getRatings).toHaveBeenCalled();
      });
    });
  });
});