package com.eventsphere.event.model.dto;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
