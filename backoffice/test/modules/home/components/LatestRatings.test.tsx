import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import LatestRatings from '../../../../modules/home/components/LatestRatings';
import { getLatestRatings } from '../../../../modules/rating/services/RatingService';
import { getProduct } from '../../../../modules/catalog/services/ProductService';
import { useRouter } from 'next/router';
import moment from 'moment';

// Mock dependencies
vi.mock('../../../../modules/rating/services/RatingService', () => ({
  getLatestRatings: vi.fn(),
}));

vi.mock('../../../../modules/catalog/services/ProductService', () => ({
  getProduct: vi.fn(),
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

vi.mock('moment', () => ({
  default: vi.fn(() => ({
    format: vi.fn(() => 'January 1st 2024, 12:00:00 pm'),
  })),
}));

describe('LatestRatings', () => {
  const mockRatings = [
    {
      id: 1,
      productName: 'Product 1',
      content: 'Great product!',
      createdOn: '2024-01-01T12:00:00Z',
      productId: 101,
    },
    {
      id: 2,
      productName: 'Product 2',
      content: 'Good quality',
      createdOn: '2024-01-02T12:00:00Z',
      productId: 102,
    },
    {
      id: 3,
      productName: 'Product 3',
      content: 'Average',
      createdOn: '2024-01-03T12:00:00Z',
      productId: 103,
    },
    {
      id: 4,
      productName: 'Product 4',
      content: 'Excellent!',
      createdOn: '2024-01-04T12:00:00Z',
      productId: 104,
    },
    {
      id: 5,
      productName: 'Product 5',
      content: 'Not bad',
      createdOn: '2024-01-05T12:00:00Z',
      productId: 105,
    },
  ];

  const mockPush = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useRouter as any).mockReturnValue({
      push: mockPush,
    });
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe('Rendering - Loading State', () => {
    it('should show loading message when fetching ratings', () => {
      (getLatestRatings as any).mockImplementation(() => new Promise(() => {}));
      render(<LatestRatings />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should show loading state before data is fetched', () => {
      (getLatestRatings as any).mockImplementation(() => new Promise(() => {}));
      render(<LatestRatings />);
      
      const loadingCell = screen.getByText('Loading...');
      expect(loadingCell).toBeDefined();
      expect(loadingCell.getAttribute('colspan')).toBe('5');
    });
  });

  describe('Rendering - Success State', () => {
    it('should render ratings when data is fetched successfully', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Product 1')).toBeDefined();
        expect(screen.getByText('Product 2')).toBeDefined();
        expect(screen.getByText('Great product!')).toBeDefined();
        expect(screen.getByText('Good quality')).toBeDefined();
      });
    });

    it('should render table headers correctly', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('ID')).toBeDefined();
        expect(screen.getByText('Product Name')).toBeDefined();
        expect(screen.getByText('Content')).toBeDefined();
        expect(screen.getByText('Created On')).toBeDefined();
        expect(screen.getByText('Action')).toBeDefined();
      });
    });

    it('should render rating details correctly', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('1')).toBeDefined();
        expect(screen.getByText('Product 1')).toBeDefined();
        expect(screen.getByText('Great product!')).toBeDefined();
        expect(screen.getByText('2')).toBeDefined();
        expect(screen.getByText('Product 2')).toBeDefined();
        expect(screen.getByText('Good quality')).toBeDefined();
      });
    });

    it('should render formatted date using moment', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(moment).toHaveBeenCalled();
        expect(screen.getAllByText('January 1st 2024, 12:00:00 pm')).toHaveLength(5);
      });
    });

    it('should render Details button for each rating', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        const buttons = screen.getAllByText('Details');
        expect(buttons).toHaveLength(5);
        buttons.forEach(button => {
          expect(button).toHaveClass('btn', 'btn-outline-primary', 'btn-sm');
        });
      });
    });
  });

  describe('Rendering - Empty State', () => {
    it('should show "No Ratings available" when ratings array is empty', async () => {
      (getLatestRatings as any).mockResolvedValue([]);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('No Ratings available')).toBeDefined();
      });
    });

    it('should show "No Ratings available" when ratings is null', async () => {
      (getLatestRatings as any).mockResolvedValue(null);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('No Ratings available')).toBeDefined();
      });
    });

    it('should have colspan 5 for empty state message', async () => {
      (getLatestRatings as any).mockResolvedValue([]);
      render(<LatestRatings />);
      
      await waitFor(() => {
        const emptyCell = screen.getByText('No Ratings available');
        expect(emptyCell.getAttribute('colspan')).toBe('5');
      });
    });
  });

  describe('Rendering - Error State', () => {
    it('should show "No Ratings available" when API call fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getLatestRatings as any).mockRejectedValue(new Error('API Error'));
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('No Ratings available')).toBeDefined();
      });
      
      expect(consoleErrorSpy).toHaveBeenCalled();
      consoleErrorSpy.mockRestore();
    });

    it('should log error to console when API fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      const error = new Error('Network error');
      (getLatestRatings as any).mockRejectedValue(error);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(consoleErrorSpy).toHaveBeenCalledWith(error);
      });
      
      consoleErrorSpy.mockRestore();
    });
  });

  describe('API Integration', () => {
    it('should call getLatestRatings with parameter 5', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(getLatestRatings).toHaveBeenCalledTimes(1);
        expect(getLatestRatings).toHaveBeenCalledWith(5);
      });
    });

    it('should call getLatestRatings only once on mount', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(getLatestRatings).toHaveBeenCalledTimes(1);
      });
    });
  });

  describe('Handle Details Click - Navigation', () => {
    it('should navigate to product edit page with parentId when product has parentId', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      (getProduct as any).mockResolvedValue({ id: 101, parentId: 10 });
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Details')).toBeDefined();
      });
      
      const detailsButton = screen.getAllByText('Details')[0];
      fireEvent.click(detailsButton);
      
      await waitFor(() => {
        expect(getProduct).toHaveBeenCalledWith(101);
        expect(mockPush).toHaveBeenCalledWith('/catalog/products/10/edit');
      });
    });

    it('should navigate to product edit page with productId when product has no parentId', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      (getProduct as any).mockResolvedValue({ id: 102, parentId: null });
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Details')).toBeDefined();
      });
      
      const detailsButton = screen.getAllByText('Details')[1];
      fireEvent.click(detailsButton);
      
      await waitFor(() => {
        expect(getProduct).toHaveBeenCalledWith(102);
        expect(mockPush).toHaveBeenCalledWith('/catalog/products/102/edit');
      });
    });

    it('should navigate when parentId is undefined', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      (getProduct as any).mockResolvedValue({ id: 103 });
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Details')).toBeDefined();
      });
      
      const detailsButton = screen.getAllByText('Details')[2];
      fireEvent.click(detailsButton);
      
      await waitFor(() => {
        expect(getProduct).toHaveBeenCalledWith(103);
        expect(mockPush).toHaveBeenCalledWith('/catalog/products/103/edit');
      });
    });

    it('should handle parentId = 0 correctly', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      (getProduct as any).mockResolvedValue({ id: 104, parentId: 0 });
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Details')).toBeDefined();
      });
      
      const detailsButton = screen.getAllByText('Details')[3];
      fireEvent.click(detailsButton);
      
      await waitFor(() => {
        expect(getProduct).toHaveBeenCalledWith(104);
        expect(mockPush).toHaveBeenCalledWith('/catalog/products/104/edit');
      });
    });
  });

  describe('Handle Details Click - Error Handling', () => {
    it('should log error when getProduct fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      (getProduct as any).mockRejectedValue(new Error('Product not found'));
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Details')).toBeDefined();
      });
      
      const detailsButton = screen.getAllByText('Details')[0];
      fireEvent.click(detailsButton);
      
      await waitFor(() => {
        expect(consoleErrorSpy).toHaveBeenCalledWith(new Error('Product not found'));
      });
      
      consoleErrorSpy.mockRestore();
    });

    it('should not navigate when getProduct fails', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      (getProduct as any).mockRejectedValue(new Error('Product not found'));
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Details')).toBeDefined();
      });
      
      const detailsButton = screen.getAllByText('Details')[0];
      fireEvent.click(detailsButton);
      
      await waitFor(() => {
        expect(mockPush).not.toHaveBeenCalled();
      });
    });
  });

  describe('Component Title', () => {
    it('should render title "List of the 5 latest Ratings"', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('List of the 5 latest Ratings')).toBeDefined();
      });
    });

    it('should have correct styling classes on title', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        const title = screen.getByText('List of the 5 latest Ratings');
        expect(title).toHaveClass('text-danger', 'font-weight-bold', 'mb-3');
      });
    });
  });

  describe('Table Structure', () => {
    it('should render table with striped, bordered, hover classes', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      const { container } = render(<LatestRatings />);
      
      await waitFor(() => {
        const table = container.querySelector('table');
        expect(table).toHaveClass('table', 'table-striped', 'table-bordered', 'table-hover');
      });
    });

    it('should render thead with correct columns', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        const headers = screen.getAllByRole('columnheader');
        expect(headers).toHaveLength(5);
        expect(headers[0].textContent).toBe('ID');
        expect(headers[1].textContent).toBe('Product Name');
        expect(headers[2].textContent).toBe('Content');
        expect(headers[3].textContent).toBe('Created On');
        expect(headers[4].textContent).toBe('Action');
      });
    });
  });

  describe('Edge Cases', () => {
    it('should handle ratings with missing fields gracefully', async () => {
      const incompleteRatings = [
        {
          id: 1,
          productName: 'Product 1',
          // missing content
          createdOn: '2024-01-01T12:00:00Z',
          productId: 101,
        },
      ];
      (getLatestRatings as any).mockResolvedValue(incompleteRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Product 1')).toBeDefined();
      });
    });

    it('should handle very long content text', async () => {
      const longContentRatings = [
        {
          id: 1,
          productName: 'Product 1',
          content: 'Very long rating content that might overflow the table cell and cause layout issues. '.repeat(10),
          createdOn: '2024-01-01T12:00:00Z',
          productId: 101,
        },
      ];
      (getLatestRatings as any).mockResolvedValue(longContentRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText(longContentRatings[0].content)).toBeDefined();
      });
    });

    it('should handle special characters in product name and content', async () => {
      const specialCharRatings = [
        {
          id: 1,
          productName: 'Product @#$%^&*()',
          content: 'Rating content with special characters !@#$%^&*()',
          createdOn: '2024-01-01T12:00:00Z',
          productId: 101,
        },
      ];
      (getLatestRatings as any).mockResolvedValue(specialCharRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        expect(screen.getByText('Product @#$%^&*()')).toBeDefined();
        expect(screen.getByText('Rating content with special characters !@#$%^&*()')).toBeDefined();
      });
    });
  });

  describe('State Management', () => {
    it('should update from loading to success state', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
      
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
        expect(screen.getByText('Product 1')).toBeDefined();
      });
    });

    it('should update from loading to empty state', async () => {
      (getLatestRatings as any).mockResolvedValue([]);
      render(<LatestRatings />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
      
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
        expect(screen.getByText('No Ratings available')).toBeDefined();
      });
    });

    it('should update from loading to error state', async () => {
      (getLatestRatings as any).mockRejectedValue(new Error('API Error'));
      render(<LatestRatings />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
      
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
        expect(screen.getByText('No Ratings available')).toBeDefined();
      });
    });
  });

  describe('Button Styling', () => {
    it('should have Details button with correct classes', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        const buttons = screen.getAllByText('Details');
        expect(buttons[0]).toHaveClass('btn', 'btn-outline-primary', 'btn-sm');
      });
    });

    it('should have button type as button', async () => {
      (getLatestRatings as any).mockResolvedValue(mockRatings);
      render(<LatestRatings />);
      
      await waitFor(() => {
        const buttons = screen.getAllByRole('button', { name: 'Details' });
        expect(buttons[0]).toHaveAttribute('type', 'button');
      });
    });
  });
});