package com.tashin.physicsai.service;

import com.tashin.physicsai.entity.User;

public interface UserService {

    User findOrCreateUser(
            String phoneNumber,
            String displayName);

    User findOrCreateByPhoneNumber(
            String phoneNumber,
            String displayName);

}
