package com.dhundhoo.acendMarketing.dto;
import com.dhundhoo.acendMarketing.enums.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {
    private String id;
    private String userId;
    private String email;
    private long mobileNumber;
    private UserRole userRole;
    private Integer margin; // Include margin if necessary
}