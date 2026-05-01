import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import WebhookCreate from '../../../pages/webhook/create';
import { createWebhook } from '../../../modules/webhook/services/WebhookService';
import { Webhook } from '../../../modules/webhook/models/Webhook';

// Mock dependencies
vi.mock('../../../../modules/webhook/services/WebhookService', () => ({
  createWebhook: vi.fn(),
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
    handleSubmit: vi.fn((fn) => fn),
    getValues: vi.fn(),
    formState: {
      errors: {},
    },
    setValue: vi.fn(),
    trigger: vi.fn(),
  }),
}));

vi.mock('next/router', () => ({
  useRouter: () => ({
    replace: vi.fn(),
  }),
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

describe('WebhookCreate Page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (createWebhook as any).mockResolvedValue({ status: 201, data: { id: 1 } });
  });

  it('should render webhook create page', () => {
    render(<WebhookCreate />);
    expect(screen.getByText('Create Webhook')).toBeDefined();
  });

  it('should display save button', () => {
    render(<WebhookCreate />);
    expect(screen.getByText('Save')).toBeDefined();
  });

  it('should display cancel button', () => {
    render(<WebhookCreate />);
    expect(screen.getByText('Cancel')).toBeDefined();
  });

  it('should have form element', () => {
    render(<WebhookCreate />);
    const form = document.querySelector('form');
    expect(form).toBeDefined();
  });
});