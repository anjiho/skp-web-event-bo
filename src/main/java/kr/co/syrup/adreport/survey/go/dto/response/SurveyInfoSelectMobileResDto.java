package kr.co.syrup.adreport.survey.go.dto.response;

import kr.co.syrup.adreport.survey.go.dto.request.SurveySubjectInfoMobileDto;
import kr.co.syrup.adreport.survey.go.dto.request.SurveySubjectSodarReqDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyInfoSelectMobileResDto implements Serializable {

    private static final long serialVersionUID = 7742222107309541697L;

    private WebEventBaseEntity webEventBaseInfo;

    //이벤트 설정 공통 정보
    private ArEventEntity arEventInfo;

    //문항, 보기 정보
    private List<SurveySubjectInfoMobileDto> surveySubjectInfo;


}
