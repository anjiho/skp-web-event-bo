package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftTokenInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArEventNftTokenInfoRepository extends JpaRepository<ArEventNftTokenInfoEntity, Long> {
    Optional<List<ArEventNftTokenInfoEntity>> findAllByArEventWinningIdAndIsPayedOrderByIdAsc(int arEventWinningId, boolean isPayed);
    Optional<List<ArEventNftTokenInfoEntity>> findAllByArEventWinningId(int arEventWinningId);
    Optional<ArEventNftTokenInfoEntity> findDistinctFirstByArEventWinningId(int arEventWinningId);
    long countByArEventWinningId(int arEventWinningId);
    void deleteByArEventIdIsNullAndArEventWinningIdIsNull();

    void deleteByArEventWinningId(int arEventWinningId);
}
