package com.peanut.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author: peanut
 * @date: 2026/4/28
 * @version:1.0
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        logger.info("WebSocket connection closed. Session ID: {}", event.getSessionId());
    }

    @EventListener
    public void handleSessionConnect(SessionConnectedEvent event) {
        logger.info("WebSocket connection established. Session ID: {}", event.getMessage().getHeaders().get("simpSessionId"));
    }
}
