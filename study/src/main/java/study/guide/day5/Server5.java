package study.guide.day5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

/**
 * Created on 2019-01-08
 *
 * @author liuzhaoyuan
 */
public class Server5 implements Serializable {

    private static final Long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        ChannelFuture future = null;

        try {

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new HttpRequestDecoder());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new HttpResponseEncoder());
                            pipeline.addLast(new ChunkedWriteHandler());

                            pipeline.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {


                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
                                    System.out.println("receive5 from client: " + request);

                                    //ctx.channel().writeAndFlush("大沙地");

                                    System.out.println(request.uri());

                                    System.out.println(request.method());


                                    String content = "<html>\n" +
                                            "<body>\n" +
                                            "<h1>Happy New Millennium!</h1>\n" +
                                            "大厦大厦\n" +
                                            "</body>\n" +
                                            "</html>";

                                    ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);

                                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");

                                    ctx.writeAndFlush(response);


                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.flush();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });


                        }
                    });


            future = bootstrap.bind(9999).sync();

            System.out.println("server1 start ....");


            future.channel().closeFuture().sync();

        } catch (Exception e) {

        } finally {


            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }

    }

}
