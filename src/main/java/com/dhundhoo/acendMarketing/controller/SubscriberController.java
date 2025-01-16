package com.dhundhoo.acendMarketing.controller;

import com.dhundhoo.acendMarketing.exception.DuplicateFieldException;
import com.dhundhoo.acendMarketing.model.Subscriber;
import com.dhundhoo.acendMarketing.model.User;
import com.dhundhoo.acendMarketing.service.SubscriberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping("/contactUs")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Subscriber subscriber) {
        try {
            Subscriber registeredUser = subscriberService.registerSubscriber(subscriber);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully...!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (DuplicateFieldException e) {
//           return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

}
