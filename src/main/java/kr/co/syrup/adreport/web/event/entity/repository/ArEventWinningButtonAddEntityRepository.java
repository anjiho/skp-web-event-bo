package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonAddEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventWinningButtonAddEntityRepository extends JpaRepository<ArEventWinningButtonAddEntity, Long> {
    Optional<List<ArEventWinningButtonAddEntity>> findAllByArEventWinningButtonId(int arEventWinningButtonId);
    void deleteAllByArEventWinningButtonId(int arEventWinningButtonId);
}
