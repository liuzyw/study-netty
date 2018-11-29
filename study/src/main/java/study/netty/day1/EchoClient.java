package study.netty.day1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * Created on 2018-11-29
 *
 * @Author lzy
 */
@Data
public class EchoClient {

    private String host;

    private int port;

    public EchoClient (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main (String[] args) throws Exception {
        new EchoClient("127.0.0.1", 9999).start();
    }

    public void start () throws Exception {


        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel (SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully().sync();
        }

    }
}

class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive (ChannelHandlerContext ctx) throws Exception {

        // 当被通知 Channel 是活跃的时候，发 送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("client ->  hello Tom", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0 (ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {

        // 接受来自服务端的信息
        System.out.println("Client received: " + byteBuf.toString(CharsetUtil.UTF_8));
    }
}

