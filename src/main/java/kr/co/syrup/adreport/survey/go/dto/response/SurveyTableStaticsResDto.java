package kr.co.syrup.adreport.survey.go.dto.response;

import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableExampleStaticsResVO;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableSubjectStaticsResVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SurveyTableStaticsResDto implements Serializable {

    private static final long serialVersionUID = -9058377858668020165L;

    private SurveyTableSubjectStaticsResVO subjectInfo;

    private List<SurveyTableExampleStaticsResVO> exampleInfo;
}
