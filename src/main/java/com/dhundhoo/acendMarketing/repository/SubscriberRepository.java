package com.dhundhoo.acendMarketing.repository;

import com.dhundhoo.acendMarketing.model.Subscriber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface SubscriberRepository extends MongoRepository<Subscriber,String> {

}
