package study.netty.day4;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

/**
 * Created on 2019-01-02
 *
 * @author liuzhaoyuan
 */
public class ChannelInitializer4 extends ChannelInitializer<SocketChannel> implements Serializable {

    private static final Long serialVersionUID = 1L;


    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));

        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

        // 自定义
        pipeline.addLast(new SimpleChannelInboundHandler4());
    }
}
