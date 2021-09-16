package tr.com.beb.boardgame.web.socket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class ChannelHandlerResolver {

    private static final Logger logger = LoggerFactory.getLogger(ChannelHandlerResolver.class);

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    // <Channel pattern, Channel invoker> pair
    private final Map<String, ChannelHandlerInvoker> channelHandlerInvokerMap = new HashMap<>();

    private ApplicationContext applicationContext;

    public ChannelHandlerResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.init();
    }

    public ChannelHandlerInvoker getInvoker(IncomingMessage incomingMessage) {
        ChannelHandlerInvoker channelHandlerInvoker = null;
        Set<String> pathPatterns = channelHandlerInvokerMap.keySet();
        for (String pathPattern : pathPatterns) {
            if (antPathMatcher.match(pathPattern, incomingMessage.getChannel()))
                channelHandlerInvoker = channelHandlerInvokerMap.get(pathPattern);
        }
        if (channelHandlerInvoker == null)
            return null;
        return channelHandlerInvoker.supports(incomingMessage.getAction()) ? channelHandlerInvoker : null;
    }

    private void init() {
        logger.info("ChannelHandlerResolver is initializing");

        Map<String, Object> handlerMap = applicationContext.getBeansWithAnnotation(ChannelHandler.class);
        for (String handlerName : handlerMap.keySet()) {
            Object handler = handlerMap.get(handlerName);
            Class<?> handlerClass = handler.getClass();

            ChannelHandler handlerAnnotation = handlerClass.getAnnotation(ChannelHandler.class);
            String channelPattern = ChannelHandlers.getPattern(handlerAnnotation);
            if (channelHandlerInvokerMap.containsKey(channelPattern))
                throw new IllegalStateException("Channel pattern`" + channelPattern + "` has duplicates.");
            channelHandlerInvokerMap.put(channelPattern, new ChannelHandlerInvoker(handler));
            logger.debug("Channel {} is mapped to channel handler {}", channelPattern, handlerClass.getName());
        }
    }

}
