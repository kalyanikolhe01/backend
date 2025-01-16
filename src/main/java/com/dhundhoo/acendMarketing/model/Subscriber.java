package com.dhundhoo.acendMarketing.model;
import com.dhundhoo.acendMarketing.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Subscribers")
public class Subscriber  {
    @Id
    private String id;

    private String userId = UUID.randomUUID().toString();

    @NotBlank(message = "please enter userName")
    @NotNull (message = "please enter userName")
    public  String userName;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    @Min(value = 1000000000L, message = "Mobile number should be at least 10 digits")
    private long mobileNumber;

}
