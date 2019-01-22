package study;

import org.apache.commons.lang.RandomStringUtils;

import java.io.Serializable;

/**
 * Created on 2019-01-09
 *
 * @author liuzhaoyuan
 */
public class TUs implements Serializable {

    private static final Long serialVersionUID = 1L;


    public static User[] getUsers(int num) {
        User[] users = new User[num];

        for (int i = 0; i < num; i++) {
            User user = new User();
            user.setName(RandomStringUtils.random(6));
            user.setAge(org.apache.commons.lang.math.RandomUtils.nextInt(100));
            user.setAddress(RandomStringUtils.random(30));

            users[i] = user;

        }

        return users;
    }


}
