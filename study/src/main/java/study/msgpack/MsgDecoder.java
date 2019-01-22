package study.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 2019-01-09
 *
 * @author liuzhaoyuan
 */
public class MsgDecoder extends MessageToMessageDecoder<ByteBuf> implements Serializable {

    private static final Long serialVersionUID = 1L;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        final byte[] arr;
        final int len = msg.readableBytes();
        arr = new byte[len];

        msg.getBytes(msg.readerIndex(), arr, 0, len);
        MessagePack messagePack = new MessagePack();

        out.add(messagePack.read(arr));

    }
}
