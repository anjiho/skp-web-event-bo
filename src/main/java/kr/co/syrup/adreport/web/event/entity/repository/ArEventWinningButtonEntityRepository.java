package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArEventWinningButtonEntityRepository extends JpaRepository<ArEventWinningButtonEntity, Integer> {
    List<ArEventWinningButtonEntity> findAllByArEventWinningIdOrderByButtonSortAsc(int ArEventWinningId);
    void deleteByArEventWinningId(int ArEventWinningId);
}
