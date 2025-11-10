package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventAttendTimeEntity;
import kr.co.syrup.adreport.web.event.entity.projection.ProjectionEventAttendTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArEventAttendTimeEntityRepository extends JpaRepository<ArEventAttendTimeEntity, Integer> {
    void deleteByArEventId(int arEventId);
    List<ArEventAttendTimeEntity> findByArEventIdOrderByArEventAttendTimeIdAsc(Integer integer);

    List<ArEventAttendTimeEntity> findAllByArEventIdOrderByArEventAttendTimeIdAsc(Integer integer);
}
