import {Injectable, signal} from '@angular/core';
import {GetOnlineUsers} from '../../../wailsjs/go/app/WailsApp';

@Injectable({providedIn: 'root'})
export class OnlineStatusService {
  readonly connectedUsers = signal<string[]>([]);

  private ws: WebSocket | null = null;
  private pollInterval: ReturnType<typeof setInterval> | null = null;

  connectWebSocket(baseUrl: string, token: string) {
    this.disconnect();
    const wsUrl = baseUrl.replace(/^https?/, m => m === 'https' ? 'wss' : 'ws') + '/ws?token=' + encodeURIComponent(token);
    this.ws = new WebSocket(wsUrl);
    this.ws.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data);
        if (Array.isArray(msg.users)) this.connectedUsers.set(msg.users);
      } catch {}
    };
    this.ws.onclose = () => this.connectedUsers.set([]);
    this.ws.onerror = () => this.ws?.close();
  }

  startLocalPolling() {
    this.disconnect();
    const poll = async () => {
      try {
        const users = await GetOnlineUsers();
        this.connectedUsers.set(users ?? []);
      } catch {}
    };
    poll();
    this.pollInterval = setInterval(poll, 5000);
  }

  disconnect() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    if (this.pollInterval) {
      clearInterval(this.pollInterval);
      this.pollInterval = null;
    }
    this.connectedUsers.set([]);
  }
}
