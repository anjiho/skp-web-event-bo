package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventDeviceGpsEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventDeviceGpsRepository extends JpaRepository<ArEventDeviceGpsEntity, Integer> {
    Optional<List<ArEventDeviceGpsEntity>> findAllByEventHtmlIdOrderByIdAsc(int eventHtmlId);
}
