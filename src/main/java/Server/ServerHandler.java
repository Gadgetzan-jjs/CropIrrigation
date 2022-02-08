package Server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.apache.log4j.Logger;
import sun.plugin2.message.Message;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;

public class ServerHandler extends SimpleChannelInboundHandler {

    private ServerMethods methods=new ServerMethods();

    private Logger logger=Logger.getLogger(TheServer.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server handler is active\n");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        MqttMessage message = (MqttMessage) msg;
        switch (message.fixedHeader().messageType()){
            case CONNECT:
                methods.doConnect(ctx.channel(),message);
                break;
            case PUBLISH:
                methods.doPublish(ctx.channel(),message);
                break;
            case DISCONNECT:
                methods.doDisconnect(ctx.channel(),message);
                break;
            case SUBSCRIBE:
                methods.doSubscribe(ctx.channel(), message);
                break;
            case PINGREQ:
                methods.doPingreq(ctx.channel(),message);
                break;
            default:
        }

    }


}
