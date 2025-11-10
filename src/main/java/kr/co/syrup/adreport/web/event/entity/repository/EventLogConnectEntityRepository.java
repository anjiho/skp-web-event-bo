package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventLogConnectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogConnectEntityRepository extends JpaRepository<EventLogConnectEntity, Long> {
    void deleteByArEventId(int arEventId);
}
