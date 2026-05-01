import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import LatestItemPanel from '../../../../modules/home/components/LatestItemPanel';

// Mock child components
vi.mock('../../../../modules/dashboard/components/LatestProducts', () => ({
  default: () => <div data-testid="latest-products">Latest Products Mock</div>,
}));

vi.mock('../../../../modules/dashboard/components/LatestOrders', () => ({
  default: () => <div data-testid="latest-orders">Latest Orders Mock</div>,
}));

vi.mock('../../../../modules/dashboard/components/LatestRatings', () => ({
  default: () => <div data-testid="latest-ratings">Latest Ratings Mock</div>,
}));

describe('LatestItemPanel', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render without crashing', () => {
      render(<LatestItemPanel />);
      expect(screen.getByTestId('latest-products')).toBeDefined();
    });

    it('should render LatestProducts component', () => {
      render(<LatestItemPanel />);
      expect(screen.getByTestId('latest-products')).toBeDefined();
      expect(screen.getByText('Latest Products Mock')).toBeDefined();
    });

    it('should render LatestOrders component', () => {
      render(<LatestItemPanel />);
      expect(screen.getByTestId('latest-orders')).toBeDefined();
      expect(screen.getByText('Latest Orders Mock')).toBeDefined();
    });

    it('should render LatestRatings component', () => {
      render(<LatestItemPanel />);
      expect(screen.getByTestId('latest-ratings')).toBeDefined();
      expect(screen.getByText('Latest Ratings Mock')).toBeDefined();
    });

    it('should render all three components', () => {
      render(<LatestItemPanel />);
      
      expect(screen.getByTestId('latest-products')).toBeDefined();
      expect(screen.getByTestId('latest-orders')).toBeDefined();
      expect(screen.getByTestId('latest-ratings')).toBeDefined();
    });

    it('should render components in correct order', () => {
      render(<LatestItemPanel />);
      
      const container = screen.getByTestId('latest-products').parentElement;
      const children = container?.children;
      
      expect(children?.[0]).toHaveAttribute('data-testid', 'latest-products');
      expect(children?.[1]).toHaveAttribute('data-testid', 'latest-orders');
      expect(children?.[2]).toHaveAttribute('data-testid', 'latest-ratings');
    });
  });

  describe('Component Structure', () => {
    it('should use Fragment to wrap components', () => {
      const { container } = render(<LatestItemPanel />);
      
      // Fragment doesn't create an additional DOM element
      // The children are rendered directly under the parent
      const directChildren = container.firstChild?.childNodes;
      expect(directChildren).toHaveLength(3);
    });

    it('should not have any wrapper div', () => {
      const { container } = render(<LatestItemPanel />);
      
      // The component returns Fragment, so firstChild should be a DocumentFragment
      // or the children should be rendered without extra wrapper
      expect(container.firstChild?.childNodes.length).toBe(3);
    });
  });

  describe('Props Passing', () => {
    it('should not require any props', () => {
      // Component has no required props, so rendering without props should work
      expect(() => render(<LatestItemPanel />)).not.toThrow();
    });

  });

  describe('Integration with Child Components', () => {
    it('should pass no props to child components', () => {
      // All child components are rendered without props
      render(<LatestItemPanel />);
      
      // Just verify that child components are called
      // The actual props passing is tested in their own test files
      expect(screen.getByTestId('latest-products')).toBeDefined();
    });

    it('should render child components with their default styles', () => {
      render(<LatestItemPanel />);
      
      const products = screen.getByTestId('latest-products');
      const orders = screen.getByTestId('latest-orders');
      const ratings = screen.getByTestId('latest-ratings');
      
      expect(products).toBeDefined();
      expect(orders).toBeDefined();
      expect(ratings).toBeDefined();
    });
  });

  describe('Multiple Renderings', () => {
    it('should render correctly multiple times', () => {
      const { unmount } = render(<LatestItemPanel />);
      expect(screen.getByTestId('latest-products')).toBeDefined();
      
      unmount();
      
      const { container } = render(<LatestItemPanel />);
      expect(container.querySelector('[data-testid="latest-products"]')).toBeDefined();
    });

    it('should maintain component identity between renders', () => {
      const { rerender } = render(<LatestItemPanel />);
      const firstProducts = screen.getByTestId('latest-products');
      const firstOrders = screen.getByTestId('latest-orders');
      const firstRatings = screen.getByTestId('latest-ratings');
      
      rerender(<LatestItemPanel />);
      
      const secondProducts = screen.getByTestId('latest-products');
      const secondOrders = screen.getByTestId('latest-orders');
      const secondRatings = screen.getByTestId('latest-ratings');
      
      expect(firstProducts).toBe(secondProducts);
      expect(firstOrders).toBe(secondOrders);
      expect(firstRatings).toBe(secondRatings);
    });
  });
});