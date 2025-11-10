package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventRepositoryButtonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventRepositoryButtonEntityRepository extends JpaRepository<ArEventRepositoryButtonEntity, Long> {
    Optional<List<ArEventRepositoryButtonEntity>> findAllByArEventWinningId(int arEventWinningId);
}
