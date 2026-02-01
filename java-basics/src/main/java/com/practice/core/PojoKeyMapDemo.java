package com.practice.core;

import java.math.BigDecimal;
import java.util.*;

public class PojoKeyMapDemo {

    public static void main(String[] args) {
        Map<User, List<Mobile>> userListMap = new HashMap<>();

        Mobile u1Mob1 = new Mobile("123", 1);
        Mobile u1Mob2 = new Mobile("456", 2);

        Mobile u2Mob1 = new Mobile("789", 1);
        Mobile u2Mob2 = new Mobile("012", 2);

        User user1 = new User("U001", "A", new BigDecimal("100"));

        User user2 = new User("U001", "B", new BigDecimal("300"));

        List<Mobile> user1MobList = new ArrayList<>();
        user1MobList.add(u1Mob1);
        user1MobList.add(u1Mob2);

        List<Mobile> user2MobList = new ArrayList<>();
        user2MobList.add(u2Mob1);
        user2MobList.add(u2Mob2);

        Set<User> userSet = new HashSet<>();
        userSet.add(user1);
        userSet.add(user2);

        System.out.println("Set Size: " + userSet.size());

        userListMap.put(user1, user1MobList);
        userListMap.put(user2, user2MobList);
        userListMap.get(user2).add(new Mobile("222", 2));
        user2MobList.add(new Mobile("111", 2));
       // user2.setUserId("U002");

        User user3 = new User("U001", "C", new BigDecimal("500"));

        System.out.println("Is User1 and User3 equal?" + user1.equals(user3));

        System.out.println("Now trying to retrieve using user3:"+ userListMap.get(user3));

        //memory leak - one more entry added to the map breaking map semantics of avoiding duplicate key
        userListMap.put(user3, user1MobList);

        System.out.println("HashMap Size:" +  userListMap.size());


        System.out.println("Set size:" +  userSet.size());
        System.out.println("User1:"+ userListMap.get(user1));
        System.out.println("User2:"+userListMap.get(user2));
        System.out.println("User3:"+userListMap.get(user3));

        System.out.println("Set Entries");
        for (User user: userSet) {
            System.out.println(user);
        }


        System.out.println("Map Entries");
        for (Map.Entry<User, List<Mobile>> entry:  userListMap.entrySet()) {
            System.out.println("Key:"+entry.getKey() + " Value:"+entry.getValue());
        }
    }
}

