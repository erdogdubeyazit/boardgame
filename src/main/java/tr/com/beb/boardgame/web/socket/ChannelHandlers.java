package tr.com.beb.boardgame.web.socket;

public final class ChannelHandlers {

    public static String getPattern(ChannelHandler channelHandler) {
        if (channelHandler.pattern().isEmpty() == false)
            return channelHandler.pattern();
        return channelHandler.value();
    }

}
