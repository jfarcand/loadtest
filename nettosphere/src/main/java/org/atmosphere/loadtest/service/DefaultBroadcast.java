package org.atmosphere.loadtest.service;

import org.atmosphere.config.service.Singleton;
import org.atmosphere.config.service.WebSocketHandlerService;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketHandlerAdapter;

import java.io.IOException;

@Singleton
@WebSocketHandlerService(path = "/default/{id}")
public class DefaultBroadcast extends WebSocketHandlerAdapter {

    @Override
    public void onTextMessage(WebSocket webSocket, String data) throws IOException {
        webSocket.resource().getBroadcaster().broadcast(data);
    }
}
