package com.wf.training.piggybank.controller;

import com.wf.training.piggybank.exception.UserNotFoundException;
import com.wf.training.piggybank.model.User;
import com.wf.training.piggybank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            User existingUser = user.get();

            if (updatedUser.getUsername() != null) {
                existingUser.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getPassword() != null) {
                existingUser.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getFullName() != null) {
                existingUser.setFullName(updatedUser.getFullName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }

            User updatedUserEntity = userService.updateUser(existingUser);
            return ResponseEntity.ok(updatedUserEntity);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        User user = userService.authenticate(loginUser.getUsername(), loginUser.getPassword());
        if (user != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(user);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @PostMapping("/lockUser/{userId}")
    public ResponseEntity<?> lockUser(@RequestBody User user, @PathVariable Long userId) {
      Optional<User> admin = userService.getUserById(user.getId());
        if (admin.isPresent() && admin.get().getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.lockUser(userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @PostMapping("/unlockUser/{userId}")
    public ResponseEntity<?> unlockUser(@RequestBody User user, @PathVariable Long userId) {
        Optional<User> admin = userService.getUserById(user.getId());
        if (admin.isPresent() && admin.get().getRole().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.unlockUser(userId));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
}
