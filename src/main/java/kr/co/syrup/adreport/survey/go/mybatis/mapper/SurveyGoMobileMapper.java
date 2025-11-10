package kr.co.syrup.adreport.survey.go.mybatis.mapper;

import kr.co.syrup.adreport.survey.go.entity.SurveyTargetAgeGenderLimitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SurveyGoMobileMapper {

    @Select(" SELECT ifnull(survey_target_limit_count, 0) as cnt  FROM survey_target_age_gender_limit WHERE ar_event_id = #{arEventId} AND survey_target_gender = #{gender} OR survey_target_gender = 'A'  AND survey_target_age = #{age}")
    Integer selectSurveyTargetLimitCountByArEventIdAndGenderAndAge(@Param("arEventId") int arEventId, @Param("gender") String gender, @Param("age") int age);

    @Select(" SELECT sort FROM survey_subject_category WHERE survey_subject_category_id = #{surveySubjectCategoryId}")
    int selectSurveySubjectCategorySortByIndex(@Param("surveySubjectCategoryId") long surveySubjectCategoryId);

    @Select(" SELECT * FROM survey_target_age_gender_limit WHERE ar_event_id = #{arEventId} ")
    List<SurveyTargetAgeGenderLimitEntity> selectSurveyTargetAgeGenderLimitListByArEventId(int arEventId);

    @Select(" SELECT event_id, ar_event_id FROM survey_log_attend WHERE survey_log_attend_id = #{surveyLogAttendId} ")
    Map<String, Object> selectArEventIdFromSurveyLogAttendByIdx(@Param("surveyLogAttendId") String surveyLogAttendId);
}
