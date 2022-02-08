import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
class My implements Runnable{

    @Override
    public void run() {
        ZMQ.Context context=ZMQ.context(1);
        ZMQ.Socket subscriber=context.socket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:5555");
        subscriber.subscribe("".getBytes());
//        for (int i=0;i<10;i++){
            String s=new String(subscriber.recv(0));
            System.out.println(Thread.currentThread().getId()+"recv "+s);
//        }
        subscriber.close();
        context.term();
    }
}
public class ZeroOthreTest {
    public static void main(String[] args) {
        My my=new My();
//        My my1=new My();
        new Thread(my).start();
//        new Thread(my1).start();
    }
}
