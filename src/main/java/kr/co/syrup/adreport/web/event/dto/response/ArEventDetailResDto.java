package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.survey.go.entity.SurveySubjectCategoryEntity;
import kr.co.syrup.adreport.web.event.dto.request.PhotoContentsListReqDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointSaveResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ArEventDetailResDto implements Serializable {

    private static final long serialVersionUID = -4501942560990759894L;

    private WebEventBaseEntity eventBaseInfo;

    private ArEventResDto arEventInfo;

    private ArEventButtonEntity arEventButtonInfo;

    private List<ArEventObjectEntity> arEventObjectInfo;

    private ArEventLogicalEntity arEventLogicalInfo;

    private List<ArEventScanningImageEntity> arEventScanningImageInfo;

    private List<ArEventWinningResDto> arEventWinningInfo;

    private List<ArEventHtmlResDto> arEventHtmlInfo;

    //서베이고 관련 기능 추가 (SS-20260)
    private List<SurveySubjectResDto> surveySubjectInfo;

    //서베이고 관련 기능 추가 (SS-20260)
    private List<SurveySubjectCategoryEntity> surveySubjectCategoryInfo;

    private PhotoLogicalResDto photoLogicalInfo;

    //포토 컨텐츠 정보
    private PhotoContentsListReqDto photoContentsInfo;

    private OcbPointSaveResDto ocbPointSaveInfo;

    private String previewEventUrlInfo;

    private String staticsViewUrlInfo;

    private String realEventUrlInfo;

}
