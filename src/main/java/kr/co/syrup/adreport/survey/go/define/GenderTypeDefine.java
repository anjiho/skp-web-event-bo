package kr.co.syrup.adreport.survey.go.define;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;

/**
 * 성별 정의
 */
public enum GenderTypeDefine {
    A("전체"), M("남"), F("여");

    final String genderTypeStr;

    GenderTypeDefine(String genderTypeStr) {
        this.genderTypeStr = genderTypeStr;
    }

    public static String getGenderTypeStr(String genderType) {
        if (PredicateUtils.isNull(genderType)) {
            return "";
        } else {
            for (GenderTypeDefine define : GenderTypeDefine.values()) {
                if (genderType.equals(define.toString())) {
                    return define.genderTypeStr;
                }
            }
            return null;
        }
    }
}
