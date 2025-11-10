package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.entity.ArEventButtonEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventHtmlEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArEventAttendConditionResDto implements Serializable {

    private static final long serialVersionUID = 1618474456744078545L;

    private WebEventBaseEntity eventBaseInfo;

    private ArEventEntity arEventInfo;

    private ArEventButtonEntity arEventButtonInfo;

    private List<ArEventHtmlEntity> eventHtmlInfo;

    private String attendCode;

    private Integer diffServiceEndDateTodayCount;
}
