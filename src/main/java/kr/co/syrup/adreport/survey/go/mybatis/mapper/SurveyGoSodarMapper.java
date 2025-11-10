package kr.co.syrup.adreport.survey.go.mybatis.mapper;

import kr.co.syrup.adreport.survey.go.entity.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SurveyGoSodarMapper {

    void updateSurveyTargetAgeGenderLimit(SurveyTargetAgeGenderLimitEntity entity);

    void updateSurveySubject(SurveySubjectEntity subjectEntity);

    void updateSurveyExample(SurveyExampleEntity exampleEntity);

    void updateSurveyExampleQuestion(SurveyExampleQuestionEntity exampleQuestionEntity);

    void updateSurveySubjectCategory(SurveySubjectCategoryEntity categoryEntity);

    void updateSurveySubjectPopupImage(SurveySubjectPopupImageEntity popupImageEntity);
}
