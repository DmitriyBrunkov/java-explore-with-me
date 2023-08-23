package ru.practicum.service.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state = EventState.PENDING;

    private String title;
}
