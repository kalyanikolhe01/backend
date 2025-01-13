package com.dhundhoo.acendMarketing.service;

import com.dhundhoo.acendMarketing.dto.UserResponseDTO;
import com.dhundhoo.acendMarketing.enums.UserRole;
import com.dhundhoo.acendMarketing.exception.DuplicateFieldException;
import com.dhundhoo.acendMarketing.exception.InvalidCredentialsException;
import com.dhundhoo.acendMarketing.model.User;
import com.dhundhoo.acendMarketing.repository.UserRepository;
import com.dhundhoo.acendMarketing.utility.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public passwordEncoder passwordEncoder;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public JwtTokenUtil jwtTokenUtil;

    //register api
    public User registerUser(User user) {
        try {
            // Log the user details
            logger.debug("Registering user with email: {}", user.getEmail());

            Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
            if (existingUserByEmail.isPresent()) {
                logger.debug("Email already in use: {}", user.getEmail());
                throw new DuplicateFieldException("Email is already in use.");
            }

            Optional<User> existingUserByMobile = userRepository.findByMobileNumber(user.getMobileNumber());
            if (existingUserByMobile.isPresent()) {
                logger.debug("Mobile number already in use: {}", user.getMobileNumber());
                throw new DuplicateFieldException("Mobile number is already in use.");
            }

            // Log user role and margine
            logger.debug("User role: {}, Margine: {}", user.getUserRole(), user.getMargin());

            if (user.getUserRole() == UserRole.CLIENT) {
                if (user.getMargin() == null) {
                    throw new IllegalArgumentException("Margine is required for CLIENT role.");
                }
            } else {
                user.setMargin(null);
            }

            user.setPassword(passwordEncoder.hashPassword(user.getPassword()));
            logger.debug("Password hashed successfully.");

            User savedUser = userRepository.save(user);
            logger.debug("User saved successfully: {}", savedUser);
            return savedUser;
        } catch (DuplicateFieldException e) {
            logger.error("Duplicate field error: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while registering the user..!");
        }
    }





    //login api
    public String loginUser(String identifier, String password) {
        try {
            Optional<User> userOptional = getUserByIdentifier(identifier);
            if (userOptional.isEmpty()) {
                throw new InvalidCredentialsException("Invalid credentials. User not found.");
            }
            User user = userOptional.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new InvalidCredentialsException("Invalid credentials. Incorrect password.");
            }
            return jwtTokenUtil.generateAccessToken(user.getUserId());

        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("something getting wrong...1 ");
        }
    }

    private Optional<User> getUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier);
        } else {
            try {
                long mobileNumber = Long.parseLong(identifier);
                return userRepository.findByMobileNumber(mobileNumber);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
    }


    //auth api

    public Map<String, String> authenticateUser(String accessCode) {
        Claims claims = jwtTokenUtil.parseToken(accessCode);
        String userId = claims.get("userId", String.class);
        String accessCodePayload = claims.get("accessCode", String.class);
        Optional<User> userOptional = userRepository.findByUserId(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        User user = userOptional.get();

        // Generate session token
        String sessionToken = jwtTokenUtil.generateSessionToken(
                user.getUserId(),
                String.valueOf(user.getUserRole()),
                accessCode
        );

        // Prepare the response with session token and user role
        Map<String, String> response = new HashMap<>();
        response.put("sessionToken", sessionToken);
        response.put("userRole", String.valueOf(user.getUserRole())); // Return user role

        return response;
    }





    //get all users api

    public List<UserResponseDTO> getAllUsers(String sessionToken) {
        // Extract userId from session token
        String userId = jwtTokenUtil.extractUserIdFromSession(sessionToken);
        if (userId == null) {
            throw new SecurityException("Invalid session or user not found.");
        }

        // Fetch user details and validate role
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new SecurityException("User not found.");
        }

        User user = userOptional.get();
        // Check if user role is either SUPER_ADMIN or ADMIN
        if (user.getUserRole() != UserRole.SUPER_ADMIN && user.getUserRole() != UserRole.ADMIN) {
            throw new SecurityException("Access denied. Only SUPER_ADMIN or ADMIN can access this API.");
        }

        // Fetch all users from the database
        List<User> users = userRepository.findAll();

        // Convert users to DTOs and exclude sensitive fields
        return users.stream()
                .map(userItem -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    BeanUtils.copyProperties(userItem, dto, "password"); // Exclude the password field
                    return dto;
                })
                .collect(Collectors.toList());
    }



    //edit user service
    public UserResponseDTO editUser(String userId, String sessionToken, UserResponseDTO userEditRequest) {
        Claims claims;
        try {
            claims = jwtTokenUtil.parseToken(sessionToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired session token.");
        }
        String userRole = claims.get("userRole", String.class);
        if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole)) {
            throw new RuntimeException("Unauthorized: Only ADMIN or SUPER_ADMIN can edit user information.");
        }
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User with the given userId not found.");
        }
        User user = userOptional.get();

        if (userEditRequest.getEmail() != null) {
            user.setEmail(userEditRequest.getEmail());
        }if (userEditRequest.getMobileNumber() != 0) {
            user.setMobileNumber(userEditRequest.getMobileNumber());
        }if (userEditRequest.getUserRole() != null) {
            user.setUserRole(userEditRequest.getUserRole());
        }if (userEditRequest.getMargin() != null) {
            user.setMargin(userEditRequest.getMargin());
        }
        User updatedUser = userRepository.save(user);
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(updatedUser.getId());
        responseDTO.setUserId(updatedUser.getUserId());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setMobileNumber(updatedUser.getMobileNumber());
        responseDTO.setUserRole(updatedUser.getUserRole());
        responseDTO.setMargin(updatedUser.getMargin());
        return responseDTO;
    }


    //delete user api
    public void deleteUser(String userId, String sessionToken) {
        Claims claims;
        try {
            claims = jwtTokenUtil.parseToken(sessionToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired session token.");
        }
        String userRole = claims.get("userRole", String.class);
        if (!"SUPER_ADMIN".equals(userRole)) {
            throw new RuntimeException("Unauthorized: Only SUPER_ADMIN can delete users.");
        }
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User with the given userId not found.");
        }
        userRepository.deleteByUserId(userId);
    }
}
