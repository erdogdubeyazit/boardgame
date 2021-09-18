package tr.com.beb.boardgame.web.socket;

/**
 * JSON message formatted as :
 * 
 * <pre>
 * {
 *   "channel": String (required)
 *   "action": String (required)
 *   "payload": String (required)
 * }
 * </pre>
 */
public class IncomingMessage {

    /**
     * Channel for the message. The request will be routed by
     * {@link WebSocketRequestDispatcher} to the corresponding
     * {@link ChannelHandler}.
     */
    private String channel;

    /**
     * Action methos which is annotated with {@link Action} will be discovered and
     * run by {@link WebSocketRequestDispatcher}
     */
    private String action;

    /**
     * Message input
     */
    private String payload;

    public IncomingMessage() {
    }

    public IncomingMessage(String channel, String action, String payload) {
        this.channel = channel;
        this.action = action;
        this.payload = payload;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
