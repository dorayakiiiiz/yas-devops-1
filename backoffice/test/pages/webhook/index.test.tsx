import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import WebhookList from '../../../../pages/webhook/index';
import { getWebhooks, deleteWebhook } from '../../../../modules/webhook/services/WebhookService';
import { Webhook } from '../../../../modules/webhook/models/Webhook';

// Mock dependencies
vi.mock('../../../../modules/webhook/services/WebhookService', () => ({
  getWebhooks: vi.fn(),
  deleteWebhook: vi.fn(),
}));

vi.mock('../../../../commonServices/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock('react', () => ({
  ...vi.importActual('react'),
  useEffect: vi.fn(),
}));

describe('WebhookList Page', () => {
  const mockWebhooks: Webhook[] = [
    {
      id: 1,
      payloadUrl: 'https://example.com/webhook1',
      secret: 'secret1',
      contentType: 'application/json',
      isActive: true,
      events: [{ id: 1, name: 'Order Created' }],
    },
    {
      id: 2,
      payloadUrl: 'https://example.com/webhook2',
      secret: 'secret2',
      contentType: 'application/xml',
      isActive: false,
      events: [{ id: 2, name: 'Order Updated' }],
    },
  ];

  const mockResponse = {
    webhooks: mockWebhooks,
    totalPages: 1,
    totalElements: 2,
    pageNo: 0,
    pageSize: 10,
  };

  beforeEach(() => {
    vi.clearAllMocks();
    (getWebhooks as any).mockResolvedValue(mockResponse);
    (deleteWebhook as any).mockResolvedValue({ status: 204 });
  });

  it('should render webhook list page', () => {
    render(<WebhookList />);
    expect(screen.getByText('Webhook')).toBeDefined();
  });

  it('should display create button', () => {
    render(<WebhookList />);
    expect(screen.getByText('Create Webhook')).toBeDefined();
  });

  it('should call getWebhooks on mount', async () => {
    render(<WebhookList />);
    await waitFor(() => {
      expect(getWebhooks).toHaveBeenCalled();
    });
  });

  it('should display webhook data in table', async () => {
    render(<WebhookList />);
    await waitFor(() => {
      expect(screen.getByText('https://example.com/webhook1')).toBeDefined();
      expect(screen.getByText('https://example.com/webhook2')).toBeDefined();
    });
  });

  it('should display content type column', async () => {
    render(<WebhookList />);
    await waitFor(() => {
      expect(screen.getByText('application/json')).toBeDefined();
      expect(screen.getByText('application/xml')).toBeDefined();
    });
  });

  it('should display status column', async () => {
    render(<WebhookList />);
    await waitFor(() => {
      expect(screen.getByText('Active')).toBeDefined();
    });
  });

  it('should handle loading state', () => {
    render(<WebhookList />);
    expect(screen.getByText('Loading...')).toBeDefined();
  });

  it('should handle empty webhook list', async () => {
    (getWebhooks as any).mockResolvedValue({
      webhooks: [],
      totalPages: 0,
      totalElements: 0,
      pageNo: 0,
      pageSize: 10,
    });
    render(<WebhookList />);
    await waitFor(() => {
      expect(screen.getByText('No Webhook')).toBeDefined();
    });
  });

  it('should handle API error', async () => {
    const consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    (getWebhooks as any).mockRejectedValue(new Error('Network error'));
    render(<WebhookList />);
    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalled();
    });
    consoleSpy.mockRestore();
  });
});