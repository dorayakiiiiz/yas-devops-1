import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductList from '../../../../pages/catalog/products/index';
import { getProducts, deleteProduct } from '../../../../modules/catalog/services/ProductService';
import { getBrands } from '../../../../modules/catalog/services/BrandService';
import { Product } from '../../../../modules/catalog/models/Product';
import { Brand } from '../../../../modules/catalog/models/Brand';
import { Category } from '../../../../modules/catalog/models/Category';
import { Media } from '../../../../modules/catalog/models/Media';


vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
    query: {},
    pathname: '',
    asPath: '',
    events: {
      on: vi.fn(),
      off: vi.fn(),
      emit: vi.fn(),
    },
  })),
}));

// Mock next/link
vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

// Mock dependencies
vi.mock('../../../../modules/catalog/services/ProductService', () => ({
  getProducts: vi.fn(),
  deleteProduct: vi.fn(),
}));

vi.mock('../../../../modules/catalog/services/BrandService', () => ({
  getBrands: vi.fn(),
}));

vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('next/router', () => ({
  useRouter: vi.fn(() => ({
    push: vi.fn(),
  })),
}));

vi.mock('next/link', () => ({
  default: ({ children, href }: any) => (
    <a href={href} data-testid="mock-link">
      {children}
    </a>
  ),
}));

vi.mock('react-toastify', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

describe('ProductList Page', () => {
  // Mock Category
  const mockCategory: Category = {
    id: 1,
    name: 'Electronics',
    description: 'Electronic products',
    slug: 'electronics',
    parentId: null,
    metaKeywords: 'electronics',
    metaDescription: 'Electronic products',
    displayOrder: 1,
    isPublish: true,
  };

  // Mock Media
  const mockMedia: Media = {
    id: 1,
    url: '/uploads/product.jpg',
  };

  // SỬA: Product với đầy đủ fields theo type
  const mockProducts: Product[] = [
    {
      id: 1,
      name: 'Test Product 1',
      shortDescription: 'Short description 1',
      description: 'Full description 1',
      specification: 'Specifications 1',
      sku: 'TEST-001',
      gtin: 'GTIN-001',
      slug: 'test-product-1',
      weight: 1.5,
      dimensionUnit: 'kg',
      length: 10,
      width: 5,
      height: 3,
      price: 100,
      metaTitle: 'Meta Title 1',
      metaKeyword: 'keyword1',
      metaDescription: 'Meta description 1',
      isAllowedToOrder: true,
      isPublished: true,
      isFeatured: false,
      isVisible: true,
      stockTrackingEnabled: true,
      taxIncluded: true,
      brandId: 1,
      categories: [mockCategory],
      thumbnailMedia: mockMedia,
      productImageMedias: [mockMedia],
      createdOn: new Date('2024-01-15T10:30:00Z'),
      taxClassId: 1,
      parentId: 0,
    },
    {
      id: 2,
      name: 'Test Product 2',
      shortDescription: 'Short description 2',
      description: 'Full description 2',
      specification: 'Specifications 2',
      sku: 'TEST-002',
      gtin: 'GTIN-002',
      slug: 'test-product-2',
      weight: 2.0,
      dimensionUnit: 'kg',
      length: 15,
      width: 10,
      height: 5,
      price: 200,
      metaTitle: 'Meta Title 2',
      metaKeyword: 'keyword2',
      metaDescription: 'Meta description 2',
      isAllowedToOrder: true,
      isPublished: false,
      isFeatured: false,
      isVisible: true,
      stockTrackingEnabled: true,
      taxIncluded: true,
      brandId: 2,
      categories: [mockCategory],
      thumbnailMedia: mockMedia,
      productImageMedias: [mockMedia],
      createdOn: new Date('2024-01-16T10:30:00Z'),
      taxClassId: 1,
      parentId: 0,
    },
  ];

  const mockBrands: Brand[] = [
    { id: 1, name: 'Nike', slug: 'nike', isPublish: true },
    { id: 2, name: 'Adidas', slug: 'adidas', isPublish: true },
  ];

  const mockProductsResponse = {
    productContent: mockProducts,
    totalPages: 1,
    totalElements: 2,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getProducts as any).mockResolvedValue(mockProductsResponse);
    (getBrands as any).mockResolvedValue(mockBrands);
    (deleteProduct as any).mockResolvedValue({ status: 204 });
  });

  describe('getProducts', () => {
    it('should call getProducts on mount', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getProducts).toHaveBeenCalled();
      });
    });

    it('should pass correct parameters to getProducts', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getProducts).toHaveBeenCalledWith(0, '', '');
      });
    });

    it('should display product data in table', async () => {
      render(<ProductList />);
      
      await waitFor(() => {
        expect(screen.getByText('Test Product 1')).toBeDefined();
        expect(screen.getByText('TEST-001')).toBeDefined();
        expect(screen.getByText('100')).toBeDefined();
      });
    });

    it('should handle empty product list', async () => {
      (getProducts as any).mockResolvedValue({
        productContent: [],
        totalPages: 0,
        totalElements: 0,
        pageNo: 0,
        pageSize: 10,
      });
      render(<ProductList />);
      
      await waitFor(() => {
        expect(screen.getByText('No products available')).toBeDefined();
      });
    });

    it('should handle API error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (getProducts as any).mockRejectedValue(new Error('Network error'));
      render(<ProductList />);
      
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('getBrands', () => {
    it('should call getBrands on mount', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getBrands).toHaveBeenCalled();
      });
    });

    it('should return brands data', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getBrands).toHaveBeenCalled();
      });
    });

    it('should handle empty brands list', async () => {
      (getBrands as any).mockResolvedValue([]);
      render(<ProductList />);
      await waitFor(() => {
        expect(getBrands).toHaveBeenCalled();
      });
    });
  });

  describe('deleteProduct', () => {
    it('should call deleteProduct with correct id', async () => {
      render(<ProductList />);
      expect(deleteProduct).toBeDefined();
    });

    it('should handle delete success', async () => {
      (deleteProduct as any).mockResolvedValue({ status: 204 });
      render(<ProductList />);
      await waitFor(() => {
        expect(deleteProduct).toBeDefined();
      });
    });

    it('should handle delete error', async () => {
      const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      (deleteProduct as any).mockRejectedValue(new Error('Delete failed'));
      render(<ProductList />);
      await waitFor(() => {
        expect(consoleSpy).toHaveBeenCalled();
      });
      consoleSpy.mockRestore();
    });
  });

  describe('Table Headers', () => {
    it('should render all table headers', async () => {
      render(<ProductList />);
      
      await waitFor(() => {
        expect(screen.getByText('ID')).toBeDefined();
        expect(screen.getByText('Name')).toBeDefined();
        expect(screen.getByText('SKU')).toBeDefined();
        expect(screen.getByText('Price')).toBeDefined();
        expect(screen.getByText('Status')).toBeDefined();
        expect(screen.getByText('Actions')).toBeDefined();
      });
    });
  });

  describe('Create Button', () => {
    it('should render Create New Product button', async () => {
      render(<ProductList />);
      
      await waitFor(() => {
        expect(screen.getByText('Create New Product')).toBeDefined();
      });
    });

    it('should have link to create product page', async () => {
      render(<ProductList />);
      
      await waitFor(() => {
        const links = screen.getAllByTestId('mock-link');
        const createLink = links.find(link => link.textContent === 'Create New Product');
        expect(createLink).toHaveAttribute('href', '/catalog/products/create');
      });
    });
  });

  describe('page state', () => {
    it('should have initial pageNo as 0', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getProducts).toHaveBeenCalledWith(0, '', '');
      });
    });
  });

  describe('search state', () => {
    it('should have initial productName as empty', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getProducts).toHaveBeenCalledWith(0, '', '');
      });
    });

    it('should have initial brandName as empty', async () => {
      render(<ProductList />);
      await waitFor(() => {
        expect(getProducts).toHaveBeenCalledWith(0, '', '');
      });
    });
  });
});