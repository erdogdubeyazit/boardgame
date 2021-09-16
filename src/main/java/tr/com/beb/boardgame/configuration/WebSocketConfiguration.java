package tr.com.beb.boardgame.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import tr.com.beb.boardgame.web.socket.WebSocketDispatcher;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private ApplicationProperties applicationProperties;

    private WebSocketDispatcher requestDispatcher;

    public WebSocketConfiguration(WebSocketDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(requestDispatcher, applicationProperties.getWebSocketServerUrl())
                .setAllowedOriginPatterns("*").withSockJS();
    }
}