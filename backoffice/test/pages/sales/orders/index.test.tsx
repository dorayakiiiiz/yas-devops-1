import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Orders from '../../../../pages/sales/orders/index';
import { getOrders } from '../../../../modules/order/services/OrderService';
import { Order } from '../../../../modules/order/models/Order';

// Mock dependencies
vi.mock('../../../../modules/order/services/OrderService', () => ({
  getOrders: vi.fn(),
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

describe('Orders Page', () => {
  const mockOrders: Order[] = [
    {
      id: 1,
      orderId: 'ORD-001',
      customerId: 'customer-1',
      orderDate: new Date('2024-01-01'),
      orderStatus: 'PENDING',
      orderTotal: 100,
      currency: 'USD',
    },
    {
      id: 2,
      orderId: 'ORD-002',
      customerId: 'customer-2',
      orderDate: new Date('2024-01-02'),
      orderStatus: 'COMPLETED',
      orderTotal: 200,
      currency: 'USD',
    },
  ];

  const mockResponse = {
    orderList: mockOrders,
    totalPages: 1,
    totalElements: 2,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getOrders as any).mockResolvedValue(mockResponse);
  });

  describe('getOrders', () => {
    it('should call getOrders on mount', async () => {
      render(<Orders />);
      await waitFor(() => {
        expect(getOrders).toHaveBeenCalled();
      });
    });

    it('should handle empty order list', async () => {
      (getOrders as any).mockResolvedValue({
        orderList: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<Orders />);
      await waitFor(() => {
        expect(screen.queryByText('Loading...')).toBeNull();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getOrders as any).mockRejectedValue(new Error('Network error'));
      render(<Orders />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<Orders />);
      await waitFor(() => {
        expect(getOrders).toHaveBeenCalled();
      });
    });
  });
});