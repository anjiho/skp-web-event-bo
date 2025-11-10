package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventWinningTextEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventWinningTextEntityRepository extends JpaRepository<ArEventWinningTextEntity, Long> {
    Optional<List<ArEventWinningTextEntity>> findAllByArEventWinningId(int arEventWinningId);
}
