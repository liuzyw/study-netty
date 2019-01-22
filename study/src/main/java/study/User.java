package study;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2019-01-09
 *
 * @author liuzhaoyuan
 */
@Data
public class User implements Serializable {

    private static final Long serialVersionUID = 1L;

    private String name;

    private Integer age;

    private String address;


    @Override
    public String toString() {
        return "User{" +
                "name='" + name +
                ", age=" + age +
                ", address='" + address +
                '}';
    }
}
