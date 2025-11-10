package kr.co.syrup.adreport.survey.go.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveySubjectPopupImageReqDto implements Serializable {

    private static final long serialVersionUID = 1655320654061905775L;

    private Long surveySubjectPopupImageId;

    // 이미지 url
    private String popupImgUrl;

    // 순서
    private Integer sort;
}
