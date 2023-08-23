package ru.practicum.service.location.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.location.repository.LocationRepository;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location getLocationByLatLon(Float lat, Float lon) {
        return locationRepository.findLocationByLatAndLon(lat, lon);
    }

    @Override
    public Location addLocation(Float lat, Float lon) {
        return locationRepository.save(new Location(null, lat, lon));
    }
}
