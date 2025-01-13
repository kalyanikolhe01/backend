package com.dhundhoo.acendMarketing.dto;
import lombok.Data;

@Data
public class PublicationRequestDTO {
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

