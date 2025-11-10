package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArPhotoLogicalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArPhotoLogicalEntityRepository extends JpaRepository<ArPhotoLogicalEntity, Integer> {
    Optional<ArPhotoLogicalEntity> findByArEventId(int arEventId);
}
