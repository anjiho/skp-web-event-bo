package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftBannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventNftBannerRepository extends JpaRepository<ArEventNftBannerEntity, Integer> {
    Optional<List<ArEventNftBannerEntity>> findAllByArEventIdOrderByIdAsc(int arEventId);
    Optional<List<ArEventNftBannerEntity>> findAllByEventHtmlId(int eventHtmlId);
    Optional<List<ArEventNftBannerEntity>> findAllByStpIdOrderByIdAsc(int stpId);

    Optional<ArEventNftBannerEntity> findById(int id);
}
