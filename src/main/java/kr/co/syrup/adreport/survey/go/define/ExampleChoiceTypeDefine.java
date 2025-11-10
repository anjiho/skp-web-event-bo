package kr.co.syrup.adreport.survey.go.define;

/*
 *  객관식 유형 정의
 */
public enum ExampleChoiceTypeDefine {
    WIDTH("가로형"),
    HEIGHT("세로형"),
    IMG("이미지형"),
    SCALE("척도형"),
    OX("OX형"),
    TEXT("텍스트형"),
    OPTIONAL("선택형")
    ;

    final String exampleChoiceTypeStr;

    ExampleChoiceTypeDefine(String exampleChoiceTypeStr) {
        this.exampleChoiceTypeStr = exampleChoiceTypeStr;
    }
}
