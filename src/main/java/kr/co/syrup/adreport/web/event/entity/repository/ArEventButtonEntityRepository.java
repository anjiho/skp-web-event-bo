package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventButtonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArEventButtonEntityRepository extends JpaRepository<ArEventButtonEntity, Integer> {
    ArEventButtonEntity findByArEventId(int arEventId);
    ArEventButtonEntity findByStpId(int stpId);
}
