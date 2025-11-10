package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyResultSaveMobileReqDto implements Serializable {

    private static final long serialVersionUID = 9111085701186969195L;

    private String eventId;
    private String surveyLogAttendId;
    private List<SurveyAnswerInfoDto> answerList;

    private String traceNo;
}
