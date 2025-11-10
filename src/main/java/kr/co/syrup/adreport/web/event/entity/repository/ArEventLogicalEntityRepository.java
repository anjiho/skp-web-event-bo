package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventLogicalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArEventLogicalEntityRepository extends JpaRepository<ArEventLogicalEntity, Integer> {
    ArEventLogicalEntity findByArEventId(int eventId);
}
