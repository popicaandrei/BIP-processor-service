package com.processorservice.models.entities;

import com.processorservice.models.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
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

    @Embedded
    @AttributeOverride(name = "number", column = @Column(name = "street_number"))
    private Address cityAddress;

    @Enumerated
    private RoleType role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToMany(mappedBy = "user")
    private Set<EventRegistry> eventRegistries;

    @OneToMany(mappedBy = "user")
    private Set<AuthMedium> authMedium;

    @ManyToMany
    @JoinTable(
        name = "event_registry",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name="event_id"))
    private Set<Event> events;

}
