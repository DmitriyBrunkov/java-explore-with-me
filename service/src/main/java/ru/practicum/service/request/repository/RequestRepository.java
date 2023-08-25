package ru.practicum.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.request.enums.RequestStatus;
import ru.practicum.service.request.model.Request;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByStatus(RequestStatus status);

    List<Request> findAllByRequester_Id(Long requesterId);

    Optional<Request> findByRequester_IdAndId(Long requesterId, Long id);

    List<Request> findAllByEvent_Id(Long eventId);

    @Modifying
    @Transactional
    @Query("update Request r set r.status = 'REJECTED' where r.status = 'PENDING' and r.event.id = :eventId")
    void rejectOverLimitRequestEvent(Long eventId);
}
