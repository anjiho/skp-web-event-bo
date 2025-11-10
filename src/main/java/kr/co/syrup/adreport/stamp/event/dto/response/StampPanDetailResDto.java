package kr.co.syrup.adreport.stamp.event.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import kr.co.syrup.adreport.stamp.event.dto.*;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@NoArgsConstructor
@Data
public class StampPanDetailResDto extends StampGateDetailResDto {

    private static final long serialVersionUID = 5426672632039196062L;

    @ApiModelProperty(value = "스탬프 판 정보")
    private StampEventPanDto stampPanInfo;

    @ApiModelProperty(value = "스탬프 TR 리스트")
    private List<StampEventTrDto> stampTrList;

    @ApiModelProperty(value = "스탬프 전체 카운트")
    private int trTotalCnt;

    private int trStampAfterCnt;

    private int trStampAfterNextCnt;

    @ApiModelProperty(value = "참여순번형태의 당첨맵핑인 경우, 다음 당첨까지 남은 스탬프 카운트")
    private int nextWinningAttendRemainingCnt;

    @JsonIgnore
    private EventButtonDto stampButtonInfo;

    public StampPanDetailResDto (StampGateDetailResDto gateDetail){
        this.setEventBaseInfo(gateDetail.getEventBaseInfo());
        this.setStampMainInfo(gateDetail.getStampMainInfo());
//        this.stampButtonInfo = gateDetail.getStampButtonInfo();
        this.setStampHtmlInfo(gateDetail.getStampHtmlInfo());
    }

}
