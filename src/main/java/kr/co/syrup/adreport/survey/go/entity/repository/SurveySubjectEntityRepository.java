package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveySubjectEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveySubjectEntityRepository extends JpaRepository<SurveySubjectEntity, Long> {
    Optional<List<SurveySubjectEntity>> findAllByArEventId(int arEventId, Sort sort);
}
