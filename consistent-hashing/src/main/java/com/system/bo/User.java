package com.system.bo;

import java.util.List;

public record User(String userId, List<UserData> userData) {}
