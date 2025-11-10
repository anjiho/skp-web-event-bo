package kr.co.syrup.adreport.survey.go.mybatis.mapper;

import kr.co.syrup.adreport.survey.go.entity.SurveyExampleQuestionEntity;
import kr.co.syrup.adreport.survey.go.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.mybatis.vo.HourlyMapperVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SurveyGoStaticsMapper {

    List<SurveyTableSubjectStaticsResVO> selectSurveyTableSubjectStatics(@Param("eventId") String eventId);

    List<SurveyTableExampleStaticsResVO> selectSurveyTableExampleStatics(@Param("surveySubjectId") long surveySubjectId);

    int countSurveyTableExampleEtc(@Param("surveySubjectId") long surveySubjectId);

    int countSurveyQuizQuestionWrong(@Param("surveySubjectId") long surveySubjectId);

    List<SurveyTableCategoryStaticsResVO> selectSurveySubjectCategoryStaticsByQuiz(@Param("arEventId") int arEventId, @Param("eventLogicalType") String eventLogicalType);

    List<SurveyTableRawResVO> selectSurveyTableRawStatics(@Param("eventId") String eventId);

    List<SurveyTableRawResVO> selectSurveyTableRawStatics2(@Param("eventId") String eventId, @Param("start") int start, @Param("limitCount") int limitCount);

    SurveyTableRawResVO selectSurveyTableRawStaticsByArEventIdAndSurveyLogAttendId(@Param("arEventId") int arEventId, @Param("surveyLogAttendId") String surveyLogAttendId);

    List<SurveyTableRawTitleResVO> selectSurveyTableRawTitleList(@Param("arEventId") int arEventId);

    List<SurveyTableRawAnswerResVO> selectSurveyTableRawAnswerList(@Param("arEventId") int arEventId);

    String selectSurveyAttendStatics(@Param("eventId") String eventId, @Param("searchDate") String searchDate, @Param("isSubmit") Boolean isSubmit);

    List<HourlyMapperVO> selectHourlySurveyAttendStatics(@Param("eventId") String eventId, @Param("searchDate") String searchDate, @Param("isSubmit") Boolean isSubmit);

    List<SurveyTableExampleStaticsResVO> selectSurveyExampleQuestionAnswerStaticsByQuiz(@Param("surveySubjectId") long surveySubjectId);

    @Select("SELECT count(*) FROM survey_log_attend WHERE event_id = #{eventId} AND is_submit = true")
    int countSurveyLogAttendByEventId(@Param("eventId") String eventId);

}
