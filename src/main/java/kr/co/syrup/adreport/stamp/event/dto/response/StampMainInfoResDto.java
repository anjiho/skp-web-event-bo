package kr.co.syrup.adreport.stamp.event.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import kr.co.syrup.adreport.stamp.event.dto.StampEventPanDto;
import kr.co.syrup.adreport.stamp.event.dto.StampEventTrDto;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@NoArgsConstructor
@Data
public class StampMainInfoResDto implements Serializable {
    private static final long serialVersionUID = -5523658331839481016L;

    @ApiModelProperty(value = "스탬프 메인 설정 여부")
    private String stpMainSettingYn;

    @ApiModelProperty(value = "메인이 없는 경우, 이동해야 할 1번 TR 의 하위 이벤트 Id")
    private String stpTrEventId;

}
