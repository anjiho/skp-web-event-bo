package kr.co.syrup.adreport.survey.go.define;

/*
 *  버튼 유형 정의
 */
public enum ButtonShapeTypeDefine {
    ROUND("둥글게"), SOFT("부드럽게"), ANGLE("각지게");

    final String shapeTypeStr;

    ButtonShapeTypeDefine(String shapeTypeStr) {
        this.shapeTypeStr = shapeTypeStr;
    }
}
