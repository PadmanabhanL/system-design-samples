package com.practice.core;

import java.math.BigDecimal;
import java.util.Objects;

public class User {

    private String userId;

    private String username;

    private BigDecimal salary;

    public User(String userId, String username, BigDecimal salary) {
        this.userId = userId;
        this.username = username;
        this.salary = salary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "{" + this.userId + ", " + this.username + ", " + this.salary + "}";
    }

    public int hashCode(){
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.userId);
        hash += 97 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) {
            return false;
        }
        if (!(other instanceof User)) {
            return false;
        }

        boolean equalCondition = this == other;
      //  System.out.println("equalCondition:" + equalCondition);

        //System.out.println("equalsCondition:" + this.userId.equals(((User) other).getUserId()));
        return equalCondition || this.userId.equals(((User) other).getUserId());
    }
}

