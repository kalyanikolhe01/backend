package com.dhundhoo.acendMarketing.controller;

import com.dhundhoo.acendMarketing.dto.LoginRequest;
import com.dhundhoo.acendMarketing.dto.SessionRequestDTO;
import com.dhundhoo.acendMarketing.dto.UserResponseDTO;
import com.dhundhoo.acendMarketing.exception.DuplicateFieldException;
import com.dhundhoo.acendMarketing.exception.InvalidCredentialsException;
import com.dhundhoo.acendMarketing.model.User;
import com.dhundhoo.acendMarketing.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    public UserService userService;

    // Endpoint for registering a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully...!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateFieldException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }



    //login api
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            String token = userService.loginUser(loginRequest.getIdentifier(), loginRequest.getPassword());
            response.put("message", "Login successful.");
            response.put("accessToken", token);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred during login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    //auth api
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticateUser(@Valid @RequestBody SessionRequestDTO sessionRequest) {
        Map<String, String> response = new HashMap<>();
        if (sessionRequest.getAccessCode() == null || sessionRequest.getAccessCode().isEmpty()) {
            response.put("message", "Access code is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            // Authenticate the user and get the session token along with user role
            Map<String, String> authResponse = userService.authenticateUser(sessionRequest.getAccessCode());

            response.put("message", "Authentication successful.");
            response.put("sessionToken", authResponse.get("sessionToken"));
            response.put("userRole", authResponse.get("userRole"));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("message", "Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "An internal error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //get all users api for Superadmin, admin, client buisnessteam
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(@RequestHeader("Authorization") String sessionToken) {
        try {
            List<UserResponseDTO> users = userService.getAllUsers(sessionToken);
            return ResponseEntity.ok(users);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    //edit api
    @PutMapping("/edit/{userId}")
    public ResponseEntity<Map<String, String>> editUserById(
            @PathVariable String userId,
            @RequestHeader("Authorization") String sessionToken,
            @RequestBody UserResponseDTO userEditRequest) {
        try {
            userService.editUser(userId, sessionToken, userEditRequest);
            return ResponseEntity.ok(Map.of("message", "User updated successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to update user: " + e.getMessage()));
        }
    }



    //delete user api
@DeleteMapping("/delete/{userId}")
public ResponseEntity<Map<String, String>> deleteUser(
        @PathVariable String userId,
        @RequestHeader("Authorization") String sessionToken) {
    Map<String, String> response = new HashMap<>();
    try {
        userService.deleteUser(userId, sessionToken);
        response.put("message", "User deleted successfully.");
        return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    } catch (Exception e) {
        response.put("message", "An internal error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}




}
