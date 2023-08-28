package ru.practicum.service.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @Min(value = -90)
    @Max(value = 90)
    private float lat;
    @Min(value = -180)
    @Max(value = 180)
    private float lon;
}
