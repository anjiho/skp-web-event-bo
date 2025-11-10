package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveySubjectCategoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveySubjectCategoryEntityRepository extends JpaRepository<SurveySubjectCategoryEntity, Long> {
    Optional<List<SurveySubjectCategoryEntity>> findAllByArEventId(int arEventId, Sort sort);
}
