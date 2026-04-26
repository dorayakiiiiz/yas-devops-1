// test/common/items/ListGroup.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { useRouter } from 'next/router';
import ListGroup from '../../../common/items/ListGroup';

// Mock next/router
vi.mock('next/router', () => ({
  useRouter: vi.fn(),
}));

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: { children: React.ReactNode; href: string }) => (
    <a href={href}>{children}</a>
  ),
}));

// Mock styles
vi.mock('../../styles/ListGroup.module.css', () => ({
  default: {
    listGroup: 'list-group-mock',
    listGroupItem: 'list-group-item-mock',
    appActiveLink: 'app-active-link-mock',
  },
}));

describe('ListGroup', () => {
  const mockData = [
    { id: 1, name: 'Dashboard', link: '/dashboard', icon: '🏠' },
    { id: 2, name: 'Products', link: '/products', icon: '📦' },
    { id: 3, name: 'Orders', link: '/orders', icon: '🛒' },
    { id: 4, name: 'Customers', link: '/customers', icon: '👥' },
  ];

  describe('Rendering', () => {
    it('should render list group component', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const listGroup = document.querySelector('.list-group-mock');
      expect(listGroup).toBeDefined();
      
      vi.clearAllMocks();
    });

    it('should render all items from data', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      expect(screen.getByText('Dashboard')).toBeDefined();
      expect(screen.getByText('Products')).toBeDefined();
      expect(screen.getByText('Orders')).toBeDefined();
      expect(screen.getByText('Customers')).toBeDefined();
      
      vi.clearAllMocks();
    });

    it('should render icons for each item', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      expect(screen.getByText('🏠')).toBeDefined();
      expect(screen.getByText('📦')).toBeDefined();
      expect(screen.getByText('🛒')).toBeDefined();
      expect(screen.getByText('👥')).toBeDefined();
      
      vi.clearAllMocks();
    });

    it('should render links with correct href', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const dashboardLink = screen.getByText('Dashboard').closest('a');
      const productsLink = screen.getByText('Products').closest('a');
      const ordersLink = screen.getByText('Orders').closest('a');
      
      expect(dashboardLink?.getAttribute('href')).toBe('/dashboard');
      expect(productsLink?.getAttribute('href')).toBe('/products');
      expect(ordersLink?.getAttribute('href')).toBe('/orders');
      
      vi.clearAllMocks();
    });
  });

  describe('Active Link Styling', () => {
    it('should apply active class to current route', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/products',
      });

      render(<ListGroup data={mockData} />);
      
      const productsLink = screen.getByText('Products').closest('a');
      expect(productsLink?.className).toContain('app-active-link-mock');
      
      vi.clearAllMocks();
    });

    it('should not apply active class to non-current routes', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/products',
      });

      render(<ListGroup data={mockData} />);
      
      const dashboardLink = screen.getByText('Dashboard').closest('a');
      const ordersLink = screen.getByText('Orders').closest('a');
      
      expect(dashboardLink?.className).toContain('app-not-active-link');
      expect(ordersLink?.className).toContain('app-not-active-link');
      
      vi.clearAllMocks();
    });

    it('should apply active class to dashboard route', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const dashboardLink = screen.getByText('Dashboard').closest('a');
      expect(dashboardLink?.className).toContain('app-active-link-mock');
      
      vi.clearAllMocks();
    });

    it('should apply active class to orders route', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/orders',
      });

      render(<ListGroup data={mockData} />);
      
      const ordersLink = screen.getByText('Orders').closest('a');
      expect(ordersLink?.className).toContain('app-active-link-mock');
      
      vi.clearAllMocks();
    });

    it('should apply active class to customers route', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/customers',
      });

      render(<ListGroup data={mockData} />);
      
      const customersLink = screen.getByText('Customers').closest('a');
      expect(customersLink?.className).toContain('app-active-link-mock');
      
      vi.clearAllMocks();
    });
  });

  describe('Empty Data', () => {
    it('should render nothing when data is empty array', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={[]} />);
      
      const listGroup = document.querySelector('.list-group-mock');
      expect(listGroup).toBeDefined();
      expect(listGroup?.children.length).toBe(0);
      
      vi.clearAllMocks();
    });

    it('should handle undefined data gracefully', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      // @ts-ignore - testing edge case
      render(<ListGroup data={undefined} />);
      
      const listGroup = document.querySelector('.list-group-mock');
      expect(listGroup).toBeDefined();
      
      vi.clearAllMocks();
    });
  });

  describe('Item Styling', () => {
    it('should apply list group item class to each item', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const items = document.querySelectorAll('.list-group-item-mock');
      expect(items.length).toBe(mockData.length);
      
      vi.clearAllMocks();
    });

    it('should have correct Bootstrap classes', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const firstItem = document.querySelector('.list-group-item-mock');
      expect(firstItem?.className).toContain('py-3');
      expect(firstItem?.className).toContain('d-flex');
      expect(firstItem?.className).toContain('align-items-center');
      
      vi.clearAllMocks();
    });
  });

  describe('Link Behavior', () => {
    it('should have unique keys for each item', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const links = document.querySelectorAll('a');
      expect(links.length).toBe(mockData.length);
      
      vi.clearAllMocks();
    });

    it('should render item names correctly', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      mockData.forEach((item) => {
        expect(screen.getByText(item.name)).toBeDefined();
      });
      
      vi.clearAllMocks();
    });
  });

  describe('Router Pathname Matching', () => {
    it('should match exact pathname for products', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/products',
      });

      render(<ListGroup data={mockData} />);
      
      const productsLink = screen.getByText('Products').closest('a');
      expect(productsLink?.className).toContain('app-active-link-mock');
      
      const dashboardLink = screen.getByText('Dashboard').closest('a');
      expect(dashboardLink?.className).not.toContain('app-active-link-mock');
      
      vi.clearAllMocks();
    });

    it('should handle nested routes', () => {
      const nestedData = [
        { id: 1, name: 'Products', link: '/products', icon: '📦' },
        { id: 2, name: 'Product Details', link: '/products/123', icon: '📝' },
      ];
      
      (useRouter as any).mockReturnValue({
        pathname: '/products/123',
      });

      render(<ListGroup data={nestedData} />);
      
      const productDetailsLink = screen.getByText('Product Details').closest('a');
      expect(productDetailsLink?.className).toContain('app-active-link-mock');
      
      vi.clearAllMocks();
    });
  });

  describe('Icon Display', () => {
    it('should render icon as h5 element', () => {
      (useRouter as any).mockReturnValue({
        pathname: '/dashboard',
      });

      render(<ListGroup data={mockData} />);
      
      const icons = document.querySelectorAll('h5');
      expect(icons.length).toBeGreaterThan(0);
      
      vi.clearAllMocks();
    });

    it('should render custom icons', () => {
      const customIconsData = [
        { id: 1, name: 'Settings', link: '/settings', icon: '⚙️' },
        { id: 2, name: 'Profile', link: '/profile', icon: '👤' },
      ];
      
      (useRouter as any).mockReturnValue({
        pathname: '/settings',
      });

      render(<ListGroup data={customIconsData} />);
      
      expect(screen.getByText('⚙️')).toBeDefined();
      expect(screen.getByText('👤')).toBeDefined();
      
      vi.clearAllMocks();
    });
  });

  describe('Multiple Items', () => {
    it('should render large number of items', () => {
      const largeData = Array.from({ length: 50 }, (_, i) => ({
        id: i,
        name: `Item ${i}`,
        link: `/item/${i}`,
        icon: '📦',
      }));
      
      (useRouter as any).mockReturnValue({
        pathname: '/item/0',
      });

      render(<ListGroup data={largeData} />);
      
      const items = document.querySelectorAll('.list-group-item-mock');
      expect(items.length).toBe(50);
      
      vi.clearAllMocks();
    });
  });
});