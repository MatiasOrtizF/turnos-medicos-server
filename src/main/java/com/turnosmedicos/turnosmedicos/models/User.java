package com.turnosmedicos.turnosmedicos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "name is mandatory")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "last name is mandatory")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "dni is mandatory")
    @Column(name = "dni", nullable = false)
    private Integer dni;

    @NotBlank(message = "email is mandatory")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "password is mandatory")
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
