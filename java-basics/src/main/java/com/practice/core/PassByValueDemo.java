package com.practice.core;

import java.math.BigDecimal;
import java.util.*;

public class PassByValueDemo {

    public static void main(String[] args) {

        PassByValueDemo demo = new PassByValueDemo();

        System.out.println("===== 1. PRIMITIVE TYPE =====");
        int x = 10;
        System.out.println("Before: " + x);
        demo.modifyPrimitive(x);
        System.out.println("After : " + x);

        System.out.println("\n===== 2. WRAPPER CLASS =====");
        Integer y = 20;
        System.out.println("Before: " + y);
        demo.modifyWrapper(y);
        System.out.println("After : " + y);

        System.out.println("\n===== 3. STRING (IMMUTABLE) =====");
        String s = "Hello";
        System.out.println("Before: " + s);
        demo.modifyString(s);
        System.out.println("After : " + s);

        System.out.println("\n===== 4. IMMUTABLE OBJECT (BigDecimal) =====");
        BigDecimal amount = new BigDecimal("100");
        System.out.println("Before: " + amount);
        demo.modifyBigDecimal(amount);
        System.out.println("After : " + amount);

        System.out.println("\n===== 5. OBJECT MUTATION =====");
        User user = new User("U1", "Paddy", BigDecimal.valueOf(1000));
        System.out.println("Before: " + user);
        demo.mutateObject(user);
        System.out.println("After : " + user);

        System.out.println("\n===== 6. OBJECT REASSIGNMENT =====");
        System.out.println("Before: " + user);
        demo.reassignObject(user);
        System.out.println("After : " + user);

        System.out.println("\n===== 7. ARRAY =====");
        int[] arr = {1, 2, 3};
        System.out.println("Before: " + arr[0]);
        demo.modifyArray(arr);
        System.out.println("After : " + arr[0]);

        System.out.println("\n===== 8. COLLECTION (LIST) =====");
        List<User> users = new ArrayList<>();
        users.add(user);
        System.out.println("Before size: " + users.size());
        demo.modifyList(users);
        System.out.println("After size : " + users.size());

        System.out.println("\n===== 9. COLLECTION REASSIGNMENT =====");
        System.out.println("Initial size: " + users.size() + " Users:"+users);
        demo.reassignList(users);
        System.out.println("After reassignment attempt size: " + users.size() + " Users:"+users );

        System.out.println("\n===== 10. SET =====");
        Set<String> set = new HashSet<>();
        demo.modifySet(set);
        System.out.println("Set contents: " + set);

        System.out.println("\n===== 11. MAP =====");
        Map<String, Integer> map = new HashMap<>();
        demo.modifyMap(map);
        System.out.println("Map contents: " + map);

        System.out.println("\n===== 12. FINAL PARAMETER =====");
        demo.modifyFinalList(users);
        System.out.println("Final list size: " + users.size());

        System.out.println("\n===== 13. RETURN PATTERN (BEST PRACTICE) =====");
        user = demo.returnNewUser(user);
        System.out.println("Updated user: " + user);
    }

    /* ===== METHODS ===== */

    // 1. Primitive
    void modifyPrimitive(int x) {
        x = 50;
    }

    // 2. Wrapper (Immutable)
    void modifyWrapper(Integer x) {
        x = 100;
    }

    // 3. String (Immutable)
    void modifyString(String s) {
        s = s + " World";
    }

    // 4. BigDecimal (Immutable)
    void modifyBigDecimal(BigDecimal bd) {
        bd = bd.add(BigDecimal.TEN);
    }

    // 5. Object Mutation
    void mutateObject(User user) {
        user.setSalary(BigDecimal.valueOf(2000));
    }

    // 6. Object Reassignment
    void reassignObject(User user) {
        user = new User("U2", "NewUser", BigDecimal.valueOf(5000));
    }

    // 7. Array
    void modifyArray(int[] arr) {
        arr[0] = 999;
    }

    // 8. List mutation
    void modifyList(List<User> users) {
        users.add(new User("U3", "Added", BigDecimal.valueOf(3000)));
    }

    // 9. List reassignment
    void reassignList(List<User> users) {
        users = new ArrayList<>();
        users.add(new User("U4", "Temp", BigDecimal.ONE));
    }

    // 10. Set
    void modifySet(Set<String> set) {
        set.add("A");
    }

    // 11. Map
    void modifyMap(Map<String, Integer> map) {
        map.put("Key", 100);
    }

    // 12. Final parameter
    void modifyFinalList(final List<User> users) {
        users.add(new User("U5", "FinalUser", BigDecimal.TEN));
        // users = new ArrayList<>(); // ❌ Compile-time error
    }

    // 13. Return pattern (Correct way)
    User returnNewUser(User user) {
        return new User(user.getUserId(), user.getUsername(), BigDecimal.valueOf(9999));
    }
}
