package ru.practicum.service.request.service;

import ru.practicum.service.request.model.Request;

import java.util.List;

public interface RequestService {
    Request addRequest(Long userId, Long eventId);

    List<Request> getUserRequests(Long userId);

    Request cancelRequest(Long userId, Long requestId);

    Long getConfirmedRequests(Long eventId);

    List<Request> getRequests(Long eventId);

    void rejectOverLimitRequestEvent(Long eventId);

    Request getRequest(Long requestId);

    void updateRequest(Request request);
}
