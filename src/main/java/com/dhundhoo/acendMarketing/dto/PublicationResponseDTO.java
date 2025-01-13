package com.dhundhoo.acendMarketing.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicationResponseDTO {
    private String userId = UUID.randomUUID().toString();
    private String publication;
    private float orgPrice;
    private float marginPrice;
    private float usdPrice;
    private Integer wordsAllowed;
    private String backlinksAllowed;
    private Integer tat;
    private boolean sponsored;
    private boolean indexed;
    private boolean doFollow;
    private String genres;
    private String sample;
    private String daChecker;
    private String trafficChecker;
}
