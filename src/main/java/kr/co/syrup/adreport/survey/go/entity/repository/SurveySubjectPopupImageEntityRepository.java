package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveySubjectPopupImageEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveySubjectPopupImageEntityRepository extends JpaRepository<SurveySubjectPopupImageEntity, Long> {
    Optional<List<SurveySubjectPopupImageEntity>> findAllBySurveySubjectId(long surveySubjectId, Sort sort);

    void deleteBySurveySubjectIdIn(List<Long> surveySubjectIds);

}
