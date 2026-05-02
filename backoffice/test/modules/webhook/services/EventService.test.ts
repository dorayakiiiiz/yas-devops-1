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
      expect(result[1].name).toBe('Order Updated');
      expect(result[2].name).toBe('Order Cancelled');
      expect(result[3].name).toBe('Payment Success');
    });

    it('should handle single event', async () => {
      const singleEvent = [{ id: 1, name: 'Single Event' }];
      const mockResponse = { json: vi.fn().mockResolvedValue(singleEvent) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getEvents();

      expect(result).toEqual(singleEvent);
      expect(result).toHaveLength(1);
    });

    it('should handle empty events list', async () => {
      const mockResponse = { json: vi.fn().mockResolvedValue([]) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getEvents();

      expect(result).toEqual([]);
      expect(result).toHaveLength(0);
    });

    it('should handle API error', async () => {
      const error = new Error('Network error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getEvents()).rejects.toThrow('Network error');
    });

    it('should handle 500 server error', async () => {
      const error = new Error('Internal server error');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getEvents()).rejects.toThrow('Internal server error');
    });

    it('should handle 404 not found error', async () => {
      const error = new Error('Not found');
      (apiClientService.get as any).mockRejectedValue(error);

      await expect(getEvents()).rejects.toThrow('Not found');
    });

    it('should handle invalid response data', async () => {
      const invalidData = { notAnArray: true };
      const mockResponse = { json: vi.fn().mockResolvedValue(invalidData) };
      (apiClientService.get as any).mockResolvedValue(mockResponse);

      const result = await getEvents();

      // Should still return whatever the API returns
      expect(result).toEqual(invalidData);
    });
  });
});