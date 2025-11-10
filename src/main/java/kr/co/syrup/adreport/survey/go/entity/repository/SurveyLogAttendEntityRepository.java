package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyLogAttendEntityRepository extends JpaRepository<SurveyLogAttendEntity, String> {

    SurveyLogAttendEntity findBySurveyLogAttendId(String surveyLogAttendId);
    void deleteByArEventId(int arEventId);

}
