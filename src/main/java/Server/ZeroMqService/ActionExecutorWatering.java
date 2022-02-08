package Server.ZeroMqService;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.concurrent.ConcurrentHashMap;

public class ActionExecutorWatering implements Runnable{

    public static ConcurrentHashMap<String,String> datamap=new ConcurrentHashMap();




    @Override
    public void run() {
        ZMQ.Context context=ZMQ.context(1);
        ZMQ.Socket publisher=context.socket(SocketType.PUB);
        publisher.bind("tcp://*:4869");
        String data=datamap.get(Thread.currentThread().getName());
        if (data!=null){
            publisher.send("[Watering]"+data,ZMQ.NOBLOCK);
        }
        publisher.close();
        context.term();

    }


}
