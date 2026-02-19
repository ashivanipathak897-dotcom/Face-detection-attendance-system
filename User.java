package com.example.faceattendance.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private String department;
    private String rollNumber;
    @Column(length = 5000) // Store face encoding as JSON string
    private String faceEncoding;
}
