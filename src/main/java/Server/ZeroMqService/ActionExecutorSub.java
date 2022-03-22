package Server.ZeroMqService;

import Server.entiy.SubMessage;
import io.netty.handler.codec.mqtt.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.concurrent.ConcurrentHashMap;
//提交浇水事件类
public class ActionExecutorSub implements Runnable{


    public static ConcurrentHashMap<String, SubMessage> datamap=new ConcurrentHashMap();

//    private static ConcurrentHashMap<Integer,Integer> packid=new ConcurrentHashMap();
//
//    private int packidindex=0;


    @Override
    public void run() {
        ZMQ.Context context=ZMQ.context(1);
        ZMQ.Socket subscriber=context.socket(SocketType.SUB);
        subscriber.connect("tcp://localhost:4869");
        subscriber.subscribe("[Watering]".getBytes());
        while (true){
            String recv=new String(subscriber.recv());
            if (recv.equals("DISCONNECT")){
                break;
            }else {
                SubMessage message=datamap.get(Thread.currentThread().getName());
                //给订阅的队列发送浇水事件
                MqttFixedHeader mqttFixedHeader=new MqttFixedHeader(MqttMessageType.SUBACK,false
                        , MqttQoS.AT_MOST_ONCE,false,0);
//                while (packid.get(packidindex)!=null){
//                    packidindex++;
//                }
                int messageid=message.getMessageid();
                MqttMessageIdVariableHeader messageIdVariableHeader=MqttMessageIdVariableHeader.from(messageid);
                MqttSubAckPayload payload=new MqttSubAckPayload(0x01);
                MqttSubAckMessage ackMessage=new MqttSubAckMessage(mqttFixedHeader,messageIdVariableHeader,payload);
                message.getCtx().writeAndFlush(ackMessage);
            }
        }
        subscriber.close();
        context.term();
    }
}
