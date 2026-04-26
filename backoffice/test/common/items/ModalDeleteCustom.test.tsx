// test/common/items/ModalDeleteCustom.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ModalDeleteCustom from '../../../common/items/ModalDeleteCustom';

// Mock react-bootstrap styles
vi.mock('react-bootstrap', () => ({
  Modal: ({ children, show, onHide }: any) => (
    <div data-testid="modal" data-show={show} onClick={onHide}>
      {children}
    </div>
  ),
  Button: ({ children, onClick, variant }: any) => (
    <button data-testid="button" data-variant={variant} onClick={onClick}>
      {children}
    </button>
  ),
}));

describe('ModalDeleteCustom', () => {
  const defaultProps = {
    showModalDelete: true,
    handleClose: vi.fn(),
    nameWantToDelete: 'Product',
    handleDelete: vi.fn(),
    action: 'delete',
  };

  describe('Rendering', () => {
    it('should render modal when showModalDelete is true', () => {
      render(<ModalDeleteCustom {...defaultProps} />);
      
      const modal = screen.getByTestId('modal');
      expect(modal).toBeDefined();
      expect(modal.getAttribute('data-show')).toBe('true');
    });

    it('should not render modal content when showModalDelete is false', () => {
      render(<ModalDeleteCustom {...defaultProps} showModalDelete={false} />);
      
      const modal = screen.getByTestId('modal');
      expect(modal.getAttribute('data-show')).toBe('false');
    });

    it('should render confirmation message with correct text', () => {
      render(<ModalDeleteCustom {...defaultProps} />);
      
      const message = screen.getByText(`Are you sure you want to delete this Product ?`);
      expect(message).toBeDefined();
    });

    it('should render dynamic action in message', () => {
      const actions = ['delete', 'remove', 'archive', 'disable'];
      
      actions.forEach((action) => {
        const { unmount } = render(
          <ModalDeleteCustom {...defaultProps} action={action} />
        );
        
        const message = screen.getByText(`Are you sure you want to ${action} this Product ?`);
        expect(message).toBeDefined();
        
        unmount();
      });
    });

    it('should render dynamic name in message', () => {
      const names = ['User', 'Order', 'Category', 'Brand'];
      
      names.forEach((name) => {
        const { unmount } = render(
          <ModalDeleteCustom {...defaultProps} nameWantToDelete={name} />
        );
        
        const message = screen.getByText(`Are you sure you want to delete this ${name} ?`);
        expect(message).toBeDefined();
        
        unmount();
      });
    });

    it('should render Close button', () => {
      render(<ModalDeleteCustom {...defaultProps} />);
      
      const closeButton = screen.getByText('Close');
      expect(closeButton).toBeDefined();
    });

    it('should render Delete button', () => {
      render(<ModalDeleteCustom {...defaultProps} />);
      
      const deleteButton = screen.getByText('Delete');
      expect(deleteButton).toBeDefined();
    });
  });

  describe('Button Variants', () => {
    it('should have outline-secondary variant for Close button', () => {
      render(<ModalDeleteCustom {...defaultProps} />);
      
      const closeButton = screen.getByText('Close');
      expect(closeButton.getAttribute('data-variant')).toBe('outline-secondary');
    });

    it('should have danger variant for Delete button', () => {
      render(<ModalDeleteCustom {...defaultProps} />);
      
      const deleteButton = screen.getByText('Delete');
      expect(deleteButton.getAttribute('data-variant')).toBe('danger');
    });
  });

  describe('Interactions', () => {
    it('should call handleClose when Close button is clicked', () => {
      const handleClose = vi.fn();
      render(<ModalDeleteCustom {...defaultProps} handleClose={handleClose} />);
      
      const closeButton = screen.getByText('Close');
      fireEvent.click(closeButton);
      
      expect(handleClose).toHaveBeenCalledTimes(1);
    });

    it('should call handleDelete when Delete button is clicked', () => {
      const handleDelete = vi.fn();
      render(<ModalDeleteCustom {...defaultProps} handleDelete={handleDelete} />);
      
      const deleteButton = screen.getByText('Delete');
      fireEvent.click(deleteButton);
      
      expect(handleDelete).toHaveBeenCalledTimes(1);
    });

    it('should call handleClose when modal backdrop is clicked', () => {
      const handleClose = vi.fn();
      render(<ModalDeleteCustom {...defaultProps} handleClose={handleClose} />);
      
      const modal = screen.getByTestId('modal');
      fireEvent.click(modal);
      
      expect(handleClose).toHaveBeenCalledTimes(1);
    });

    it('should not call handleDelete when modal backdrop is clicked', () => {
      const handleDelete = vi.fn();
      render(<ModalDeleteCustom {...defaultProps} handleDelete={handleDelete} />);
      
      const modal = screen.getByTestId('modal');
      fireEvent.click(modal);
      
      expect(handleDelete).not.toHaveBeenCalled();
    });
  });

  describe('Multiple clicks', () => {
    it('should handle multiple Close button clicks', () => {
      const handleClose = vi.fn();
      render(<ModalDeleteCustom {...defaultProps} handleClose={handleClose} />);
      
      const closeButton = screen.getByText('Close');
      fireEvent.click(closeButton);
      fireEvent.click(closeButton);
      fireEvent.click(closeButton);
      
      expect(handleClose).toHaveBeenCalledTimes(3);
    });

    it('should handle multiple Delete button clicks', () => {
      const handleDelete = vi.fn();
      render(<ModalDeleteCustom {...defaultProps} handleDelete={handleDelete} />);
      
      const deleteButton = screen.getByText('Delete');
      fireEvent.click(deleteButton);
      fireEvent.click(deleteButton);
      
      expect(handleDelete).toHaveBeenCalledTimes(2);
    });
  });

  describe('Different messages', () => {
    it('should show correct message for different actions', () => {
      const testCases = [
        { action: 'delete', name: 'User', expected: 'Are you sure you want to delete this User ?' },
        { action: 'remove', name: 'Item', expected: 'Are you sure you want to remove this Item ?' },
        { action: 'archive', name: 'Order', expected: 'Are you sure you want to archive this Order ?' },
        { action: 'disable', name: 'Account', expected: 'Are you sure you want to disable this Account ?' },
      ];
      
      testCases.forEach((testCase) => {
        const { unmount } = render(
          <ModalDeleteCustom
            showModalDelete={true}
            handleClose={vi.fn()}
            nameWantToDelete={testCase.name}
            handleDelete={vi.fn()}
            action={testCase.action}
          />
        );
        
        const message = screen.getByText(testCase.expected);
        expect(message).toBeDefined();
        
        unmount();
      });
    });
  });

  describe('Edge cases', () => {
    it('should handle empty nameWantToDelete', () => {
      render(<ModalDeleteCustom {...defaultProps} nameWantToDelete="" />);
      
      const message = screen.getByText(`Are you sure you want to delete this  ?`);
      expect(message).toBeDefined();
    });

    it('should handle long nameWantToDelete', () => {
      const longName = 'VeryLongProductNameThatMightBreakTheLayout'.repeat(5);
      render(<ModalDeleteCustom {...defaultProps} nameWantToDelete={longName} />);
      
      const message = screen.getByText(`Are you sure you want to delete this ${longName} ?`);
      expect(message).toBeDefined();
    });

    it('should handle special characters in name', () => {
      const specialName = 'Product @#$%^&*()';
      render(<ModalDeleteCustom {...defaultProps} nameWantToDelete={specialName} />);
      
      const message = screen.getByText(`Are you sure you want to delete this ${specialName} ?`);
      expect(message).toBeDefined();
    });
  });
});