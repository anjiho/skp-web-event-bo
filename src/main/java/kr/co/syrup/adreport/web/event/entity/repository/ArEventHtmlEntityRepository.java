package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventHtmlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventHtmlEntityRepository extends JpaRepository<ArEventHtmlEntity, Integer> {
    List<ArEventHtmlEntity> findAllByArEventIdOrderByHtmlTypeSortAsc(int arEventId);
    Optional<ArEventHtmlEntity> findByArEventIdOrderByEventHtmlIdDesc(int arEventId);
    Optional<ArEventHtmlEntity> findFirstByEventHtmlId(int eventHtmlId);
    Optional<ArEventHtmlEntity> findByEventIdAndHtmlButtonType(String eventId, String htmlButtonType);
    Optional<List<ArEventHtmlEntity>> findByArEventId(int arEventId);
    void deleteByArEventId(int arEventId);
    Optional<List<ArEventHtmlEntity>> findByStpIdAndStpPanIdIsNull(int stpId);
    Optional<List<ArEventHtmlEntity>> findByStpIdAndStpPanIdIsNullOrderByHtmlTypeSort(int stpId);
    Optional<List<ArEventHtmlEntity>> findByStpPanId(int stpPanId);

    Optional<List<ArEventHtmlEntity>> findByStpPanIdOrderByHtmlTypeSort(int stpPanId);
    Optional<List<ArEventHtmlEntity>> findByStpIdAndStpPanId(int stpId, int stpPanId);
}
