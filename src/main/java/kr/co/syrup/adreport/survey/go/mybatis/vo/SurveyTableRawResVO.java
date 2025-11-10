package kr.co.syrup.adreport.survey.go.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SurveyTableRawResVO implements Serializable {

    private static final long serialVersionUID = -7631325389211206129L;

    private String surveyLogAttendId;

    private Integer arEventId;

    private String attendStartDate;

    private String gender;

    private Integer age;

    private Integer giveAwayId;

    private String productName;

    private String attendEndDate;
}
