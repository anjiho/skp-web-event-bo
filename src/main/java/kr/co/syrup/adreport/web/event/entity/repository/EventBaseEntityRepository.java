package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventBaseEntityRepository extends JpaRepository<WebEventBaseEntity, Integer> {
    WebEventBaseEntity findFirstByOrderByIdDesc();
    Optional<WebEventBaseEntity> findByEventId(String eventId);
}
