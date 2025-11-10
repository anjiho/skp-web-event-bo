package kr.co.syrup.adreport.web.event.define;

import lombok.Getter;

public enum MethodArgErrorCode {
    NOT_NULL("2001","NOT_NULL"),
    NOT_BLANK("2002", "NOT_BLANK"),
    MIN_VALUE("2003", "최소값보다 커야 합니다."),
    NOT_EMPTY("2004", "문자열 값이 없습니다.")
    ;

    @Getter
    private String code;

    @Getter
    private String description;

    MethodArgErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
