package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventLogAttendButtonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogAttendButtonEntityRepository extends JpaRepository<EventLogAttendButtonEntity, Long> {
    long countByEventIdAndAttendCode(String eventId, String attendCode);
    void deleteByArEventId(int arEventId);
}
