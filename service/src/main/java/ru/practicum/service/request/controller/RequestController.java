package ru.practicum.service.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.request.RequestMapper;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.service.RequestService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @NotNull @PositiveOrZero Long userId,
                                              @RequestParam @NotNull @PositiveOrZero Long eventId) {
        log.info(this.getClass().getSimpleName() + ": POST: userId: {} eventId: {}", userId, eventId);
        return RequestMapper.toParticipationRequestDto(requestService.addRequest(userId, eventId));
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable @NotNull @PositiveOrZero Long userId) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: {} ", userId);
        return requestService.getUserRequests(userId).stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @PositiveOrZero Long userId,
                                                 @PathVariable @PositiveOrZero Long requestId) {
        log.info(this.getClass().getSimpleName() + ": PATCH: userId: {} requestId: {}", userId, requestId);
        return RequestMapper.toParticipationRequestDto(requestService.cancelRequest(userId, requestId));
    }
}
