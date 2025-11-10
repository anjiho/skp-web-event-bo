package kr.co.syrup.adreport.stamp.event.dto.request;

import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanTrModel;
import kr.co.syrup.adreport.web.event.dto.request.EventBaseDto;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import kr.co.syrup.adreport.web.event.dto.request.EventHtmlDto;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class StampEventSaveReqDto implements Serializable {

    private static final long serialVersionUID = -8790713173980830366L;
    //이벤트 기본 정보
    private EventBaseDto eventBaseInfo;
    //AR 이벤트 버튼 정보
    private EventButtonDto arEventButtonInfo;
    //이벤트 당첨정보, 당첨버튼정보 저장
    private List<EventWinningDto> arEventWinningInfo;

    private List<EventHtmlDto> arEventHtmlInfo;

    private StampEventMainModel stampMainInfo;

    private StampEventPanModel stampPanInfo;

    private List<EventHtmlDto> stampPanHtmlInfo;

    private List<StampEventPanTrModel> stampTrInfo;

    private StampAlimtokReqDto stampAlimtokInfo;
}
