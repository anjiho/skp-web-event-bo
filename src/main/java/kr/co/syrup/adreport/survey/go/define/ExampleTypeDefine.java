package kr.co.syrup.adreport.survey.go.define;

/*
 *  보기 유형 정의
 */
public enum ExampleTypeDefine {
    CHOICE("객관식"), QUESTION("주관식");

    final String exampleTypeStr;

    ExampleTypeDefine(String exampleTypeStr) {
        this.exampleTypeStr = exampleTypeStr;
    }
}
