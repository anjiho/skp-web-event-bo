package kr.co.syrup.adreport.survey.go.define;

/*
 * 보기 유형 - 척도형 한정
 */
public enum ScaleViewTypeDefine {
    BASIC("기본"), EXPRESSION("표정"), IMG("이미지 삽입"), STAR("별점");

    final String scaleViewTypeStr;

    ScaleViewTypeDefine(String scaleViewTypeStr) {
        this.scaleViewTypeStr = scaleViewTypeStr;
    }
}
