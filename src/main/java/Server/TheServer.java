package Server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.apache.log4j.Logger;


public class TheServer {
    private final int port=8081;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;
    private Logger logger=Logger.getLogger(TheServer.class);
    public void run() {
        try {

            bossGroup = new NioEventLoopGroup();
            workGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new MqttDecoder())
                    .childHandler(new ChannelInitializer<NioSocketChannel>(){
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MqttDecoder());
                            ch.pipeline().addLast(MqttEncoder.INSTANCE);
                            ch.pipeline().addLast("ServerHandler",new ServerHandler());
                        }
                    } )
                    .option(ChannelOption.TCP_NODELAY,true)
                    .childOption(ChannelOption.SO_BACKLOG,1024);
//                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if (channelFuture.isSuccess()){
                logger.info("server action success");
            }
            channelFuture.channel().closeFuture().sync();
            logger.info("server close soon");
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        TheServer server=new TheServer();
        server.run();
    }
}
