package kr.co.syrup.adreport.survey.go.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.entity.SurveyTargetAgeGenderLimitEntity;
import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoMobileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SurveyGoMobileService {

    @Autowired
    SurveyGoMobileMapper surveyGoMobileMapper;

    public int findSurveySubjectCategorySortByIndex(long index) {
        return surveyGoMobileMapper.selectSurveySubjectCategorySortByIndex(index);
    }

    public List<SurveyTargetAgeGenderLimitEntity> findSurveyTargetAgeGenderLimitListByArEventId(int arEventId) {
        return surveyGoMobileMapper.selectSurveyTargetAgeGenderLimitListByArEventId(arEventId);
    }

    public Map<String, Object> findArEventIdFromSurveyLogAttendByIdx(String surveyLogAttendId) {
        return surveyGoMobileMapper.selectArEventIdFromSurveyLogAttendByIdx(surveyLogAttendId);
    }
}
