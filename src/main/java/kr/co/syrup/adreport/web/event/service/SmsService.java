package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.SmsTranTypeDefine;
import kr.co.syrup.adreport.web.event.entity.EmTranEntity;
import kr.co.syrup.adreport.web.event.entity.EmTranMmsEntity;
import kr.co.syrup.adreport.web.event.entity.repository.EmTranEntityRepository;
import kr.co.syrup.adreport.web.event.entity.repository.EmTranMmsEntityRepository;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SmsService {

    @Value("${sms.send.number}")
    private String smsSendNumber;


    @Autowired
    private EmTranEntityRepository emTranEntityRepository;

    @Autowired
    private EmTranMmsEntityRepository emTranMmsEntityRepository;

    @Autowired
    private ArEventMapper arEventMapper;

    @Transactional
    public void saveEmTranSms(String receiverPhoneNumber, String message, String tranDate, Integer msgType) {
        if (PredicateUtils.isNotNull(receiverPhoneNumber)) {
            EmTranEntity emTranEntity = new EmTranEntity();
            //SMS발송일때
            if (msgType == SmsTranTypeDefine.SMS.key()) {
                //발송일 미지정일때
                if (PredicateUtils.isNull(tranDate)) {
                    emTranEntity = EmTranEntity.ofSms(receiverPhoneNumber, smsSendNumber, message);
                }
                //발송일 지정일때
                if (PredicateUtils.isNotNull(tranDate)) {
                    emTranEntity = EmTranEntity.ofSmsAssignTranDate(receiverPhoneNumber, smsSendNumber, message, tranDate);
                }
            }
            //MMS발송일때
            if (msgType == SmsTranTypeDefine.MMS.key()) {
                EmTranMmsEntity mmsEntity = this.saveEmTranMms(message, "");
                //발송일 미지정일때
                if (PredicateUtils.isNull(tranDate)) {
                    emTranEntity = EmTranEntity.ofMMS(receiverPhoneNumber, smsSendNumber, message, mmsEntity.getId());
                }
                //발송일 지정일때
                if (PredicateUtils.isNotNull(tranDate)) {
                    emTranEntity = EmTranEntity.ofMMSAssignTranDate(receiverPhoneNumber, smsSendNumber, message, tranDate, mmsEntity.getId());
                }
            }
            try {
                emTranEntityRepository.save(emTranEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public EmTranMmsEntity saveEmTranMms(String mmsBody, String mmsSubject) {
        if (PredicateUtils.isNotNull(mmsBody)) {
            EmTranMmsEntity saveEntity = EmTranMmsEntity.ofMms(mmsBody, mmsSubject);
            try {
                emTranMmsEntityRepository.saveAndFlush(saveEntity);
                return emTranMmsEntityRepository.findFirstByOrderByIdDesc();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * SMS 발송하기
     * @param smsSendList
     * @param tranDate : 발송일 지정시 발송일 값 입력
     */
    @Transactional
    public void saveEmTranSmsList(List<Map<String, String>>smsSendList, String tranDate) {
        if (!PredicateUtils.isNullList(smsSendList)) {
            List<EmTranEntity> emTranEntityList = new ArrayList<>();

            for (Map<String, String>sms : smsSendList) {
                log.info("sms발송 정보 >> 수신자 번호 :: " + sms.get("receiverPhoneNumber") + " ,내용 :: " + sms.get("message"));
                //발송일 미지정일때
                if (StringUtils.isEmpty(tranDate)) {
                    emTranEntityList.add(EmTranEntity.ofSms(sms.get("receiverPhoneNumber"), smsSendNumber, sms.get("message")));
                }
                //발송일 지정일때
                if (StringUtils.isNotEmpty(tranDate)) {
                    emTranEntityList.add(EmTranEntity.ofSmsAssignTranDate(sms.get("receiverPhoneNumber"), smsSendNumber, sms.get("message"), sms.get("tranDate")));
                }
            }
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            emTranEntityRepository.saveAll(emTranEntityList);
        }
    }

    @Transactional
    public void updateSmsBySendMms(Integer mmsSeq, Integer tranPr) {
        arEventMapper.updateEmTranSmsBySendMms(mmsSeq, tranPr);
    }

}
