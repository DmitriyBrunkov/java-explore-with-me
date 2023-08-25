package ru.practicum.service.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.request.RequestMapper;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @Positive Long userId, @RequestParam @Positive Long eventId) {
        log.info("{}: POST: userId: {} eventId: {}", this.getClass().getSimpleName(), userId, eventId);
        return RequestMapper.toParticipationRequestDto(requestService.addRequest(userId, eventId));
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable @Positive Long userId) {
        log.info("{}: GET: userId: {} ", this.getClass().getSimpleName(), userId);
        return requestService.getUserRequests(userId).stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId, @PathVariable @Positive Long requestId) {
        log.info("{}: PATCH: userId: {} requestId: {}", this.getClass().getSimpleName(), userId, requestId);
        return RequestMapper.toParticipationRequestDto(requestService.cancelRequest(userId, requestId));
    }
}
