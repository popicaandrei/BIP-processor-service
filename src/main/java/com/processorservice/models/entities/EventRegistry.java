package com.processorservice.models.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event_registry")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(nullable = false)
    private boolean rewarded;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    @PrePersist
    private void onCreate() {
        timestamp = new Date(System.currentTimeMillis());
    }

}
