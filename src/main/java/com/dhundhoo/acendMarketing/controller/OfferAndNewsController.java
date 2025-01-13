
package com.dhundhoo.acendMarketing.controller;

import com.dhundhoo.acendMarketing.model.OfferAndNews;
import com.dhundhoo.acendMarketing.service.OfferAndNewsService;
import com.dhundhoo.acendMarketing.utility.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/offerAndNews")
public class OfferAndNewsController {

    @Autowired
    private OfferAndNewsService offerAndNewsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Create OfferAndNews
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> createOfferAndNews(
            @RequestHeader("Authorization") String sessionToken,
            @Valid @RequestBody OfferAndNews offerAndNews) {

        Map<String, String> response = new HashMap<>();

        try {
            String userRole = jwtTokenUtil.extractUserRole(sessionToken);
            if (!"ADMIN".equals(userRole) && !"SUPER_ADMIN".equals(userRole)) {
                throw new IllegalArgumentException("Unauthorized: Only ADMIN or SUPER_ADMIN can access this API.");
            }
            if (offerAndNews.getText() != null && wordCount(offerAndNews.getText()) > 250) {
                throw new IllegalArgumentException("Text cannot exceed 250 words.");
            }
            offerAndNewsService.createOfferAndNews(sessionToken, offerAndNews);
            response.put("message", "OfferAndNews created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // Forbidden (403)

        } catch (RuntimeException e) {
            response.put("message", "Invalid request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Bad request (400)

        } catch (Exception e) {
            response.put("message", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Internal server error (500)
        }
    }
    private int wordCount(String text) {
        return text != null ? text.split("\\s+").length : 0;
    }

    //get api for offerse and news
    @GetMapping("/getAllOfferAndNews")
    public ResponseEntity<?> getAllOfferAndNews(@RequestHeader("Authorization") String sessionToken) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<OfferAndNews> offerAndNewsList = offerAndNewsService.getAllOfferAndNews(sessionToken);
            response.put("data", offerAndNewsList);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            response.put("error", "An unexpected error occurred. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Edit OfferAndNews
    @PutMapping("/edit/{offerAndNewsId}")
    public ResponseEntity<Map<String, String>> editOfferAndNews(
            @RequestHeader("Authorization") String sessionToken,
            @PathVariable("offerAndNewsId") String offerAndNewsId,
            @RequestBody OfferAndNews updatedOfferAndNews) {
        try {
            offerAndNewsService.editOfferAndNews(sessionToken, offerAndNewsId, updatedOfferAndNews);
            return ResponseEntity.ok(Map.of("message", "OfferAndNews has been updated successfully."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }




    // Delete OfferAndNews
    @DeleteMapping("/remove/{offerAndNewsId}")
    public ResponseEntity<Map<String, String>> deleteOfferAndNews(
            @RequestHeader("Authorization") String sessionToken,
            @PathVariable String offerAndNewsId) {
        try {
            offerAndNewsService.deleteOfferAndNews(sessionToken, offerAndNewsId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "OfferAndNews has been deleted successfully."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized: " + e.getMessage()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "OfferAndNews with the given ID not found."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

}
