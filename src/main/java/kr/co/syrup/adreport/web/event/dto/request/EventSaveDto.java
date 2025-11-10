package kr.co.syrup.adreport.web.event.dto.request;

import kr.co.syrup.adreport.survey.go.dto.request.SurveySubjectCategoryReqDto;
import kr.co.syrup.adreport.survey.go.dto.request.SurveySubjectSodarReqDto;
import kr.co.syrup.adreport.web.event.dto.request.api.OcbPointSaveReqDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class EventSaveDto implements Serializable {

    private static final long serialVersionUID = -2212928561492468421L;
    //이벤트 기본 정보
    private EventBaseDto eventBaseInfo;

    //AR 이벤트 설정 공통 정보
    private EventDto arEventInfo;

    //AR 이벤트 버튼 정보
    private EventButtonDto arEventButtonInfo;

    //AR 구동 정보(기본형, 브릿지형, 미션클리어판, 드래그앤드랍)
    private List<EventObjectDto> arEventObjectInfo;

    //AR 구동정보 공통
    private EventLogicalDto arEventLogicalInfo;

    //이미지스캐닝 정보(AR 구동정보가 이미지스캐닝일때만 저장)
    private List<EvenScanningImageDto> arEventScanningImageInfo;

    //이벤트 당첨정보, 당첨버튼정보 저장
    private List<EventWinningDto> arEventWinningInfo;

    private List<EventHtmlDto> arEventHtmlInfo;

    //문항, 보기 정보
    private List<SurveySubjectSodarReqDto> surveySubjectInfo;

    //유형 정보
    private List<SurveySubjectCategoryReqDto> surveySubjectCategoryInfo;

    //AR포토 로지컬 정보
    private PhotoLogicalReqDto photoLogicalInfo;

    //포토 컨텐츠 정보
    private PhotoContentsListReqDto photoContentsInfo;

    //OCB 포인트 적립정보
    private OcbPointSaveReqDto ocbPointSaveInfo;

    private String traceNo;
}
