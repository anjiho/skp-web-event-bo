package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventNftRepositoryRepository extends JpaRepository<ArEventNftRepositoryEntity, Long> {
    Optional<List<ArEventNftRepositoryEntity>> findAllByGiveAwayIdInOrderByIdAsc(List<Integer>giveAwayIdList);
}
