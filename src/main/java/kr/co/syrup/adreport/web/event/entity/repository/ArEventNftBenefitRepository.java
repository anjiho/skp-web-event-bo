package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArEventNftBenefitRepository extends JpaRepository<ArEventNftBenefitEntity, Integer> {
    Optional<List<ArEventNftBenefitEntity>> findAllByArEventWinningIdOrderByArEventNftBenefitIdAsc(int arEventWinningId);
}
