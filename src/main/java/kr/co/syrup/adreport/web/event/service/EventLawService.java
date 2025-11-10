package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.mybatis.mapper.EventLawMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLawInfoVO;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class EventLawService {

    @Autowired
    private EventLawMapper eventLawMapper;

    public List<EventLawInfoVO> getEventLawInfoListByLawType(String lawType) {
        if (PredicateUtils.isNotNull(lawType)) {
            if (PredicateUtils.isEqualsStr(lawType, "ALL")) {
                lawType = null;
            }
            return eventLawMapper.selectEventLawInfoListByType(lawType);
        }
        return new ArrayList<>();
    }

    @Transactional
    public Integer saveEventLawInfo(String lawType, String contents, Date startDate, Date endDate) {
        String version = this.makeEventLawLastVersion(lawType);
        eventLawMapper.insertEventLawInfo(lawType, contents, startDate, endDate, SodarMemberSession.get().getName(), version);
        return eventLawMapper.selectLastIdx();
    }

    @Transactional
    public void updateEventLawInfo(Integer idx, String lawType, String contents, Date startDate, Date endDate) {
        eventLawMapper.updateEventLawInfo(idx, lawType, contents, startDate, endDate, SodarMemberSession.get().getName());
    }

    @Transactional
    public void deleteEventLawInfo(Integer idx) {
        if (PredicateUtils.isNotNull(idx)) {
            eventLawMapper.deleteEventLawInfoById(idx);
        }
    }

    private String makeEventLawLastVersion(String lawType) {
        if (PredicateUtils.isNotNull(lawType)) {
            Double lastVersion = eventLawMapper.selectLastVersionByLawType(lawType);
            if (PredicateUtils.isNotNull(lastVersion)) {
                 return String.format("%.1f", (lastVersion + 0.1));
            }
        }
        return null;
    }
}
