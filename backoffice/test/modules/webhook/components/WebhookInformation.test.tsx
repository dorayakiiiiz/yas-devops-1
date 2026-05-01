import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import WebhookInformation from '../../../../modules/webhook/components/WebhookInformation';
import { ContentType } from '../../../../modules/webhook/models/ContentType';

// Mock Input component
vi.mock('common/items/Input', () => ({
  Input: ({ labelText, field, defaultValue, register, registerOptions, error, disabled }: any) => (
    <div>
      <label htmlFor={field}>{labelText}</label>
      <input
        id={field}
        data-testid={`input-${field}`}
        defaultValue={defaultValue}
        disabled={disabled}
        onChange={(e) => {
          if (registerOptions?.onChange) {
            registerOptions.onChange(e);
          }
          if (register) {
            register(field, registerOptions).onChange(e);
          }
        }}
        onBlur={() => register && register(field, registerOptions).onBlur()}
      />
      {error && <span data-testid={`error-${field}`}>{error}</span>}
    </div>
  ),
  CheckBox: ({ labelText, field, register, defaultChecked }: any) => (
    <div>
      <label htmlFor={field}>
        <input
          id={field}
          type="checkbox"
          data-testid={`checkbox-${field}`}
          defaultChecked={defaultChecked}
          onChange={(e) => register && register(field).onChange(e)}
        />
        {labelText}
      </label>
    </div>
  ),
}));

describe('WebhookInformation', () => {
  const mockRegister = vi.fn((field, options) => ({
    name: field,
    onChange: vi.fn(),
    onBlur: vi.fn(),
    ref: vi.fn(),
  }));

  const mockSetValue = vi.fn();
  const mockTrigger = vi.fn();

  const mockWebhook = {
    id: 1,
    payloadUrl: 'https://example.com/webhook',
    secret: 'my-secret-key',
    isActive: true,
  };

  const defaultProps = {
    register: mockRegister,
    errors: {},
    setValue: mockSetValue,
    trigger: mockTrigger,
    webhook: undefined,
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render all form fields', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(screen.getByText('Payload URL')).toBeDefined();
      expect(screen.getByText('Content Type')).toBeDefined();
      expect(screen.getByText('Secret')).toBeDefined();
      expect(screen.getByText('Active')).toBeDefined();
    });

    it('should render Payload URL input', () => {
      render(<WebhookInformation {...defaultProps} />);
      expect(screen.getByTestId('input-payloadUrl')).toBeDefined();
    });

    it('should render Content Type input', () => {
      render(<WebhookInformation {...defaultProps} />);
      expect(screen.getByTestId('input-contentType')).toBeDefined();
    });

    it('should render Secret input', () => {
      render(<WebhookInformation {...defaultProps} />);
      expect(screen.getByTestId('input-secret')).toBeDefined();
    });

    it('should render Active checkbox', () => {
      render(<WebhookInformation {...defaultProps} />);
      expect(screen.getByTestId('checkbox-isActive')).toBeDefined();
    });
  });

  describe('Default Values', () => {
    it('should render with empty fields when webhook is undefined', () => {
      render(<WebhookInformation {...defaultProps} />);

      const payloadUrlInput = screen.getByTestId('input-payloadUrl') as HTMLInputElement;
      const secretInput = screen.getByTestId('input-secret') as HTMLInputElement;

      expect(payloadUrlInput.defaultValue).toBeUndefined();
      expect(secretInput.defaultValue).toBeUndefined();
    });

    it('should set Content Type default value to APPLICATION_JSON', () => {
      render(<WebhookInformation {...defaultProps} />);

      const contentTypeInput = screen.getByTestId('input-contentType') as HTMLInputElement;
      expect(contentTypeInput.defaultValue).toBe(ContentType.APPLICATION_JSON);
    });

    it('should set Active checkbox defaultChecked to false when webhook not provided', () => {
      render(<WebhookInformation {...defaultProps} />);

      const activeCheckbox = screen.getByTestId('checkbox-isActive') as HTMLInputElement;
      expect(activeCheckbox.checked).toBe(false);
    });

  });

  describe('Register Options', () => {
    it('should register payloadUrl field with required validation', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('payloadUrl', {
        required: { value: true, message: 'Payload URL is required' },
      });
    });

    it('should register contentType field without validation and disabled', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('contentType', undefined);
    });

    it('should register secret field without validation', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('secret', undefined);
    });

    it('should register isActive field', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledWith('isActive');
    });

    it('should call register 4 times', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(mockRegister).toHaveBeenCalledTimes(4);
    });
  });

  describe('Content Type Field', () => {
    it('should render Content Type field as disabled', () => {
      render(<WebhookInformation {...defaultProps} />);

      const contentTypeInput = screen.getByTestId('input-contentType');
      expect(contentTypeInput).toBeDisabled();
    });

    it('should have correct default value for Content Type', () => {
      render(<WebhookInformation {...defaultProps} />);

      const contentTypeInput = screen.getByTestId('input-contentType') as HTMLInputElement;
      expect(contentTypeInput.defaultValue).toBe(ContentType.APPLICATION_JSON);
    });
  });

  describe('Error Display', () => {

    it('should not display error when no errors', () => {
      render(<WebhookInformation {...defaultProps} />);

      expect(screen.queryByTestId('error-payloadUrl')).toBeNull();
      expect(screen.queryByTestId('error-secret')).toBeNull();
    });
  });

  describe('Form Interaction', () => {
    it('should handle payloadUrl input change', () => {
      render(<WebhookInformation {...defaultProps} />);

      const payloadUrlInput = screen.getByTestId('input-payloadUrl');
      fireEvent.change(payloadUrlInput, { target: { value: 'https://new-url.com/webhook' } });

      expect(payloadUrlInput).toHaveValue('https://new-url.com/webhook');
    });

    it('should handle secret input change', () => {
      render(<WebhookInformation {...defaultProps} />);

      const secretInput = screen.getByTestId('input-secret');
      fireEvent.change(secretInput, { target: { value: 'new-secret-key' } });

      expect(secretInput).toHaveValue('new-secret-key');
    });

    it('should handle active checkbox toggle', () => {
      render(<WebhookInformation {...defaultProps} />);

      const activeCheckbox = screen.getByTestId('checkbox-isActive') as HTMLInputElement;
      fireEvent.click(activeCheckbox);

      expect(activeCheckbox.checked).toBe(true);
    });

    it('should handle empty payloadUrl input', () => {
      render(<WebhookInformation {...defaultProps} />);

      const payloadUrlInput = screen.getByTestId('input-payloadUrl');
      fireEvent.change(payloadUrlInput, { target: { value: '' } });

      expect(payloadUrlInput).toHaveValue('');
    });
  });

  describe('Accessibility', () => {
    it('should have labels associated with inputs', () => {
      render(<WebhookInformation {...defaultProps} />);

      const payloadUrlLabel = screen.getByText('Payload URL');
      const payloadUrlInput = screen.getByTestId('input-payloadUrl');

      expect(payloadUrlLabel).toHaveAttribute('for', 'payloadUrl');
      expect(payloadUrlInput).toHaveAttribute('id', 'payloadUrl');
    });

    it('should have correct id for Content Type input', () => {
      render(<WebhookInformation {...defaultProps} />);

      const contentTypeInput = screen.getByTestId('input-contentType');
      expect(contentTypeInput).toHaveAttribute('id', 'contentType');
    });

    it('should have correct id for Secret input', () => {
      render(<WebhookInformation {...defaultProps} />);

      const secretInput = screen.getByTestId('input-secret');
      expect(secretInput).toHaveAttribute('id', 'secret');
    });

    it('should have correct id for Active checkbox', () => {
      render(<WebhookInformation {...defaultProps} />);

      const activeCheckbox = screen.getByTestId('checkbox-isActive');
      expect(activeCheckbox).toHaveAttribute('id', 'isActive');
    });
  });
});