package study.guide.day8;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created on 2019-01-11
 *
 * @author liuzhaoyuan
 */
public class D implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(123);

        buffer.put((byte)2);
    }

}
