package com.dhundhoo.acendMarketing.repository;

import com.dhundhoo.acendMarketing.model.OfferAndNews;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OffersAndNewsRepository extends MongoRepository<OfferAndNews,String> {
//    Optional<OfferAndNews> findByOffersAndNews(String offersAndNewsId);
@Query("{ 'offersAndNews' : ?0 }")
Optional<OfferAndNews> findByOffersAndNews(String offersAndNews);
}
