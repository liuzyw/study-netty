package study.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

import java.io.Serializable;

/**
 * Created on 2019-01-09
 *
 * @author liuzhaoyuan
 */
public class MsgEncoder extends MessageToByteEncoder<Object> implements Serializable {

    private static final Long serialVersionUID = 1L;


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        MessagePack messagePack = new MessagePack();
        byte[] bytes = messagePack.write(msg);

        out.writeBytes(bytes);

    }
}
