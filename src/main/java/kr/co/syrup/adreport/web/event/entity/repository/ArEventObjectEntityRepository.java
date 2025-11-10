package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventObjectEntityRepository extends JpaRepository<ArEventObjectEntity, Integer> {
    void deleteByArEventId(int ArEventId);
    Optional<List<ArEventObjectEntity>> findByArEventIdOrderByArEventObjectIdAsc(int arEventId);
    List<ArEventObjectEntity> findByArEventId(int arEventId);
}
