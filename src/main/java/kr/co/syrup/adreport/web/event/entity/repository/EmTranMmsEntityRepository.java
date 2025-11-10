package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EmTranMmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmTranMmsEntityRepository extends JpaRepository<EmTranMmsEntity, Integer> {
    EmTranMmsEntity findFirstByOrderByIdDesc();
}
