package kr.co.syrup.adreport.stamp.event.dto.response;

import kr.co.syrup.adreport.stamp.event.dto.request.StampAlimtokReqDto;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventPanTrModel;
import kr.co.syrup.adreport.web.event.dto.request.EventBaseDto;
import kr.co.syrup.adreport.web.event.dto.request.EventButtonDto;
import kr.co.syrup.adreport.web.event.dto.request.EventHtmlDto;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventHtmlResDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventWinningResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventButtonEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StampEventSodarResDto implements Serializable {

    private static final long serialVersionUID = 4752167256491441196L;

    private WebEventBaseEntity eventBaseInfo;
    //AR 이벤트 버튼 정보
    private ArEventButtonEntity arEventButtonInfo;
    //이벤트 당첨정보, 당첨버튼정보 저장
    private List<ArEventWinningResDto> arEventWinningInfo;

    private List<ArEventHtmlResDto> arEventHtmlInfo;

    private StampEventMainModel stampMainInfo;

    private StampEventPanModel stampPanInfo;

    private List<ArEventHtmlResDto> stampPanHtmlInfo;

    private List<StampEventPanTrModel> stampTrInfo;

    private StampAlimtokInfoResDto stampAlimtokInfo;

    private String previewEventUrlInfo;

    private String staticsViewUrlInfo;

    private String realEventUrlInfo;

    private String stampMainUrlInfo;

    private String stampPanUrlInfo;

    private String stampFirstEventUrlInfo;

    private String stampFirstEventPreviewUrlInfo;
}
