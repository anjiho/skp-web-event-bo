package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenderAgeReqDto implements Serializable {

    private static final long serialVersionUID = -1487866783598170615L;

    private String eventId;

    private String gender;

    private Integer age;

    private String surveyLogAttendId;
}
