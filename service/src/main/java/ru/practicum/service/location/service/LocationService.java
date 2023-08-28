package ru.practicum.service.location.service;

import ru.practicum.service.location.model.Location;

public interface LocationService {
    Location getLocationByLatLon(Float lat, Float lon);

    Location addLocation(Float lat, Float lon);
}
