import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LatestOrders from '../../../../modules/home/components/LatestOrders';
import { getLatestOrders } from '../../../../modules/order/services/OrderService';
import moment from 'moment';

// Mock dependencies
vi.mock('../../../../modules/order/services/OrderService', () => ({
  getLatestOrders: vi.fn(),
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

describe('LatestOrders', () => {
  const mockOrders = [
    {
      id: 1,
      email: 'john@example.com',
      orderStatus: 'Completed',
      createdOn: '2024-01-01T12:00:00Z',
    },
    {
      id: 2,
      email: 'jane@example.com',
      orderStatus: 'Processing',
      createdOn: '2024-01-02T12:00:00Z',
    },
    {
      id: 3,
      email: 'bob@example.com',
      orderStatus: 'Pending',
      createdOn: '2024-01-03T12:00:00Z',
    },
    {
      id: 4,
      email: 'alice@example.com',
      orderStatus: 'Shipped',
      createdOn: '2024-01-04T12:00:00Z',
    },
    {
      id: 5,
      email: 'charlie@example.com',
      orderStatus: 'Delivered',
      createdOn: '2024-01-05T12:00:00Z',
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe('Rendering - Loading State', () => {
    it('should show loading message when fetching orders', () => {
      (getLatestOrders as any).mockImplementation(() => new Promise(() => {}));
      render(<LatestOrders />);
      
      expect(screen.getByText('Loading...')).toBeDefined();
    });

    it('should show loading state before data is fetched', () => {
      (getLatestOrders as any).mockImplementation(() => new Promise(() => {}));
      render(<LatestOrders />);
      
      const loadingCell = screen.getByText('Loading...');
      expect(loadingCell).toBeDefined();
      expect(loadingCell.getAttribute('colspan')).toBe('5');
    });
  });

  describe('Rendering - Success State', () => {
    it('should render orders when data is fetched successfully', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('john@example.com')).toBeDefined();
        expect(screen.getByText('jane@example.com')).toBeDefined();
        expect(screen.getByText('bob@example.com')).toBeDefined();
      });
    });

    it('should render table headers correctly', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('ID')).toBeDefined();
        expect(screen.getByText('Email')).toBeDefined();
        expect(screen.getByText('Status')).toBeDefined();
        expect(screen.getByText('Created On')).toBeDefined();
        expect(screen.getByText('Action')).toBeDefined();
      });
    });

    it('should render order details correctly', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('1')).toBeDefined();
        expect(screen.getByText('john@example.com')).toBeDefined();
        expect(screen.getByText('Completed')).toBeDefined();
        expect(screen.getByText('2')).toBeDefined();
        expect(screen.getByText('jane@example.com')).toBeDefined();
        expect(screen.getByText('Processing')).toBeDefined();
      });
    });

    it('should render formatted date using moment', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(moment).toHaveBeenCalled();
        expect(screen.getAllByText('January 1st 2024, 12:00:00 pm')).toHaveLength(5);
      });
    });

    it('should render Details button for each order', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const buttons = screen.getAllByText('Details');
        expect(buttons).toHaveLength(5);
        buttons.forEach(button => {
          expect(button).toHaveClass('btn', 'btn-outline-primary', 'btn-sm');
        });
      });
    });

    it('should have correct link href for each order', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        expect(links[0]).toHaveAttribute('href', '/sales/orders/1/edit');
        expect(links[1]).toHaveAttribute('href', '/sales/orders/2/edit');
        expect(links[2]).toHaveAttribute('href', '/sales/orders/3/edit');
        expect(links[3]).toHaveAttribute('href', '/sales/orders/4/edit');
        expect(links[4]).toHaveAttribute('href', '/sales/orders/5/edit');
      });
    });
  });

  describe('Rendering - Empty State', () => {
    it('should show "No Orders available" when orders array is empty', async () => {
      (getLatestOrders as any).mockResolvedValue([]);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('No Orders available')).toBeDefined();
      });
    });

    it('should show "No Orders available" when orders is null', async () => {
      (getLatestOrders as any).mockResolvedValue(null);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('No Orders available')).toBeDefined();
      });
    });

    it('should have colspan 5 for empty state message', async () => {
      (getLatestOrders as any).mockResolvedValue([]);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const emptyCell = screen.getByText('No Orders available');
        expect(emptyCell.getAttribute('colspan')).toBe('5');
      });
    });
  });

  describe('Rendering - Error State', () => {
    it('should show "No Orders available" when API call fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getLatestOrders as any).mockRejectedValue(new Error('API Error'));
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('No Orders available')).toBeDefined();
      });
      
      expect(consoleErrorSpy).toHaveBeenCalled();
      consoleErrorSpy.mockRestore();
    });

    it('should log error to console when API fails', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      const error = new Error('Network error');
      (getLatestOrders as any).mockRejectedValue(error);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(consoleErrorSpy).toHaveBeenCalledWith(error);
      });
      
      consoleErrorSpy.mockRestore();
    });
  });

  describe('API Integration', () => {
    it('should call getLatestOrders with parameter 5', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(getLatestOrders).toHaveBeenCalledTimes(1);
        expect(getLatestOrders).toHaveBeenCalledWith(5);
      });
    });

    it('should call getLatestOrders only once on mount', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(getLatestOrders).toHaveBeenCalledTimes(1);
      });
    });
  });

  describe('Component Title', () => {
    it('should render title "List of the 5 latest Orders"', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('List of the 5 latest Orders')).toBeDefined();
      });
    });

    it('should have correct styling classes on title', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const title = screen.getByText('List of the 5 latest Orders');
        expect(title).toHaveClass('text-danger', 'font-weight-bold', 'mb-3');
      });
    });
  });

  describe('Table Structure', () => {
    it('should render table with striped, bordered, hover classes', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      const { container } = render(<LatestOrders />);
      
      await waitFor(() => {
        const table = container.querySelector('table');
        expect(table).toHaveClass('table', 'table-striped', 'table-bordered', 'table-hover');
      });
    });

    it('should render thead with correct columns', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const headers = screen.getAllByRole('columnheader');
        expect(headers).toHaveLength(5);
        expect(headers[0].textContent).toBe('ID');
        expect(headers[1].textContent).toBe('Email');
        expect(headers[2].textContent).toBe('Status');
        expect(headers[3].textContent).toBe('Created On');
        expect(headers[4].textContent).toBe('Action');
      });
    });
  });

  describe('Button Interaction', () => {
    it('should render Details button as button element', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const buttons = screen.getAllByRole('button', { name: 'Details' });
        expect(buttons).toHaveLength(5);
        expect(buttons[0]).toHaveAttribute('type', 'button');
      });
    });

    it('should have link wrapping the Details button', async () => {
      (getLatestOrders as any).mockResolvedValue(mockOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        expect(links[0].querySelector('button')).toBeDefined();
      });
    });
  });

  describe('Edge Cases', () => {
    it('should handle orders with missing fields gracefully', async () => {
      const incompleteOrders = [
        {
          id: 1,
          email: 'test@example.com',
          // missing orderStatus
          createdOn: '2024-01-01T12:00:00Z',
        },
      ];
      (getLatestOrders as any).mockResolvedValue(incompleteOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText('test@example.com')).toBeDefined();
      });
    });

    it('should handle very long email addresses', async () => {
      const longEmailOrders = [
        {
          id: 1,
          email: 'verylongemailaddressthatmightoverflowthetablecell@example.com',
          orderStatus: 'Completed',
          createdOn: '2024-01-01T12:00:00Z',
        },
      ];
      (getLatestOrders as any).mockResolvedValue(longEmailOrders);
      render(<LatestOrders />);
      
      await waitFor(() => {
        expect(screen.getByText(longEmailOrders[0].email)).toBeDefined();
      });
    });
  });
});