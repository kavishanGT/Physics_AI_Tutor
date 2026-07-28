package com.tashin.physicsai.service;

import com.tashin.physicsai.entity.User;
import com.tashin.physicsai.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl
        implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public User findOrCreateUser(
            String phone,
            String name) {

        return repository.findByPhoneNumber(phone)

                .orElseGet(() -> {

                    User user = new User();

                    user.setPhoneNumber(phone);
                    user.setDisplayName(name);

                    return repository.save(user);

                });

    }

    /**
     * Alias used by WhatsAppMessageProcessor.
     * Delegates to {@link #findOrCreateUser(String, String)}.
     */
    @Override
    @Transactional
    public User findOrCreateByPhoneNumber(
            String phoneNumber,
            String displayName) {

        return findOrCreateUser(phoneNumber, displayName);

    }

}
