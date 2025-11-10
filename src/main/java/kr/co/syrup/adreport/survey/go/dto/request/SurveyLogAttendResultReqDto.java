package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyLogAttendResultReqDto implements Serializable {

    private static final long serialVersionUID = -3267257992303051739L;

    private String surveyLogAttendId;

    private List<SurveyLogAttendListReqDto> surveyResultInfo;
}
