package kr.co.syrup.adreport.web.event.mybatis.vo;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.EventLogPvKeyDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventLogPvReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventLogPvVO implements Serializable {

    private static final long serialVersionUID = 7694253211441824953L;

    private String eventId;

    private Integer arEventId;

    private String pageId;

    private String actionId;

    private String order;

    private String type;

    private String code;

    private String browserVersion;

    private String br;

    private Date createdDate;

    public static EventLogPvVO saveOf(EventLogPvReqDto reqDto, EventLogPvKeyDefine define) {
        EventLogPvVO eventLogPvVO = new EventLogPvVO();
        eventLogPvVO.setEventId(reqDto.getEventId());
        eventLogPvVO.setArEventId(reqDto.getArEventId());
        eventLogPvVO.setPageId(define.getPageId());
        eventLogPvVO.setActionId(define.getActionId());
        eventLogPvVO.setOrder(define.getIsOrder() ? reqDto.getOrder() : "");
        eventLogPvVO.setType(define.getIsType() ? reqDto.getType() : "");
        eventLogPvVO.setCode(define.getIsCode() ? reqDto.getCode() : "");
        eventLogPvVO.setBrowserVersion(PredicateUtils.isNotNull(reqDto.getBrowserVersion()) ? reqDto.getBrowserVersion() : "");
        eventLogPvVO.setBr(PredicateUtils.isNotNull(reqDto.getBr()) ? reqDto.getBr() : "");
        return eventLogPvVO;
    }
}
