package study.netty.day4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.Serializable;

/**
 * Created on 2018-12-28
 * <p>
 * 多客户端通信
 *
 * @author liuzhaoyuan
 */
public class Server4 implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {

        Server4 server = new Server4();
        server.start();

    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer4());

        ChannelFuture future = null;
        try {
            future = bootstrap.bind(9999).sync();

            System.out.println("server4 start ....  ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
