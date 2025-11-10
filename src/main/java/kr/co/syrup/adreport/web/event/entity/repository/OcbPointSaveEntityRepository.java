package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.OcbPointSaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OcbPointSaveEntityRepository extends JpaRepository<OcbPointSaveEntity, Integer> {
    Optional<OcbPointSaveEntity> findTopByArEventIdAndArEventWinningId(int arEventId, Integer ArEventWinningId);
    Optional<OcbPointSaveEntity> findTopByArEventId(int arEventId);
}
