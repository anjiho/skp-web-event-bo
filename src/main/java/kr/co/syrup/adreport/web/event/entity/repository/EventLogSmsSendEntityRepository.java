package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventLogSmsSendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventLogSmsSendEntityRepository extends JpaRepository<EventLogSmsSendEntity, Long> {
}
