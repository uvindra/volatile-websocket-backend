package org.wso2.carbon.apimgt.gateway.mediators;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.transport.passthru.Pipe;
import org.wso2.carbon.apimgt.gateway.handlers.WebsocketHandler;

import java.util.Map;

/**
 * This mediator will be used to callback the Websocket clients by the Websocket backend
 */
public class WebsocketClientCallbackMediator extends AbstractMediator {
    @Override
    public boolean mediate(MessageContext messageContext) {
        org.apache.axis2.context.MessageContext axis2MsgContext = ((Axis2MessageContext) messageContext).
                getAxis2MessageContext();

        Map headers = (Map) axis2MsgContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        if (headers.containsKey("X-Clientid")) {
            Long clientId = Long.parseLong(((String) headers.get("X-Clientid")));

            if (WebsocketHandler.clientMap.containsKey(clientId)) {
                Channel channel = (Channel) WebsocketHandler.clientMap.get(clientId);

                Pipe pipe = (Pipe) axis2MsgContext.getProperty("pass-through.pipe");

                String payload = getBufferValue(pipe);

                if (!StringUtils.isEmpty(payload)) {
                    TextWebSocketFrame event = new TextWebSocketFrame(payload);
                    channel.writeAndFlush(safeDuplicate(event));
                }
            }
        }


        return true;
    }

    private Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf) message).duplicate().retain();
        } else if (message instanceof ByteBufHolder) {
            return ((ByteBufHolder) message).duplicate().retain();
        } else {
            return ReferenceCountUtil.retain(message);
        }
    }

    public String getBufferValue(final Pipe pipe) {
        byte[] byteArr = pipe.getBuffer().getByteBuffer().array();

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < byteArr.length; ++i) {
            char c = (char) Integer.valueOf(byteArr[i]).intValue();

            buffer.append(c);
        }

        return buffer.toString();
    }
}
