package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{user.name.notblank}")
    @Size(min = 2, max = 50, message = "{user.name.size}")
    private String name;

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.valid}")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 6, message = "{user.password.size}")
    @Column(nullable = false)
    private String password;
}