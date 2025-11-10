package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampSortAttendSortYnResVO;
import kr.co.syrup.adreport.web.event.entity.ArEventAttendTimeEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventBaseJoinArEventJoinEventButtonVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ArEventGatePageResDto implements Serializable {

    private static final long serialVersionUID = 2051914416257421544L;
    
    EventBaseJoinArEventJoinEventButtonVO eventBaseInfo;

    private List<ArEventHtmlResDto> eventHtmlInfo;

    private String attendCode;

    private Integer diffServiceEndDateTodayCount;

    private List<ArEventAttendTimeEntity> attendTimeInfo;

    private List<String>stampEventIdInfo;

    private StampSortAttendSortYnResVO stampTrInfo;

}
