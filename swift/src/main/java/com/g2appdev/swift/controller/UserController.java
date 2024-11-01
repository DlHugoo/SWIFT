package com.g2appdev.swift.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.g2appdev.swift.dto.UserDTO;
import com.g2appdev.swift.dto.LoginRequest;
import com.g2appdev.swift.entity.UserEntity;
import com.g2appdev.swift.service.UserService;
import com.g2appdev.swift.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userv;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/print")
    public String print() {
        return "Hello, User";
    }

    // Create of CRUD
    @PostMapping("/post")
    public UserEntity postUserRecord(@RequestBody UserEntity user) {
        return userv.postUserRecord(user);
    }

    // Read of CRUD
    @GetMapping("/get")
    public List<UserEntity> getAllUsers() {
        return userv.getAllUsers();
    }

    // Update of CRUD
    @PutMapping("/put")
    public UserEntity putUserDetails(@RequestParam int userID, @RequestBody UserEntity newUserDetails) {
        return userv.putUserDetails(userID, newUserDetails);
    }

    // Delete of CRUD
    @DeleteMapping("/delete/{userID}")
    public String deleteUser(@PathVariable int userID) {
        return userv.deleteUser(userID);
    }

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    // Registration Endpoint using UserDTO
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
    	logger.info("Attempting to register user with username: {}", userDTO.getUsername());
        try {
            // Register new user using UserDTO and convert to UserEntity in service layer
            UserEntity registeredUser = userv.registerUser(userDTO);

            // Generate JWT token
            String token = jwtUtils.generateToken(registeredUser.getUsername());

            // Prepare response with token and user ID
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("userId", registeredUser.getUserID());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Login Endpoint using LoginRequest
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate the user based on LoginRequest (username and password)
            UserEntity user = userv.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());

            // Generate the JWT token
            String token = jwtUtils.generateToken(user.getUsername());

            // Prepare response with token and user details
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("userId", user.getUserID());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("progressData", user.getProgressData());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Check if Username Exists Endpoint
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkUsernameExists(@RequestParam String username) {
        boolean exists = userv.existsByUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
