package com.hejz.springsecurityjpa.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "User")
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private Boolean active;
    private String roles;
}
