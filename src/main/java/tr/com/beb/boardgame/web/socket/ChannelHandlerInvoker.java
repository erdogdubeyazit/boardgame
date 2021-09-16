package tr.com.beb.boardgame.web.socket;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import tr.com.beb.boardgame.utils.JsonUtils;

public class ChannelHandlerInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ChannelHandlerInvoker.class);
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private String channelPattern;
    private Object handler;

    // <Key,Method> Pair. Method is resposible for handling the action.
    private final Map<String, Method> actionMethods = new HashMap<>();

    public ChannelHandlerInvoker(Object handler) {
        Assert.notNull(handler, "Parameter `handler` must not be null");

        Class<?> handlerClass = handler.getClass();
        ChannelHandler handlerAnnotation = handlerClass.getAnnotation(ChannelHandler.class);
        Assert.notNull(handlerAnnotation, "Parameter `handler` must have annotation @ChannelHandler");

        Method[] methods = handlerClass.getMethods();
        for (Method method : methods) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation == null)
                continue;

            String action = actionAnnotation.value();
            actionMethods.put(action, method);
            logger.debug("Mapped action `{}` in channel handler `{}#{}`", action, handlerClass.getName(), method);
        }

        this.channelPattern = ChannelHandlers.getPattern(handlerAnnotation);
        this.handler = handler;

    }

    public boolean supports(String action) {
        return actionMethods.containsKey(action);
    }

    public void handle(IncomingMessage incomingMessage, CustomWebSocketSession webSocketSession) {
        Assert.isTrue(antPathMatcher.match(channelPattern, incomingMessage.getChannel()),
                "Channel and the handler do not correspond");
        Method method = actionMethods.get(incomingMessage.getAction());
        Assert.notNull(method, "Method for `" + incomingMessage.getAction() + "` can not be null");

        // Get method parameters
        Class<?>[] parameterTypes = method.getParameterTypes();
        // Get parameter annotations
        Annotation[][] allParameterAnnotations = method.getParameterAnnotations();
        // Argument array for the method
        Object[] arguments = new Object[parameterTypes.length];

        try {
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                Annotation[] parameterAnnotations = allParameterAnnotations[i];

                // There is no annotations
                if (parameterAnnotations.length == 0) {
                    if (parameterType.isInstance(webSocketSession))
                        arguments[i] = webSocketSession;
                    else
                        arguments[i] = null;
                    continue;
                }

                // One or many annotations passed
                // We will consider only the first annotation
                Annotation parameterAnnotation = parameterAnnotations[0];
                if (parameterAnnotation instanceof WebSocketPayload) {
                    Object argument = JsonUtils.toObject(incomingMessage.getPayload(), parameterType);
                    if (argument == null)
                        throw new IllegalArgumentException(
                                "Parameter cannot be derived for '" + parameterType.getName() + "'");
                    arguments[i] = argument;
                } else if (parameterAnnotation instanceof ChannelValue)
                    arguments[i] = incomingMessage.getChannel();
            }
            method.invoke(handler, arguments);
        } catch (Exception e) {
            String errorDetails = "Invoker action method` " + incomingMessage.getAction() + "` failed at channel `"
                    + incomingMessage.getChannel() + "` ";
            logger.error(errorDetails, e);
            webSocketSession.error(errorDetails);
        }

    }

}
