package com.eduflow.course.dto.external;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
}
