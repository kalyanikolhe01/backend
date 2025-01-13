package com.dhundhoo.acendMarketing.model;
import com.dhundhoo.acendMarketing.enums.OffersAndNews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.UUID;

@Data
@Document(collection = "OffersAndNews")
public class OfferAndNews {
    @Id
    @JsonIgnore
    private String id;

    private String offersAndNews = UUID.randomUUID().toString();

    private String text;

    @Field(targetType = FieldType.STRING)
    @NotNull(message = "please mentioned type")
    private OffersAndNews type;
}
