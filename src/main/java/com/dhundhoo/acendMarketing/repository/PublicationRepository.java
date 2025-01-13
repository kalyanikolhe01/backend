package com.dhundhoo.acendMarketing.repository;

import com.dhundhoo.acendMarketing.model.Publication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationRepository extends MongoRepository<Publication,String> {
    boolean existsByUserId(String userId);
    void deleteByUserId(String userId);
    List<Publication> findAll();
    Optional<Publication> findByUserId(String userId);
    List<Publication> findByPublicationStartingWithIgnoreCase(String initials);


}
