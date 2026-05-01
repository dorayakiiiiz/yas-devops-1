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

  describe('getEvents', () => {
    it('should call API with correct URL', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockEvents) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      await getEvents();

      expect(apiClientService.get).toHaveBeenCalledTimes(1);
      expect(apiClientService.get).toHaveBeenCalledWith('/api/webhook/backoffice/events');
    });

    it('should return events data on success', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue(mockEvents) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getEvents();

      expect(result).toEqual(mockEvents);
      expect(result).toHaveLength(4);
      expect(result[0].name).toBe('Order Created');
    });



  });
});