package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArPhotoContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArPhotoContentsEntityRepository extends JpaRepository<ArPhotoContentsEntity, Long> {
    Optional<List<ArPhotoContentsEntity>> findAllByArEventIdAndPhotoContentType(int arEventId, String photoContentType);

    Optional<List<ArPhotoContentsEntity>> findAllByArEventId(int arEventId);

    Optional<List<ArPhotoContentsEntity>> findAllByArEventIdAndPhotoContentTypeOrderBySortAsc(int arEventId, String photoContentType);
}
