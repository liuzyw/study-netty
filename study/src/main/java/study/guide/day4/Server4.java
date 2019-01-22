package study.guide.day4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import study.TUs;
import study.User;
import study.msgpack.MsgDecoder;
import study.msgpack.MsgEncoder;

import java.io.Serializable;

/**
 * Created on 2019-01-08
 *
 * @author liuzhaoyuan
 */
public class Server4 implements Serializable {

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

                            pipeline.addLast(new MsgEncoder());

                            pipeline.addLast(new MsgDecoder());

                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("receive4 from client: " + msg);

                                    //ctx.channel().writeAndFlush("大沙地");

                                    User[] users = TUs.getUsers(2);
                                    for (User user : users) {
                                        ctx.write(user);
                                    }

                                    ctx.flush();



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
