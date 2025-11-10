package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventLogExposureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface EventLogExposureEntityRepository extends JpaRepository<EventLogExposureEntity, Long> {
}
