package study.netty.day7;

import java.io.Serializable;
import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * Created on 2019-01-06
 *
 * @author liuzhaoyuan
 */
public class NIOT implements Serializable {

    private static final Long serialVersionUID = 1L;

    /**
     * --
     * --     1,  2,  3, 4,  5,  6,  7,  8,  9,  10
     * --
     *
     * @param args
     */

    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(10);

        write(buffer, 5);

        print(buffer);

        buffer.flip();

        print(buffer);

        read(buffer);

        print(buffer);

        buffer.flip();

        print(buffer);

        buffer.clear();

        print(buffer);

        write(buffer, 7);
        print(buffer);

        



    }

    private static void write(IntBuffer buffer, int num) {
        for (int i = 1; i < num; i++) {
            buffer.put(i);
        }
    }

    private static void print(Buffer buffer) {
        System.out.println("position :" + buffer.position());
        System.out.println("limit :" + buffer.limit());

    }

    private static void read(IntBuffer buffer) {
        while (buffer.hasRemaining()) {
            System.out.print(buffer.get() + ", ");
        }
        System.out.println();

    }

}
