package com.practice.stream;

import com.practice.core.Mobile;
import com.practice.core.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FlatMapDemo {

    public static void main(String[] args) {
        Map<User, List<Mobile>> userListMap = new HashMap<>();

        Mobile u1Mob1 = new Mobile("123", 1);
        Mobile u1Mob2 = new Mobile("456", 2);

        Mobile u2Mob1 = new Mobile("789", 1);
        Mobile u2Mob2 = new Mobile("012", 2);

        User user1 = new User("U001", "A", new BigDecimal("100"));

        User user2 = new User("U002", "B", new BigDecimal("300"));

        List<Mobile> user1MobList = new ArrayList<>();
        user1MobList.add(u1Mob1);
        user1MobList.add(u1Mob2);

        List<Mobile> user2MobList = new ArrayList<>();
        user2MobList.add(u2Mob1);
        user2MobList.add(u2Mob2);

        List<User> userList = new ArrayList<>();

        userList.add(user1);
        userList.add(user2);
        System.out.println("Printing all mobile phones of type 2");
        userListMap.put(user1, user1MobList);
        userListMap.put(user2, user2MobList);


        //how to get only mobiles of type 2
        userListMap.values().stream()
                .flatMap(List::stream).filter(m -> m.type() == 2).forEach(System.out::println);

    }
}
