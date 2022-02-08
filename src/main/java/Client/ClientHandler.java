package Client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
public class ClientHandler extends SimpleChannelInboundHandler<MqttMessage> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MqttConnectVariableHeader mcvh=new MqttConnectVariableHeader(
                "MQTT",0x04,false,false,false,0,false,false,10
        );
        MqttFixedHeader mqttFixedHeader=new MqttFixedHeader(MqttMessageType.CONNECT,false
                , MqttQoS.AT_MOST_ONCE,false,0);
        MqttConnectPayload mqttConnectPayload=new MqttConnectPayload("MQTT","hello","hello sherry".getBytes(),null,null);
        MqttMessage message=MqttMessageFactory.newMessage(mqttFixedHeader,mcvh,mqttConnectPayload);


        ctx.writeAndFlush(message);
//        ctx.writeAndFlush(Unpooled.wrappedBuffer(message.toString().getBytes()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead \n");
        MqttMessage message=(MqttMessage)msg;
        System.out.println(message.fixedHeader().messageType());

//        MqttConnAckVariableHeader
//                connAckVariableHeader=(MqttConnAckVariableHeader)(message.variableHeader());
//
//
//        MqttFixedHeader fixedHeader=new MqttFixedHeader(MqttMessageType.PUBLISH,
//                false,MqttQoS.AT_LEAST_ONCE,false,0);
//        MqttPublishVariableHeader publishVariableHeader=new
//                MqttPublishVariableHeader("watering",)
//
//
//        MqttPublishMessage publishMessage=new MqttPublishMessage();



    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        System.out.println("00000000000\n");
    }
}
