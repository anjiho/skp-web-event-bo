package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventNftCouponRepositoryEntityRepository extends JpaRepository<ArEventNftCouponRepositoryEntity, Long> {
    List<ArEventNftCouponRepositoryEntity> findAllByGiveAwayIdInOrderByIdAsc(List<Integer>giveAwayIdList);
    List<ArEventNftCouponRepositoryEntity> findAllByStpGiveAwayIdInOrderByIdAsc(List<Long>stpGiveAwayIdList);
    void deleteByEventWinningLogId(long eventWinningLogId);
    Optional<ArEventNftCouponRepositoryEntity> findById(long id);
    void deleteByStampEventWinningLogId(long stampEventWinningLogId);
}
