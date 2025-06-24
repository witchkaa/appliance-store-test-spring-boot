package com.epam.rd.autocode.assessment.appliances.model;

import jakarta.persistence.*;
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

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}