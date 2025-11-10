package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveyExampleEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyExampleEntityRepository extends JpaRepository<SurveyExampleEntity, Long> {
    Optional<List<SurveyExampleEntity>> findAllBySurveySubjectId(long surveySubjectId, Sort sort);

    void deleteBySurveySubjectIdIn(List<Long> surveySubjectIds);

    void deleteBySurveySubjectId(long surveySubjectId);

    SurveyExampleEntity findBySurveyExampleId(Long surveyExampleId);
}
