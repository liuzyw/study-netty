package study.netty.day5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

/**
 * Created on 2018-12-28
 *
 * <p>客户单和服务端的 长连接</>
 *
 * @author liuzhaoyuan
 */
public class Server5 implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {

        Server5 server2 = new Server5();
        server2.start();

    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();


                        pipeline.addLast(new IdleStateHandler(5, 5, 10));

                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new ChannelInboundHandlerAdapter() {


                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                                if (evt instanceof IdleStateEvent) {
                                    IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

                                    // 空闲状态
                                    String eventType = null;
                                    switch (idleStateEvent.state()) {
                                        case READER_IDLE:
                                            eventType = "读空闲";
                                            break;
                                        case WRITER_IDLE:
                                            eventType = "写空闲";
                                            break;
                                        case ALL_IDLE:
                                            eventType = "都空闲";
                                            break;
                                    }

                                    System.out.println(ctx.channel() + " event " + eventType);

                                    ctx.channel().close();
                                }
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

            System.out.println("server5 start ....  ");
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
