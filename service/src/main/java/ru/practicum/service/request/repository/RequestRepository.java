package ru.practicum.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.request.enums.RequestStatus;
import ru.practicum.service.request.model.Request;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select count(*) from Request r " +
            "where r.status = :status " +
            "and r.event.id = :eventId ")
    Long getCountConfirmedRequestsByEvent(RequestStatus status, Long eventId);

    @Query("select r.event.id, count(*) " +
            "from Request r " +
            "where r.status = :status " +
            "and r.event.id in :eventIds " +
            "group by r.event.id")
    List<Object[]> getCountConfirmedRequestsByEvents(RequestStatus status, Set<Long> eventIds);

    List<Request> findAllByRequester_Id(Long requesterId);

    Optional<Request> findByRequester_IdAndId(Long requesterId, Long id);

    List<Request> findAllByEvent_Id(Long eventId);

    @Modifying
    @Transactional
    @Query("update Request r set r.status = 'REJECTED' where r.status = 'PENDING' and r.event.id = :eventId")
    void rejectOverLimitRequestEvent(Long eventId);
}
