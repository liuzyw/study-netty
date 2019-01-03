package study.netty.day6;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created on 2018-12-28
 *
 * <p>webSocket 的支撑</>
 *
 * @author liuzhaoyuan
 */
public class Server6 implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {

        Server6 server2 = new Server6();
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

                        ChannelPipeline pipeline =  channel.pipeline();

                        pipeline.addLast(new HttpServerCodec());

                        pipeline.addLast(new ChunkedWriteHandler());

                        pipeline.addLast(new HttpObjectAggregator(4096));

                        pipeline.addLast(new WebSocketServerProtocolHandler("/wsa"));

                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                                System.out.println("receive : " + msg.text());

                                ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now()));
                            }

                            @Override
                            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("handlerAdded " + ctx.channel().id().asLongText());
                            }

                            @Override
                            public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("handlerRemoved " + ctx.channel().id().asLongText());
                            }
                        });

                    }
                });

        ChannelFuture future = null;
        try {
            future = bootstrap.bind(9999).sync();

            System.out.println("server6 start ....  ");
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
