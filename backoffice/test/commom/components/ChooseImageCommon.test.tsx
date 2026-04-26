// test/common/components/ChooseImageCommon.test.tsx
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ChooseImageCommon from '../../../common/components/ChooseImageCommon';

// Mock styles
vi.mock('../../styles/ChooseImage.module.css', () => ({
  default: {
    'product-image': 'product-image-mock',
    'actions': 'actions-mock',
    'icon': 'icon-mock',
    'delete': 'delete-mock',
  },
}));

describe('ChooseImageCommon', () => {
  const defaultProps = {
    id: 'test-image-id',
    url: 'https://example.com/image.jpg',
    onDeleteImage: vi.fn(),
    iconStyle: { color: 'red', fontSize: '20px' },
    wrapperStyle: { border: '1px solid #ccc' },
    actionStyle: { backgroundColor: '#f0f0f0' },
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render image with correct src and alt', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const image = screen.getByRole('img');
      expect(image).toBeDefined();
      expect(image.getAttribute('src')).toBe('https://example.com/image.jpg');
      expect(image.getAttribute('alt')).toBe('image');
    });

    it('should render refresh icon (bi-arrow-repeat)', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const refreshIcon = document.querySelector('.bi-arrow-repeat');
      expect(refreshIcon).toBeDefined();
    });

    it('should render delete icon (bi-x-lg)', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const deleteIcon = document.querySelector('.bi-x-lg');
      expect(deleteIcon).toBeDefined();
    });

    it('should have label with correct htmlFor attribute', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const label = screen.getByLabelText(/arrow-repeat/i);
      expect(label).toBeDefined();
      expect(label.getAttribute('htmlFor')).toBe('test-image-id');
    });
  });

  describe('Props', () => {
    it('should apply wrapperStyle correctly', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const wrapper = document.querySelector('.product-image-mock');
      expect(wrapper).toBeDefined();
      expect(wrapper?.getAttribute('style')).toContain('border: 1px solid #ccc');
    });

    it('should apply iconStyle to refresh icon', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const refreshIconContainer = document.querySelector('.icon-mock');
      expect(refreshIconContainer?.getAttribute('style')).toContain('color: red');
      expect(refreshIconContainer?.getAttribute('style')).toContain('font-size: 20px');
    });

    it('should apply actionStyle correctly', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const actions = document.querySelector('.actions-mock');
      expect(actions?.getAttribute('style')).toContain('background-color: #f0f0f0');
    });

    it('should handle different image URLs', () => {
      const urls = [
        'https://example.com/photo1.jpg',
        'https://cdn.example.com/image.png',
        '/local/image.webp',
      ];

      urls.forEach((url) => {
        const { unmount } = render(<ChooseImageCommon {...defaultProps} url={url} />);
        const image = screen.getByRole('img');
        expect(image.getAttribute('src')).toBe(url);
        unmount();
      });
    });

    it('should handle different IDs', () => {
      const ids = ['id-1', 'id-2', 'custom-id'];
      
      ids.forEach((id) => {
        const { unmount } = render(<ChooseImageCommon {...defaultProps} id={id} />);
        const label = screen.getByLabelText(/arrow-repeat/i);
        expect(label.getAttribute('htmlFor')).toBe(id);
        unmount();
      });
    });
  });

  describe('Events', () => {
    it('should call onDeleteImage when delete icon is clicked', () => {
      const onDeleteImage = vi.fn();
      render(<ChooseImageCommon {...defaultProps} onDeleteImage={onDeleteImage} />);
      
      const deleteDiv = document.querySelector('.delete-mock');
      expect(deleteDiv).toBeDefined();
      
      fireEvent.click(deleteDiv!);
      expect(onDeleteImage).toHaveBeenCalledTimes(1);
    });

    it('should not call onDeleteImage when refresh icon is clicked', () => {
      const onDeleteImage = vi.fn();
      render(<ChooseImageCommon {...defaultProps} onDeleteImage={onDeleteImage} />);
      
      const refreshIcon = document.querySelector('.icon-mock');
      expect(refreshIcon).toBeDefined();
      
      fireEvent.click(refreshIcon!);
      expect(onDeleteImage).not.toHaveBeenCalled();
    });
  });

  describe('CSS Classes', () => {
    it('should have correct CSS classes', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const wrapper = document.querySelector('.product-image-mock');
      const actions = document.querySelector('.actions-mock');
      const icons = document.querySelectorAll('.icon-mock');
      
      expect(wrapper).toBeDefined();
      expect(actions).toBeDefined();
      expect(icons.length).toBe(2); // refresh and delete icons
    });

    it('should apply delete class to delete icon', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const deleteDiv = document.querySelector('.delete-mock');
      expect(deleteDiv).toBeDefined();
      expect(deleteDiv?.className).toContain('delete-mock');
    });
  });

  describe('Edge cases', () => {
    it('should handle empty URL', () => {
      render(<ChooseImageCommon {...defaultProps} url="" />);
      
      const image = screen.getByRole('img');
      expect(image.getAttribute('src')).toBe('');
    });

    it('should handle undefined styles', () => {
      render(
        <ChooseImageCommon
          id="test"
          url="test.jpg"
          onDeleteImage={vi.fn()}
        />
      );
      
      const image = screen.getByRole('img');
      expect(image).toBeDefined();
      
      const refreshIcon = document.querySelector('.bi-arrow-repeat');
      expect(refreshIcon).toBeDefined();
    });

    it('should handle empty onDeleteImage', () => {
      render(
        <ChooseImageCommon
          id="test"
          url="test.jpg"
          onDeleteImage={undefined as any}
        />
      );
      
      const deleteDiv = document.querySelector('.delete-mock');
      expect(deleteDiv).toBeDefined();
      
      // Should not throw error
      fireEvent.click(deleteDiv!);
    });
  });

  describe('Accessibility', () => {
    it('should have alt text for image', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const image = screen.getByRole('img');
      expect(image.getAttribute('alt')).toBe('image');
    });

    it('should have label for refresh icon', () => {
      render(<ChooseImageCommon {...defaultProps} />);
      
      const label = screen.getByLabelText(/arrow-repeat/i);
      expect(label).toBeDefined();
    });
  });
});