package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventGateCodeEntity;
import kr.co.syrup.adreport.web.event.entity.projection.ProjectionAttendCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventGateCodeEntityRepository extends JpaRepository<ArEventGateCodeEntity, Long> {
    void deleteByEventId(String eventId);
    Optional<ArEventGateCodeEntity> findByEventIdAndAttendCode(String eventId, String attendCode);
}
