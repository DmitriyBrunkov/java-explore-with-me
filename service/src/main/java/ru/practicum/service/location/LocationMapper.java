package ru.practicum.service.location;

import lombok.experimental.UtilityClass;
import ru.practicum.service.location.dto.LocationDto;
import ru.practicum.service.location.model.Location;

@UtilityClass
public class LocationMapper {
    public LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
