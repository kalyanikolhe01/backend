package com.dhundhoo.acendMarketing.service;

import com.dhundhoo.acendMarketing.enums.UserRole;
import com.dhundhoo.acendMarketing.exception.DuplicateFieldException;
import com.dhundhoo.acendMarketing.model.Subscriber;
import com.dhundhoo.acendMarketing.model.User;
import com.dhundhoo.acendMarketing.repository.SubscriberRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    public Subscriber registerSubscriber(Subscriber subscriber) {
        try {
            return subscriberRepository.save(subscriber);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register subscriber: " + e.getMessage());
        }
    }
}



