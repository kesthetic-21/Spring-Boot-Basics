package io.springboot.java.meetingscheduler.entities;

import io.springboot.java.meetingscheduler.enums.UserRoles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Email
    private String email;
    private String username;
    private String password;

    @Column(nullable = false)
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    private UserRoles role;
}
