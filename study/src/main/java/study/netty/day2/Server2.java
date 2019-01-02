package study.netty.day2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

/**
 * Created on 2018-12-28
 *
 * @author liuzhaoyuan
 */
public class Server2 implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {

        Server2 server2 = new Server2();
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

                        pipeline.addLast("httpServerCodec", new HttpServerCodec());

                        pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
                                // 读取客户端发过来的请求， 并向客户端返回响应

                                System.out.println(channelHandlerContext.channel().remoteAddress());

                                if (httpObject instanceof HttpRequest) {

                                    HttpRequest request = (HttpRequest) httpObject;

                                    String uri = request.uri();

                                    System.out.println("-----> " + uri);

                                    String name = request.method().name();

                                    System.out.println("-----> " + name);


                                    System.out.println("----> response ");

                                    ByteBuf content = Unpooled.copiedBuffer("Hello Word " + uri, CharsetUtil.UTF_8);
                                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                                    channelHandlerContext.writeAndFlush(response);
                                }
                            }


                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("----->  Registered");

                                super.channelRegistered(ctx);
                            }

                            @Override
                            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("----->  Unregistered");

                                super.channelUnregistered(ctx);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("----->  Active");

                                super.channelActive(ctx);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("----->  Inactive");

                                super.channelInactive(ctx);
                            }

                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("----->  ReadComplete");

                                super.channelReadComplete(ctx);
                            }
                        });

                    }
                });

        ChannelFuture future = null;
        try {
            future = bootstrap.bind(9999).sync();

            System.out.println("server start ....  ");
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
