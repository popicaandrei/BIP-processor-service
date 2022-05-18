package com.processorservice.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.processorservice.models.enums.RoleType;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user_metadata")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "wallet_address")
    private String walletAddress;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Embedded
    private Address cityAddress;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @JsonIgnore
    private Institution institution;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<EventRegistry> eventRegistries;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<AuthMedium> authMedium;

    public User(String name, String email, String password, RoleType role, String phoneNumber,
                String walletAddress, Address cityAddress, Institution institution) {
        this.password = password;
        this.role = role;
        this.cityAddress = cityAddress;
        this.walletAddress = walletAddress;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.name = name;
        this.institution = institution;
    }
}
