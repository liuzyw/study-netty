package study.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019-01-09
 *
 * @author liuzhaoyuan
 */
public class Demo1 implements Serializable {

    private static final Long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<>();

        list.add("I");
        list.add("hello");
        list.add("Tom");

        MessagePack messagePack = new MessagePack();
        byte[] bytes = messagePack.write(list);

        List<String> read = messagePack.read(bytes, Templates.tList(Templates.TString));

        System.out.println(read);

    }

}
