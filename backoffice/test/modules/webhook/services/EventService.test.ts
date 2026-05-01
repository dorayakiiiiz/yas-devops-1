import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getEvents } from '../../../../modules/webhook/services/EventService';
import { WebhookEvent } from '../../../../modules/webhook/models/Event';

// Mock ApiClientService
vi.mock('../../../../common/services/ApiClientService', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import apiClientService from '../../../../common/services/ApiClientService';

describe('EventService', () => {
  const mockEvents: WebhookEvent[] = [
    { id: 1, name: 'Order Created' },
    { id: 2, name: 'Order Updated' },
    { id: 3, name: 'Order Cancelled' },
    { id: 4, name: 'Payment Success' },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

});