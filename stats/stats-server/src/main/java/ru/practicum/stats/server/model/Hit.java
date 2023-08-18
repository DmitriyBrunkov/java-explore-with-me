package ru.practicum.stats.server.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
