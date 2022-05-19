package com.processorservice.models.entities;

import com.processorservice.models.enums.AuthType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "auth_medium")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthMedium {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "auth_type")
    @Enumerated
    private AuthType authType;

    @Column(unique = true)
    private String identificator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
