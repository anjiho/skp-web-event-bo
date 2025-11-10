package kr.co.syrup.adreport.survey.go.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.entity.SurveyLogAttendEntity;
import kr.co.syrup.adreport.survey.go.entity.repository.SurveyLogAttendEntityRepository;
import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoLogMapper;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultSaveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class SurveyGoLogService {

    @Autowired
    private SurveyGoLogMapper surveyGoLogMapper;

    @Autowired
    private SurveyLogAttendEntityRepository surveyLogAttendEntityRepository;

    public int countSurveyLogAttendByEventId(String eventId, String gender, Integer age, boolean isAllGender, boolean isAllAge) {
        return surveyGoLogMapper.countBySurveyLogAttendByEventIdAndGenderAndAge(eventId, gender, age, isAllGender, isAllAge);
    }

    public int countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCodeAndTodayYn(String eventId, String phoneNumber, String attendCode, String todayYn) {
        return surveyGoLogMapper.countSurveyLogAttendByEventIdAndPhoneNumberOrAttendCode(eventId, phoneNumber, attendCode, todayYn);
    }

    @Transactional
    public void saveSurveyLogAttend(String surveyLogAttendId, String eventId, int arEventId, String phoneNumber, String attendCode) {
        SurveyLogAttendEntity entity = new SurveyLogAttendEntity();
        entity.setSurveyLogAttendId(surveyLogAttendId);
        entity.setEventId(eventId);
        entity.setArEventId(arEventId);
        entity.setPhoneNumber(phoneNumber);
        entity.setAttendCode(attendCode);

        try {
            surveyLogAttendEntityRepository.save(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateSurveyLogAttend(SurveyLogAttendEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoLogMapper.updateSurveyLogAttend(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveSurveyLogAttendResultList(List<SurveyLogAttendResultSaveVO> saveList) {
        try {
            surveyGoLogMapper.insertSurveyLogAttendResult(saveList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveSurveyLogSubjectCategory(String surveyLogAttendId, String eventId, int arEventId, long surveySubjectCategoryId) {
        try {
            surveyGoLogMapper.insertSurveyLogSubjectCategory(surveyLogAttendId, eventId, arEventId, surveySubjectCategoryId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyLogAttendByArEventId(int arEventId) {
        try {
            surveyLogAttendEntityRepository.deleteByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyLogAttendResultByArEventId(int arEventId){
        try {
            surveyGoLogMapper.deleteSurveyLogAttendResultByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyLogSubjectCategoryByArEventId(int arEventId) {
        try {
            surveyGoLogMapper.deleteSurveyLogSubjectCategoryByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
