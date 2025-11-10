package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventLogWinningEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogWinningEntityRepository extends JpaRepository<EventLogWinningEntity, Long> {
}
