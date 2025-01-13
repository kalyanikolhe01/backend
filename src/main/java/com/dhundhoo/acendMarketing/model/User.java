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
@Document(collection = "User")
public class User {
    @Id
    private String id;

    private String userId = UUID.randomUUID().toString();

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Indexed(unique = true)
    private String email;

    @NotNull(message = "Mobile number cannot be null")
    @Min(value = 1000000000L, message = "Mobile number should be at least 10 digits")
    @Indexed(unique = true)
    private long mobileNumber;


    @NotNull(message = "Please enter password")
    @NotBlank(message = "Please enter password")
    private String password;


    @NotNull(message = "Please mention role")
    @Field(targetType = FieldType.STRING)
    private UserRole userRole;

    @Field(targetType = FieldType.INT32)
    private Integer margin;


}
