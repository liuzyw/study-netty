package study.netty.day3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created on 2018-12-28
 *
 * <p>客户单和服务端的 TCP 通信</>
 *
 * @author liuzhaoyuan
 */
public class Server3 implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {

        Server3 server2 = new Server3();
        server2.start();

    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

                        pipeline.addLast(new LengthFieldPrepender(4));

                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

                                System.out.println(ctx.channel().remoteAddress());

                                System.out.println("receive client msg: " + msg);

                                ctx.writeAndFlush("server response: " + UUID.randomUUID().toString());

                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                ctx.close();
                            }
                        });

                    }
                });

        ChannelFuture future = null;
        try {
            future = bootstrap.bind(9999).sync();

            System.out.println("server3 start ....  ");
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
