package com.dhundhoo.acendMarketing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "Publisher")
public class Publication {
    @Id
    @JsonIgnore
    private String id;
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
