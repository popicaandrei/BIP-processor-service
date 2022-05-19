package com.processorservice.models.entities;

import com.processorservice.models.enums.AuthType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "event_name", nullable = false)
    private String name;

    @Column(name = "reward_quantity", nullable = false)
    private Double reward;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "needs_validation", nullable = false)
    private boolean validationNeeded;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    @Column(name = "auth_type", nullable = false)
    @Enumerated
    private AuthType authType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToMany(mappedBy = "event")
    private Set<EventRegistry> eventRegistries;

    @PrePersist
    private void onCreate() {
        timestamp = new Date(System.currentTimeMillis());
    }
}
