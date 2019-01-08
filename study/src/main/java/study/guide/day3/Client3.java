package study.guide.day3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

/**
 * Created on 2019-01-08
 *
 * @author liuzhaoyuan
 */
public class Client3 implements Serializable {

    private static final Long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {

        EventLoopGroup loopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        ChannelFuture future = null;


        try {

            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new FixedLengthFrameDecoder(10));


                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("receive3 from server: " + msg);

                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("first hello $_ aaaaaaa", CharsetUtil.UTF_8));
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });

                        }
                    });

            future = bootstrap.connect("127.0.0.1", 9999).sync();

            System.out.println("client2 start ....");

            future.channel().closeFuture().sync();


        } catch (Exception e) {
        } finally {

            loopGroup.shutdownGracefully();
        }

    }

}
