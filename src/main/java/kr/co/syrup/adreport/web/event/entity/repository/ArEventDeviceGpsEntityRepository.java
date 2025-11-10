package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventDeviceGpsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventDeviceGpsEntityRepository extends JpaRepository<ArEventDeviceGpsEntity, Integer> {
    Optional<List<ArEventDeviceGpsEntity>> findAllByEventHtmlId(int eventHtmlId);
}
