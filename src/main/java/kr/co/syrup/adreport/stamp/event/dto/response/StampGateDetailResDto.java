package kr.co.syrup.adreport.stamp.event.dto.response;

import io.swagger.annotations.ApiModelProperty;
import kr.co.syrup.adreport.stamp.event.dto.StampEventBaseDto;
import kr.co.syrup.adreport.stamp.event.dto.StampEventHtmlDto;
import kr.co.syrup.adreport.stamp.event.dto.StampEventMainDto;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@NoArgsConstructor
@Data
public class StampGateDetailResDto implements Serializable {
    private static final long serialVersionUID = 2218206649264736464L;

    @ApiModelProperty(value = "이벤트 Base 정보")
    private StampEventBaseDto eventBaseInfo;

    @ApiModelProperty(value = "스탬프 메인 정보")
    private StampEventMainDto stampMainInfo;

    @ApiModelProperty(value = "스탬프 메인 버튼 정보")
    private EventButtonDto stampButtonInfo;

    @ApiModelProperty(value = "스탬프 Html 정보")
    private List<StampEventHtmlDto> stampHtmlInfo;
}
