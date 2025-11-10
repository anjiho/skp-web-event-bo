package kr.co.syrup.adreport.survey.go.dto.request;

import kr.co.syrup.adreport.web.event.dto.request.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SodarSaveReqDto implements Serializable {

    private static final long serialVersionUID = -123683991101114255L;

    //문항, 보기 정보
    private List<SurveySubjectSodarReqDto> surveySubjectInfo;

    private String traceNo;

}
