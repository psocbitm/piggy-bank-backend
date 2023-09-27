package com.wf.training.piggybank.service;

import com.wf.training.piggybank.model.User;
import com.wf.training.piggybank.model.UserStatus;
import com.wf.training.piggybank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        } else {
            return null;
        }
    }


    public boolean isUserDataIncomplete(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userData = user.get();
            return userData.getFullName() == null || userData.getEmail() == null || userData.getPhoneNumber() == null;
        }
        return true; // User not found, considered as incomplete
    }

    public User lockUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userData = user.get();
            userData.setUserStatus(UserStatus.LOCKED);
            return userRepository.save(userData);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public User unlockUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userData = user.get();
            userData.setUserStatus(UserStatus.ACTIVE);
            return userRepository.save(userData);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }


}

