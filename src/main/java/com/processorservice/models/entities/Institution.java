package com.processorservice.models.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, name = "wallet_adress")
    private String walletAdress;

    @OneToMany(mappedBy = "institution")
    private Set<User> users;

    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
    private Set<Event> events;

    public Institution(String name, String walletAdress) {
        this.name = name;
        this.walletAdress = walletAdress;
    }
}
