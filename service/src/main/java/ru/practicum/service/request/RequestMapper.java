package ru.practicum.service.request;

import lombok.experimental.UtilityClass;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.model.Request;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(request.getCreated(), request.getEvent().getId(), request.getId(),
                request.getRequester().getId(), request.getStatus());
    }
}
