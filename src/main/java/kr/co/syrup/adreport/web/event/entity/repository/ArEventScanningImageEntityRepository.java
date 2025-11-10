package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventScanningImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventScanningImageEntityRepository extends JpaRepository<ArEventScanningImageEntity, Integer> {
    void deleteByArEventId(int arEventId);
    Optional<List<ArEventScanningImageEntity>> findAllByArEventIdOrderByArEventScanningImageIdAsc(int arEventId);
    List<ArEventScanningImageEntity> findByArEventId(int arEventId);
}
