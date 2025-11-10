package kr.co.syrup.adreport.survey.go.define;

/**
 * 보기 가중치 유형 - 퀴즈형, 분석형 한정
 */
public enum ExampleWeightTypeDefine {
    FREE("자유입력"), ASSIGN("숫자지정");

    final String exampleWeightTypeStr;

    ExampleWeightTypeDefine(String exampleWeightTypeStr) {
        this.exampleWeightTypeStr = exampleWeightTypeStr;
    }
}
