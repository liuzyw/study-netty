package study.guide.day6;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created on 2019-01-08
 *
 * @author liuzhaoyuan
 */
public class Server6 implements Serializable {

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

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler("/asd"));


                            pipeline.addLast(new SimpleChannelInboundHandler<Object>() {


                                private WebSocketServerHandshaker handshaker;

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if (msg instanceof FullHttpRequest) {
                                        FullHttpRequest request = (FullHttpRequest) msg;
                                        if (request.decoderResult().isFailure() || !"websocket".equals(request.headers().get("Upgrade"))) {
                                            sendHttpResp(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("bad request".getBytes())));
                                        } else {
                                            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://127.0.0.1:9999/wsa", null, false);

                                            handshaker = wsFactory.newHandshaker(request);

                                            if (handshaker == null) {
                                                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                                            } else {
                                                handshaker.handshake(ctx.channel(), request);
                                            }
                                        }

                                    } else if (msg instanceof WebSocketFrame) {
                                        WebSocketFrame frame = (WebSocketFrame) msg;

                                        if (frame instanceof CloseWebSocketFrame) {
                                            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
                                            return;
                                        }

                                        if (frame instanceof PingWebSocketFrame) {
                                            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                                            return;
                                        }

                                        String text = ((TextWebSocketFrame) frame).text();

                                        ctx.channel().write(new TextWebSocketFrame(text + "_" + " receive " + LocalDateTime.now()));


                                    }


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

    private static void sendHttpResp(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        if (response.status().code() != 200) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            byteBuf.release();
        }
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (response.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }
}
