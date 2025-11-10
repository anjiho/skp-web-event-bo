package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventLogWinningSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventLogWinningSubscriptionRepository extends JpaRepository<EventLogWinningSubscriptionEntity, Long> {
    Optional<EventLogWinningSubscriptionEntity> findByGiveAwayId(int giveAwayId);
}
