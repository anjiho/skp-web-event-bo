package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SurveyConnectionStaticsMapperVO {

    private String gender;

    private Integer age;

    private String attendCnt;

    private String successCnt;

    private Integer cnt;

}
