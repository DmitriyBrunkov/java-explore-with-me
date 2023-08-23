package ru.practicum.service.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.request.enums.RequestStatus;
import ru.practicum.service.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Generated(GenerationTime.INSERT)
    @Column(insertable = false)
    private LocalDateTime created;
}
