package com.dhundhoo.acendMarketing.controller;

import com.dhundhoo.acendMarketing.dto.PublicationRequestDTO;
import com.dhundhoo.acendMarketing.dto.PublicationResponseDTO;
import com.dhundhoo.acendMarketing.model.Publication;
import com.dhundhoo.acendMarketing.service.PublicationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    private static final Logger logger = LoggerFactory.getLogger(PublicationController.class);
    @Autowired
    private PublicationService publicationService;

    //add publication api for superadmin only
    @PostMapping("/addPublication")
    public ResponseEntity<Map<String, String>> addPublication(
            @Valid @RequestBody PublicationRequestDTO publicationRequest,
            @RequestHeader("Authorization") String sessionToken) {
        Map<String, String> response = new HashMap<>();
        try {
            publicationService.addPublication(publicationRequest, sessionToken);
            response.put("message", "Publication added successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while adding the publication.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //get all publication controller
    @GetMapping("/getAllPublication")
    public ResponseEntity<List<PublicationResponseDTO>> getPublications(@RequestHeader("Authorization") String sessionToken) {
        try {
            if (sessionToken == null || sessionToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            List<PublicationResponseDTO> publications = publicationService.getAllPublications(sessionToken);

            return ResponseEntity.ok(publications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    //delete publication api for superadmin
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<Map<String, String>> deletePublicationByUserId(
            @PathVariable String userId,
            @RequestHeader("Authorization") String sessionToken) {
        Map<String, String> response = new HashMap<>();
        try {
            publicationService.deletePublication(userId, sessionToken);
            response.put("message", "Publication deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "Failed to delete publication: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    //edit controller
    @PutMapping("/edit/{userId}")
    public ResponseEntity<Map<String, String>> editPublicationByUserId(
            @PathVariable String userId,
            @RequestHeader("Authorization") String sessionToken,
            @RequestBody PublicationRequestDTO publicationRequestDTO) {

        try {
            publicationService.editPublication(userId, sessionToken, publicationRequestDTO);
            return ResponseEntity.ok(Map.of("message", "Publication updated successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to update publication: " + e.getMessage()));
        }
    }




    //send bulk data with excel
    @PostMapping("/addBulkPublication")
    public ResponseEntity<Map<String, String>> addBulkPublication(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String sessionToken) {

        Map<String, String> response = new HashMap<>();
        try {
            // Delegate the logic to the service layer
            publicationService.addBulkPublications(file, sessionToken);

            response.put("message", "Publications added successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while adding bulk publications.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}
