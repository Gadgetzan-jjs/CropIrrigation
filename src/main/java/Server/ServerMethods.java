package Server;

import Server.LogSystem.AheadLog;
//import Server.ZeroMqService.ActionExecutor;
import Server.Tools.OpsConcurrentHashMap;
import Server.ZeroMqService.ActionExecutorError;
import Server.ZeroMqService.ActionExecutorLog;
import Server.ZeroMqService.ActionExecutorSub;
import Server.ZeroMqService.ActionExecutorWatering;
import Server.entiy.SubMessage;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMethods {
    private static AtomicInteger atomicInteger=new AtomicInteger(0);
    public static ServerMethods methods;

    private ExecutorService executor= Executors.newCachedThreadPool();

    private org.apache.log4j.Logger logger= Logger.getLogger(ServerMethods.class);



    public void doConnect(Channel channel, MqttMessage message){


        MqttConnectMessage connectMessage=(MqttConnectMessage)message;

        MqttFixedHeader mqttFixedHeader=connectMessage.fixedHeader();

        MqttConnectVariableHeader mqttConnectVariableHeader
                =connectMessage.variableHeader();



        //反馈报文的可变头
        MqttConnAckVariableHeader
                mqttConnAckVariableHeader=new MqttConnAckVariableHeader(
                        MqttConnectReturnCode.CONNECTION_ACCEPTED,
                mqttConnectVariableHeader.isCleanSession()
        );
        MqttFixedHeader requestmqttfixedheader=new MqttFixedHeader(
                MqttMessageType.CONNACK,mqttFixedHeader.isDup(),
                MqttQoS.AT_MOST_ONCE,mqttFixedHeader.isRetain(),
                0x02
        );
        MqttConnAckMessage
                connAckMessage=new MqttConnAckMessage(requestmqttfixedheader,
                mqttConnAckVariableHeader);

        channel.writeAndFlush(connAckMessage);
    }

    public void doPublish(Channel channel,MqttMessage message){
        MqttPublishMessage publishMessage=(MqttPublishMessage)message;

        MqttFixedHeader mqttFixedHeader=publishMessage.fixedHeader();

        MqttPublishVariableHeader publishVariableHeader=
                publishMessage.variableHeader();

        byte[] getbytes=new byte[publishMessage.payload().readableBytes()];

        publishMessage.payload().readBytes(getbytes);

        String data=new String(getbytes);


          while (!AheadLog.actionHeadLog("dopublish")){//TODO：需要调时序数据库
              logger.info("aheadLog insert failed\n");
          }
          logger.info("aheadLog insert success\n");

        switch (publishVariableHeader.topicName()){
            case "watering":
                Thread watering=new Thread(new ActionExecutorWatering());
                watering.setName(watering.getId()+"");
                OpsConcurrentHashMap.putvalue(ActionExecutorWatering.datamap,watering.getName(),data);
//                ActionExecutorWatering.datamap.put(watering.getName(),data);
                executor.submit(watering);
                break;
            case "log":
                Thread log=new Thread(new ActionExecutorLog());
                log.setName(log.getId()+"");
                OpsConcurrentHashMap.putvalue(ActionExecutorLog.datamap,log.getName(),data);
//                ActionExecutorLog.datamap.put(log.getName(),data);
                executor.submit(log);
                break;
            case "error":
                Thread error=new Thread(new ActionExecutorError());
                error.setName(error.getId()+"");
                OpsConcurrentHashMap.putvalue(ActionExecutorError.datamap,error.getName(),data);
//                ActionExecutorError.datamap.put(error.getName(),data);
                executor.submit(error);
                break;
        }
        MqttQoS qos=(MqttQoS)mqttFixedHeader.qosLevel();

        MqttMessageIdVariableHeader messageIdVariableHeader
                =MqttMessageIdVariableHeader.from(publishVariableHeader.packetId());
        switch (qos){
            case AT_MOST_ONCE:
                break;
            case AT_LEAST_ONCE:

                MqttFixedHeader requestfixedheaderALO=new MqttFixedHeader(
                        MqttMessageType.PUBACK,mqttFixedHeader.isDup(),
                        MqttQoS.AT_MOST_ONCE,mqttFixedHeader.isRetain(),
                        0x02
                );
                MqttPubAckMessage requestpubackmessage=new MqttPubAckMessage(
                        requestfixedheaderALO,messageIdVariableHeader
                );
                logger.info("back to client:"+requestpubackmessage.toString());
                channel.writeAndFlush(requestpubackmessage);
                break;
            case EXACTLY_ONCE:
                MqttFixedHeader requestfixedheaderEO=new MqttFixedHeader(
                        MqttMessageType.PUBREC,false,
                        MqttQoS.AT_MOST_ONCE,false,
                        0x02
                );
                MqttMessage requestpubrecmessage=new MqttMessage(requestfixedheaderEO,messageIdVariableHeader);
                logger.info("back to client:"+requestpubrecmessage.toString());
                break;
            default:
                break;
        }
    }
    public MqttMessage doDisconnect(Channel channel,MqttMessage message){

        return null;
    }
    public void doSubscribe(Channel channel,MqttMessage message){
        MqttSubscribeMessage mqttSubscribeMessage=(MqttSubscribeMessage)message;
//        MqttSubscribePayload mqttSubscribePayload=mqttSubscribeMessage.payload();

        //订阅消息队列
        InetSocketAddress inetSocketAddress= (InetSocketAddress) channel.remoteAddress();
        Thread thread=new Thread(new ActionExecutorSub());
        SubMessage subMessage=new SubMessage();
        subMessage.setIpaddress(inetSocketAddress.getAddress().toString());
        subMessage.setPort(String.valueOf(inetSocketAddress.getPort()));
        subMessage.setCtx(channel);
        subMessage.setMessageid(mqttSubscribeMessage.variableHeader().messageId());
        thread.setName(thread.getId()+"");
        ActionExecutorSub.datamap.put(thread.getName(),subMessage);
        executor.submit(thread);
    }
    public void doPingreq(Channel channel,MqttMessage message){


        MqttFixedHeader fixedHeader=new MqttFixedHeader(
                MqttMessageType.PINGRESP,false,
                MqttQoS.AT_MOST_ONCE,false,
                0x02
        );
         MqttMessage response=new MqttMessage(fixedHeader);

        channel.writeAndFlush(response);


    }

}
