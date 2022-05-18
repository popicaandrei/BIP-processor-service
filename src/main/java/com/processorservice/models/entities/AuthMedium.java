package com.processorservice.models.entities;

import com.processorservice.models.enums.AuthType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "auth_medium")
@Getter
@Setter
@NoArgsConstructor
public class AuthMedium {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "auth_type")
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private String identificator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
