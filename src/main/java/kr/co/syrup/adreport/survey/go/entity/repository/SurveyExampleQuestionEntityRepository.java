package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveyExampleQuestionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyExampleQuestionEntityRepository extends JpaRepository<SurveyExampleQuestionEntity, Long> {
    Optional<List<SurveyExampleQuestionEntity>> findAllBySurveySubjectId(long surveySubjectId, Sort sort);

    void deleteBySurveySubjectIdIn(List<Long> surveySubjectIds);

    void deleteBySurveySubjectId(long surveySubjectId);
}
