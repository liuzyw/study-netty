package study.netty.day4;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.Serializable;

/**
 * Created on 2019-01-02
 *
 * @author liuzhaoyuan
 */
public class SimpleChannelInboundHandler4 extends SimpleChannelInboundHandler<String> implements Serializable {

    private static final Long serialVersionUID = 1L;

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();

        channels.forEach(ch -> {
            if (ch != channel) {
                ch.writeAndFlush(channel.remoteAddress() + " say: " + msg + "\n");
            } else {
                ch.writeAndFlush("I say: " + msg + "\n");
            }
        });


    }

    /**
     * 连接建立
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        // 广播到其他 channel
        channels.writeAndFlush("【客户端】- " + channel.remoteAddress() + " 加入\n");

        channels.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();

        //channels.remove(channel);

        channels.writeAndFlush("【客户端】- " + channel.remoteAddress() + " 离开\n");

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "  上线 .");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "  下线 .");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }


}
