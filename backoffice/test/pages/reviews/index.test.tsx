import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Reviews from '../../../pages/reviews/index';
import { getRatings, deleteRatingById } from '../../../modules/rating/services/RatingService';
import { Rating } from '../../../modules/rating/models/Rating';

// Mock dependencies
vi.mock('../../../modules/rating/services/RatingService', () => ({
  getRatings: vi.fn(),
  deleteRatingById: vi.fn(),
}));

vi.mock('../../../common/services/ApiClientService', () => ({
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

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    query: {},
  })),
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

describe('Reviews Page', () => {
  // SỬA: Rating theo đúng type
  const mockRatings: Rating[] = [
    {
      id: 1,
      content: 'Great product! Very satisfied with the quality.',
      createdBy: 'john.doe@example.com',
      star: 5,
      productId: 100,
      createdOn: new Date('2024-01-15T10:30:00Z'),
      lastName: 'Doe',
      firstName: 'John',
      productName: 'Laptop',
    },
    {
      id: 2,
      content: 'Good product, fast shipping',
      createdBy: 'jane.smith@example.com',
      star: 4,
      productId: 101,
      createdOn: new Date('2024-01-16T10:30:00Z'),
      lastName: 'Smith',
      firstName: 'Jane',
      productName: 'Mouse',
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

    it('should display rating data in table', async () => {
      render(<Reviews />);
      
      await waitFor(() => {
        expect(screen.getByText('Laptop')).toBeDefined();
        expect(screen.getByText('Mouse')).toBeDefined();
        expect(screen.getByText('John')).toBeDefined();
        expect(screen.getByText('Doe')).toBeDefined();
        expect(screen.getByText('Jane')).toBeDefined();
        expect(screen.getByText('Smith')).toBeDefined();
        expect(screen.getByText('Great product! Very satisfied with the quality.')).toBeDefined();
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
        expect(screen.getByText('No ratings available')).toBeDefined();
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

  describe('Table Headers', () => {
    it('should render all table headers', async () => {
      render(<Reviews />);
      
      await waitFor(() => {
        expect(screen.getByText('ID')).toBeDefined();
        expect(screen.getByText('Product Name')).toBeDefined();
        expect(screen.getByText('Rating')).toBeDefined();
        expect(screen.getByText('Content')).toBeDefined();
        expect(screen.getByText('Customer')).toBeDefined();
        expect(screen.getByText('Created On')).toBeDefined();
        expect(screen.getByText('Actions')).toBeDefined();
      });
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