package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventOuterMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.AttendCodeHistorySearchMapperVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArEventOuterService {

    @Autowired
    private ArEventOuterMapper arEventOuterMapper;

    @Autowired
    private AES256Utils aes256Utils;

    public Date getNowFromDB() {
        return arEventOuterMapper.findNow();
    }

    public void saveCommonLogPv(String eventName, String logKey) {
        try {
            arEventOuterMapper.insertCommonLogPv(eventName, logKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void saveCommonLogPersonAgree(String eventName, String agreeId, String phoneNumber) {
        try {
            arEventOuterMapper.insertCommonLogPersonAgree(eventName.trim(), aes256Utils.encrypt(agreeId.trim()), PredicateUtils.isNotNull(phoneNumber) ? aes256Utils.encrypt(phoneNumber) : "");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean isDuplicateCommonLogPersonAgree(String eventName, String agreeId, String phoneNumber) {
        if (PredicateUtils.isNotNull(agreeId)) {
            agreeId = aes256Utils.encrypt(agreeId);
        }
        if (PredicateUtils.isNotNull(phoneNumber)) {
            phoneNumber = aes256Utils.encrypt(phoneNumber);
        }

        int count = arEventOuterMapper.countByCommonLogPersonAgreeByEventNameAndAgreeId(eventName, agreeId, phoneNumber);
        if (count == 0) {
            return false;
        }
        return true;
    }

    public List<AttendCodeHistorySearchMapperVO> selectAttendCodeHistoryByEventIdAndAttendCode(String eventId, String attendCode) {
        return arEventOuterMapper.selectAttendCodeHistoryByEventIdAndAttendCode(eventId, attendCode);
    }
}
