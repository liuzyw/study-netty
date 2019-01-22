package study.guide.day4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.CharsetUtil;
import study.TUs;
import study.User;
import study.msgpack.MsgDecoder;
import study.msgpack.MsgEncoder;

import java.io.Serializable;
import java.util.Scanner;

/**
 * Created on 2019-01-08
 * <p>
 * <>messagePack 的使用</>
 *
 * @author liuzhaoyuan
 */
public class Client4 implements Serializable {

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
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

                            pipeline.addLast(new LengthFieldPrepender(4));


                            pipeline.addLast(new MsgEncoder());

                            pipeline.addLast(new MsgDecoder());


                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("receive4 from server: " + msg);

                                    Scanner scanner = new Scanner(System.in);

                                    String line = scanner.nextLine();

                                    scanner.close();

                                    ctx.writeAndFlush(Unpooled.copiedBuffer(line, CharsetUtil.UTF_8));


                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                    User[] users = TUs.getUsers(5);
                                    for (User user : users) {
                                        ctx.write(user);
                                    }

                                    ctx.flush();

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });

                        }
                    });

            future = bootstrap.connect("127.0.0.1", 9999).sync();

            System.out.println("client4 start ....");

            future.channel().closeFuture().sync();


        } catch (Exception e) {
        } finally {

            loopGroup.shutdownGracefully();
        }

    }

}
