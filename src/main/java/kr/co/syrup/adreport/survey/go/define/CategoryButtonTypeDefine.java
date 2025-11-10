package kr.co.syrup.adreport.survey.go.define;

/**
 * 유형 버튼 정의
 */
public enum CategoryButtonTypeDefine {
    RAFFLE("경품추첨"), END("종료");

    final String categoryButtonTypeStr;

    CategoryButtonTypeDefine(String categoryButtonTypeStr) {
        this.categoryButtonTypeStr = categoryButtonTypeStr;
    }
}
