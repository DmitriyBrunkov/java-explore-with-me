package ru.practicum.service.request.dto;

import lombok.Data;
import ru.practicum.service.request.enums.RequestStatus;

import java.util.Set;

@Data
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private RequestStatus status;
}
