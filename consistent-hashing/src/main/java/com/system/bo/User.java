package com.system.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class User {
    private final String userId;
    private final List<UserData> userData;
    private final boolean activeFlag;

    public final List<Integer> hashes;

    public User(String userId, List<UserData> userData, boolean activeFlag) {
        this.userId = userId;
        this.userData = userData;
        this.activeFlag = activeFlag;
        this.hashes = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public List<UserData> getUserData() {
        return userData;
    }

    public boolean isActiveFlag() {
        return activeFlag;
    }

    public void addHashes(int hash) {
        this.hashes.add(hash);
    }

    public List<Integer> getHashes() {
        return hashes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return activeFlag == user.activeFlag &&
                Objects.equals(userId, user.userId) &&
                Objects.equals(userData, user.userData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userData, activeFlag);
    }

    @Override
    public String toString() {
        return "User[" +
                "userId='" + userId + '\'' +
                ", userData=" + userData +
                ", activeFlag=" + activeFlag +
                ']';
    }
}