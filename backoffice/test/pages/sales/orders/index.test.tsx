import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Orders from '../../../../pages/sales/orders/index';
import { getOrders } from '../../../../modules/order/services/OrderService';
import { Order } from '../../../../modules/order/models/Order';
import { OrderItem } from '../../../../modules/order/models/OrderItem';
import { OrderAddress } from '../../../../modules/order/models/OrderAddress';

// Mock dependencies
vi.mock('../../../../modules/order/services/OrderService', () => ({
  getOrders: vi.fn(),
}));

vi.mock('../../../../common/services/ApiClientService', () => ({
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

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

describe('Orders Page', () => {
  // Mock OrderAddress
  const mockAddress: OrderAddress = {
    contactName: 'John Doe',
    phone: '123-456-7890',
    addressLine1: '123 Main St',
    addressLine2: 'Apt 4B',
    zipCode: '10001',
    districtName: 'Manhattan',
    districtId: 1,
    city: 'New York',
    stateOrProvinceName: 'New York',
    stateOrProvinceId: 1,
    countryName: 'United States',
    countryId: 1,
  };

  // Mock OrderItem
  const mockOrderItems: OrderItem[] = [
    {
      id: 1,
      productId: 100,
      productName: 'Laptop',
      quantity: 1,
      productPrice: 999.99,
    },
  ];

  // SỬA: Order theo đúng type
  const mockOrders: Order[] = [
    {
      id: 1,
      email: 'john@example.com',
      note: 'Please leave at front door',
      tax: 10.00,
      discount: 5.00,
      numberItem: 2,
      totalPrice: 299.99,
      deliveryFee: 15.00,
      couponCode: 'SAVE10',
      deliveryMethod: 'Standard Shipping',
      deliveryStatus: 'Shipped',
      paymentMethod: 'Credit Card',
      paymentStatus: 'Paid',
      createdOn: new Date('2024-01-15T10:30:00Z'),
      orderStatus: 'Processing',
      orderItemVms: mockOrderItems,
      shippingAddressVm: mockAddress,
      billingAddressVm: mockAddress,
      checkoutId: 'checkout_123',
    },
    {
      id: 2,
      email: 'jane@example.com',
      note: '',
      tax: 8.00,
      discount: 0,
      numberItem: 1,
      totalPrice: 199.99,
      deliveryFee: 10.00,
      couponCode: '',
      deliveryMethod: 'Express Shipping',
      deliveryStatus: 'Delivered',
      paymentMethod: 'PayPal',
      paymentStatus: 'Paid',
      createdOn: new Date('2024-01-16T10:30:00Z'),
      orderStatus: 'Completed',
      orderItemVms: mockOrderItems,
      shippingAddressVm: mockAddress,
      billingAddressVm: mockAddress,
      checkoutId: 'checkout_456',
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
});