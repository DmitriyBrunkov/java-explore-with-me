package ru.practicum.service.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.exception.model.RequestValidationException;
import ru.practicum.service.request.enums.RequestStatus;
import ru.practicum.service.request.model.Request;
import ru.practicum.service.request.repository.RequestRepository;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    public Long getConfirmedRequests(Long eventId) {
        List<Request> requestList = requestRepository.findAllByStatus(RequestStatus.CONFIRMED);
        Map<Long, Long> confirmedRequests = new HashMap<>();
        for (Request request : requestList) {
            confirmedRequests.put(request.getEvent().getId(),
                    confirmedRequests.getOrDefault(request.getEvent().getId(), 0L) + 1);
        }
        return confirmedRequests.getOrDefault(eventId, 0L);
    }

    @Override
    public Request addRequest(Long userId, Long eventId) {
        return requestRepository.save(buildRequest(userId, eventId));
    }

    @Override
    public List<Request> getUserRequests(Long userId) {
        return requestRepository.findAllByRequester_Id(userId);
    }

    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByRequester_IdAndId(userId, requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request: " + requestId + " not found"));
        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequests(Long eventId) {
        return requestRepository.findAllByEvent_Id(eventId);
    }

    @Override
    public void rejectOverLimitRequestEvent(Long eventId) {
        requestRepository.rejectOverLimitRequestEvent(eventId);
    }

    @Override
    public Request getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request: " + requestId + " not found"));
    }

    @Override
    public void updateRequest(Request request) {
        requestRepository.save(request);
    }

    private Request buildRequest(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event: " + eventId +
                " not found"));

        if (event.getInitiator().equals(user)) {
            throw new RequestValidationException("Event initiator cannot add a request to participate in his own " +
                    "event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestValidationException("Can't participate in an unpublished event");
        }

        Long confirmedRequests =  getConfirmedRequests(eventId);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new RequestValidationException("Event has reached the limit of requests for participation");
        }

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return new Request(null, event, user, RequestStatus.CONFIRMED, null);
        }

        return new Request(null, event, user, RequestStatus.PENDING, null);
    }
}
