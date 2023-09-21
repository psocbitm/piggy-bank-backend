package com.wf.training.piggybank.service;

import com.wf.training.piggybank.model.User;
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

    public boolean authenticate(String username, String password) {
        // Implement logic to check if the provided username and password match your records
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }

    public boolean isUserDataIncomplete(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userData = user.get();
            // Check if any required user details are missing or empty
            return userData.getFullName() == null || userData.getEmail() == null || userData.getPhoneNumber() == null;
        }
        return true; // User not found, considered as incomplete
    }
}

