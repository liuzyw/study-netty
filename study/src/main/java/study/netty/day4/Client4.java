package study.netty.day4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import study.netty.Constant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created on 2018-12-28
 *
 * @author liuzhaoyuan
 */
public class Client4 implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {

        Client4 client = new Client4();
        client.start();

    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(bossGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));

                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));


                        pipeline.addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("receive: " + msg);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                ctx.close();
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                // 当被通知 Channel 是活跃的时候，发 送一条消息
                                //ctx.writeAndFlush(Unpooled.copiedBuffer("client ->  hello Tom", CharsetUtil.UTF_8));
                            }
                        });
                    }
                });


        ChannelFuture future = null;
        try {
            future = bootstrap.connect(Constant.HOST, 9999).sync();


            Channel channel = future.channel();

            System.out.println("clinet4 start ....  " + channel.remoteAddress());


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            for (; ; ) {
                channel.writeAndFlush(bufferedReader.readLine() + "\n");
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bossGroup.shutdownGracefully();
        }

    }

}
