import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import LatestProducts from '../../../../modules/home/components/LatestProducts';
import { getLatestProducts } from '../../../../modules/catalog/services/ProductService';
import moment from 'moment';

// Mock dependencies
vi.mock('../../../../modules/catalog/services/ProductService', () => ({
  getLatestProducts: vi.fn(),
}));

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('moment', () => ({
  default: vi.fn(() => ({
    format: vi.fn(() => 'January 1st 2024, 12:00:00 pm'),
  })),
}));

describe('LatestProducts', () => {
  const mockProducts = [
    {
      id: 1,
      name: 'Product 1',
      slug: 'product-1',
      createdOn: '2024-01-01T12:00:00Z',
      parentId: null,
    },
    {
      id: 2,
      name: 'Product 2',
      slug: 'product-2',
      createdOn: '2024-01-02T12:00:00Z',
      parentId: null,
    },
    {
      id: 3,
      name: 'Product 3',
      slug: 'product-3',
      createdOn: '2024-01-03T12:00:00Z',
      parentId: 1,
    },
    {
      id: 4,
      name: 'Product 4',
      slug: 'product-4',
      createdOn: '2024-01-04T12:00:00Z',
      parentId: null,
    },
    {
      id: 5,
      name: 'Product 5',
      slug: 'product-5',
      createdOn: '2024-01-05T12:00:00Z',
      parentId: null,
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe('Rendering - Loading State', () => {
    it('should show loading message when fetching products', () => {
      (getLatestProducts as any).mockImplementation(() => new Promise(() => {}));
      render(<LatestProducts />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should show loading state before data is fetched', () => {
      (getLatestProducts as any).mockImplementation(() => new Promise(() => {}));
      render(<LatestProducts />);
      
      const loadingCell = screen.getByText('Loading...');
      expect(loadingCell).toBeDefined();
      expect(loadingCell.getAttribute('colspan')).toBe('5');
    });
  });

  describe('Rendering - Success State', () => {

    it('should render table headers correctly', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(screen.getByText('ID')).toBeDefined();
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('Slug')).toBeDefined();
        expect(screen.getByText('Created On')).toBeDefined();
        expect(screen.getByText('Action')).toBeDefined();
      });
    });

    it('should render product details correctly', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(screen.getByText('1')).toBeDefined();
        expect(screen.getByText('Product 1')).toBeDefined();
        expect(screen.getByText('product-1')).toBeDefined();
        expect(screen.getByText('2')).toBeDefined();
        expect(screen.getByText('Product 2')).toBeDefined();
        expect(screen.getByText('product-2')).toBeDefined();
      });
    });

    it('should not render date when createdOn is missing', async () => {
      const productsWithoutDate = [
        {
          id: 1,
          name: 'Product 1',
          slug: 'product-1',
          createdOn: null,
          parentId: null,
        },
      ];
      (getLatestProducts as any).mockResolvedValue(productsWithoutDate);
      render(<LatestProducts />);
      
      await waitFor(() => {
        const cells = screen.getAllByRole('cell');
        const dateCell = cells.find(cell => cell.textContent === '');
        expect(dateCell).toBeDefined();
      });
    });

  });

  describe('Rendering - Empty State', () => {
    it('should show "No Products available" when products array is empty', async () => {
      (getLatestProducts as any).mockResolvedValue([]);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(screen.getByText('No Products available')).toBeDefined();
      });
    });

    it('should show "No Products available" when products is null', async () => {
      (getLatestProducts as any).mockResolvedValue(null);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(screen.getByText('No Products available')).toBeDefined();
      });
    });

    it('should have colspan 5 for empty state message', async () => {
      (getLatestProducts as any).mockResolvedValue([]);
      render(<LatestProducts />);
      
      await waitFor(() => {
        const emptyCell = screen.getByText('No Products available');
        expect(emptyCell.getAttribute('colspan')).toBe('5');
      });
    });
  });

  describe('Rendering - Error State', () => {
    it('should show "No Products available" when API call fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getLatestProducts as any).mockRejectedValue(new Error('API Error'));
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(screen.getByText('No Products available')).toBeDefined();
      });
      
      expect(consoleErrorSpy).toHaveBeenCalled();
      consoleErrorSpy.mockRestore();
    });

    it('should log error to console when API fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      const error = new Error('Network error');
      (getLatestProducts as any).mockRejectedValue(error);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(consoleErrorSpy).toHaveBeenCalledWith(error);
      });
      
      consoleErrorSpy.mockRestore();
    });
  });

  describe('API Integration', () => {
    it('should call getLatestProducts with parameter 5', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(getLatestProducts).toHaveBeenCalledTimes(1);
        expect(getLatestProducts).toHaveBeenCalledWith(5);
      });
    });

    it('should call getLatestProducts only once on mount', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(getLatestProducts).toHaveBeenCalledTimes(1);
      });
    });
  });

  describe('Component Title', () => {
    it('should render title "List of the 5 latest Products"', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        expect(screen.getByText('List of the 5 latest Products')).toBeDefined();
      });
    });

    it('should have correct styling classes on title', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        const title = screen.getByText('List of the 5 latest Products');
        expect(title).toHaveClass('text-danger', 'font-weight-bold', 'mb-3');
      });
    });
  });

  describe('Table Structure', () => {
    it('should render table with striped, bordered, hover classes', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      const { container } = render(<LatestProducts />);
      
      await waitFor(() => {
        const table = container.querySelector('table');
        expect(table).toHaveClass('table', 'table-striped', 'table-bordered', 'table-hover');
      });
    });

    it('should render thead with correct columns', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      await waitFor(() => {
        const headers = screen.getAllByRole('columnheader');
        expect(headers).toHaveLength(5);
        expect(headers[0].textContent).toBe('ID');
        expect(headers[1].textContent).toBe('Name');
        expect(headers[2].textContent).toBe('Slug');
        expect(headers[3].textContent).toBe('Created On');
        expect(headers[4].textContent).toBe('Action');
      });
    });
  });

  describe('State Management', () => {
    it('should update from loading to success state', async () => {
      (getLatestProducts as any).mockResolvedValue(mockProducts);
      render(<LatestProducts />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
      
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
        expect(screen.getByText('Product 1')).toBeDefined();
      });
    });

    it('should update from loading to empty state', async () => {
      (getLatestProducts as any).mockResolvedValue([]);
      render(<LatestProducts />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
      
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
        expect(screen.getByText('No Products available')).toBeDefined();
      });
    });

    it('should update from loading to error state', async () => {
      (getLatestProducts as any).mockRejectedValue(new Error('API Error'));
      render(<LatestProducts />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
      
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
        expect(screen.getByText('No Products available')).toBeDefined();
      });
    });
  });
});