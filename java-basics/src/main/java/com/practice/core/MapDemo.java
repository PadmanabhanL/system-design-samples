package com.practice.core;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapDemo {
    public static void main(String[] args) {
        MapDemo mapDemo = new MapDemo();

        User user1 = new User("U001", "Paddy", new BigDecimal(100));
        User user2 = new User("U002", "Balaji", new BigDecimal(200));

        Map<String, User> map = new HashMap<>();
        map.put("U001", user1);
        map.put("U002", user2);

        System.out.println("Initial:"+ map);
        mapDemo.modifyMap(map);

        System.out.println("Modified:"+ map);

        System.out.println("UserObj:"+user2);

        mapDemo.modifyPojoAndCheckMap(user2);
        System.out.println("Modified Map:" + map + " User Obj:" + user2);

        mapDemo.addToSet();

    }

    private void addToSet() {

        Set<User> set = new HashSet<>();
        User u1 = new User("U001", "Paddy", new BigDecimal(100));
        User u2 = new User("U002", "Balaji", new BigDecimal(200));
        set.add(u1);
        set.add(u2);
        u2.setUserId("U001");

        set.add(new User("U001", "SomeOne", BigDecimal.ONE));
        set.add(new User("U002", "SomeoneElse", BigDecimal.ZERO));


        System.out.println("Size Of Set:" + set.size() + " Set:" + set + " Hash for U1:" + u1.hashCode() + " Hash for U2:" + u2.hashCode() + " == comparison:" + (u1 == u2) + " Equals comparison:"+ u1.equals(u2));

    }

    public void modifyMap(Map<String, User> map) {
        map.get("U001").setSalary(new BigDecimal(999));
    }

    public void modifyPojoAndCheckMap(User user) {
        user.setSalary(new BigDecimal(888));
    }

    //Conclusion: Maps store references to objects, not copies of objects.
}
