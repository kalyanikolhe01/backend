package com.dhundhoo.acendMarketing.repository;

import com.dhundhoo.acendMarketing.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(Long mobileNumber);
    Optional<User> findByUserId(String userId);
    void deleteByUserId(String userId);
}
