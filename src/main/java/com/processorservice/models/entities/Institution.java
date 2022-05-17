package com.processorservice.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, name = "wallet_adress")
    private String walletAdress;

    @OneToMany(mappedBy="institution", fetch = FetchType.LAZY)
    private Set<User> users;

    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
    private Set<Event> events;
}
