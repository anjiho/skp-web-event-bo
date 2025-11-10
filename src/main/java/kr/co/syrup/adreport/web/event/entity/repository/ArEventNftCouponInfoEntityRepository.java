package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponInfoEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftTokenInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventNftCouponInfoEntityRepository extends JpaRepository<ArEventNftCouponInfoEntity, Long> {
    Optional<List<ArEventNftCouponInfoEntity>> findAllByArEventWinningId(int arEventWinningId);
    Optional<ArEventNftCouponInfoEntity> findDistinctFirstByArEventWinningId(int arEventWinningId);
    Optional<List<ArEventNftCouponInfoEntity>> findAllByArEventWinningIdAndIsPayedOrderByIdAsc(int arEventWinningId, boolean isPayed);
    long countByArEventWinningId(int arEventWinningId);
    void deleteByArEventWinningId(int arEventWinning);
    void deleteByArEventIdIsNullAndArEventWinningIdIsNull();
    void deleteByStpIdIsNullAndArEventWinningIdIsNull();
}
