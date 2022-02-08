package Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

public class Client implements Runnable{
    private EventLoopGroup clientworker;
    private int port;
    private String host="127.0.0.1";
    public Client(int port){
        this.port=port;
    }
    private static Channel channel;
    public Channel getChannel(){
        return channel;
    }
    @Override
    public void run() {
        try {

            clientworker = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientworker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MqttDecoder());
                            ch.pipeline().addLast(MqttEncoder.INSTANCE);
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    }).option(ChannelOption.TCP_NODELAY,true);
//                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            if (channelFuture.isSuccess()){
//                logger.info("server action success");
                System.out.println("client action success\n");
            }
            channelFuture.channel().closeFuture().sync();
            this.channel=channelFuture.channel();
            System.out.println("client 即将关闭");
//            logger.info("server close soon");
        }catch (Exception e){
//            logger.error(e.getMessage());
        }
        finally {
            clientworker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Client client=new Client(8081);
        Thread thread=new Thread(client);
        thread.start();
//        Channel channel=client.getChannel();
//        System.out.println("hello hello\n");
//        channel.writeAndFlush("hello sherry");
    }
}
