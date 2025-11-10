package kr.co.syrup.adreport.survey.go.mybatis.mapper;

import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultResVO;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultSaveVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SurveyGoLogMapper {

    Integer countBySurveyLogAttendByEventIdAndGenderAndAge(@Param("eventId") String eventId, @Param("gender") String gender, @Param("age") Integer age, @Param("isAllGender") boolean isAllGender, @Param("isAllAge") boolean isAllAge);

    int countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCode(@Param("eventId") String eventId, @Param("phoneNumber") String phoneNumber,
                                                                @Param("attendCode") String attendCode, @Param("todayYn") String todayYn);

    List<SurveyLogAttendResultResVO> selectSurveyLogAttendResultByAttendIdAndArEventId(@Param("surveyLogAttendId") String surveyLogAttendId, @Param("arEventId") int arEventId);

    List<SurveyLogAttendResultResVO> selectSurveyLogAttendResultByAttendIdAndArEventId2(@Param("surveyLogAttendIdList") List<String> surveyLogAttendIdList, @Param("arEventId") int arEventId);

    List<SurveyLogAttendResultResVO> selectSurveyLogAttendResultByAttendIdInAndArEventId(@Param("surveyLogAttendIdList") List<String> surveyLogAttendIdList, @Param("arEventId") int arEventId);

    void updateSurveyLogAttend(SurveyLogAttendEntity surveyLogAttendEntity);

    void insertSurveyLogAttendResult(List<SurveyLogAttendResultSaveVO> list);

    @Insert(" INSERT INTO survey_log_subject_category " +
            " (survey_log_attend_id, event_id, ar_event_id, survey_subject_category_id, created_date) " +
            " VALUES ( #{surveyLogAttendId}, #{eventId}, #{arEventId}, #{surveySubjectCategoryId}, now() ) ")
    void insertSurveyLogSubjectCategory(@Param("surveyLogAttendId") String surveyLogAttendId, @Param("eventId") String eventId,
                                        @Param("arEventId") int arEventId, @Param("surveySubjectCategoryId") long surveySubjectCategoryId);

    @Delete( " DELETE FROM survey_log_attend_result WHERE survey_subject_id IN ( SELECT survey_subject_id FROM survey_subject WHERE ar_event_id = #{arEventId} )" )
    void deleteSurveyLogAttendResultByArEventId(@Param("arEventId") int arEventId);

    @Delete( " DELETE FROM survey_log_subject_category WHERE ar_event_id = #{arEventId} " )
    void deleteSurveyLogSubjectCategoryByArEventId(@Param("arEventId") int arEventId);
}
