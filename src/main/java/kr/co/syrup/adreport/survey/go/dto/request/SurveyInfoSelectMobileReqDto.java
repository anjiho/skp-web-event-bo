package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyInfoSelectMobileReqDto implements Serializable {

    private static final long serialVersionUID = 7842831420065705180L;

    private String eventId;

    private String surveyLogAttendId;

    private String traceNo;

}
