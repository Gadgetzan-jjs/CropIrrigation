import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Date;

public class ZeroTest {
    public static void main(String[] args) throws InterruptedException {
        ZMQ.Context context=ZMQ.context(1);
        ZMQ.Socket publisher=context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:5555");
        Thread.sleep(1000);
        for (int i=0;i<100;i++){
            publisher.send(("admin "+i).getBytes(),ZMQ.NOBLOCK);
            System.out.println("pub msg "+i);
            Thread.sleep(1000);
        }
        publisher.close();
        context.term();
    }
}
