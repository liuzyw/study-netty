package study.guide.day1;

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
import java.util.Scanner;

/**
 * Created on 2019-01-08
 *
 * @author liuzhaoyuan
 */
public class Server1 implements Serializable {

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

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

                            pipeline.addLast(new LengthFieldPrepender(4));

                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("receive1 from client: " + msg);

                                    Scanner scanner = new Scanner(System.in);

                                    String line = scanner.nextLine();

                                    scanner.close();

                                    ctx.channel().writeAndFlush(line);

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
