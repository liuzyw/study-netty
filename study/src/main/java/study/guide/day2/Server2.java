package study.guide.day2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created on 2019-01-08
 * <p>
 * DelimiterBasedFrameDecoder 解码器的使用
 *
 * @author liuzhaoyuan
 */
public class Server2 implements Serializable {

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

                            /**
                             * 用这个做分隔符
                             */
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            pipeline.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            //pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //
                            //pipeline.addLast(new LengthFieldPrepender(4));

                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("receive2 from client: " + msg);

                                    String line = msg + "$_" + LocalDateTime.now() + "$_";

                                    ctx.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes()));

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

            System.out.println("server2 start ....");


            future.channel().closeFuture().sync();

        } catch (Exception e) {

        } finally {


            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }

    }

}
