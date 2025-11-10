package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArEventWinningEntityRepository extends JpaRepository<ArEventWinningEntity, Integer> {
    List<ArEventWinningEntity> findAllByArEventId(int arEventId);
    List<ArEventWinningEntity> findAllByArEventIdOrderByArEventWinningIdAsc(int arEventId);
    ArEventWinningEntity findFirstByArEventIdOrderByArEventWinningIdDesc(int eventId);
    List<ArEventWinningEntity> findAllByArEventIdAndWinningTypeInOrderByEventWinningSortAsc(int arEventId, List<String> winningTypeIn);
    List<ArEventWinningEntity> findAllByArEventIdAndSubscriptionYnAndWinningTypeInOrderByEventWinningSortAsc(int arEventId, String subscriptionYn, List<String> winningTypeIn);
    void deleteByArEventId(int arEventId);
    ArEventWinningEntity findFirstByStpIdOrderByArEventWinningIdDesc(int stpId);
    List<ArEventWinningEntity> findAllByStpId(int stpId);
    List<ArEventWinningEntity> findAllByStpIdOrderByEventWinningSortAsc(int stpId);
}
