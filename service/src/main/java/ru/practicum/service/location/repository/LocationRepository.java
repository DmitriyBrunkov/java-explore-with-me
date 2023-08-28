package ru.practicum.service.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findLocationByLatAndLon(Float lat, Float lon);
}
