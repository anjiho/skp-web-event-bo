package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArEventEntityRepository extends JpaRepository<ArEventEntity, Integer> {
    ArEventEntity findFirstByOrderByArEventIdDesc();
    ArEventEntity findByEventId(String eventId);
}
