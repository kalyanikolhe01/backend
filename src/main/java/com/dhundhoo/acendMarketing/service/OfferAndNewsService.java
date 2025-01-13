
package com.dhundhoo.acendMarketing.service;

import com.dhundhoo.acendMarketing.model.OfferAndNews; // Correct model import
import com.dhundhoo.acendMarketing.model.User;
import com.dhundhoo.acendMarketing.repository.OffersAndNewsRepository; // Correct repository import
import com.dhundhoo.acendMarketing.repository.UserRepository;
import com.dhundhoo.acendMarketing.utility.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Service
public class OfferAndNewsService {

    @Autowired
    private OffersAndNewsRepository offersAndNewsRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    // Create OfferAndNews
    public OfferAndNews createOfferAndNews(String sessionToken, OfferAndNews offerAndNews) {
        String userRole = jwtTokenUtil.extractUserRole(sessionToken);
        if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole)) {
            throw new IllegalArgumentException("Unauthorized: Only ADMIN or SUPER_ADMIN can access this API.");
        }
        if (wordCount(offerAndNews.getText()) > 250) {
            throw new IllegalArgumentException("Text cannot exceed 250 words.");
        }
        return offersAndNewsRepository.save(offerAndNews);
    }

    private int wordCount(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text);
        return tokenizer.countTokens();
    }


    // Get all OfferAndNews
    public List<OfferAndNews> getAllOfferAndNews(String sessionToken) {
        String userId = jwtTokenUtil.extractUserIdFromSession(sessionToken);
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Unauthorized: User does not exist.");
        }
        return offersAndNewsRepository.findAll(); // Correct method usage
    }


    // Edit OfferAndNews for admin and superadmin
    public OfferAndNews editOfferAndNews(String sessionToken, String offerAndNews, OfferAndNews updatedOfferAndNews) {
        String userRole = jwtTokenUtil.extractUserRole(sessionToken);

        if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole)) {
            throw new IllegalArgumentException("Unauthorized: Only ADMIN or SUPER_ADMIN can edit OfferAndNews.");
        }
        OfferAndNews existingOfferAndNews = offersAndNewsRepository.findByOffersAndNews(offerAndNews)
                .orElseThrow(() -> new RuntimeException("OfferAndNews with the given ID not found."));

        if (updatedOfferAndNews.getText() != null && !updatedOfferAndNews.getText().isEmpty()) {
            existingOfferAndNews.setText(updatedOfferAndNews.getText());
        }
        if (updatedOfferAndNews.getType() != null) {
            existingOfferAndNews.setType(updatedOfferAndNews.getType());
        }

        return offersAndNewsRepository.save(existingOfferAndNews);
    }


    // Delete OfferAndNews
    public void deleteOfferAndNews(String sessionToken, String offerAndNewsId) {
        String userRole = jwtTokenUtil.extractUserRole(sessionToken);
        if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole)) {
            throw new IllegalArgumentException("Unauthorized: Only ADMIN or SUPER_ADMIN can delete OfferAndNews.");
        }
        OfferAndNews existingOfferAndNews = offersAndNewsRepository.findByOffersAndNews(offerAndNewsId)
                .orElseThrow(() -> new RuntimeException("OfferAndNews with the given ID not found."));
        offersAndNewsRepository.delete(existingOfferAndNews);
    }

}

