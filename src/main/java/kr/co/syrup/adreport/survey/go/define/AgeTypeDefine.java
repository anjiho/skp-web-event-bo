package kr.co.syrup.adreport.survey.go.define;

import kr.co.syrup.adreport.web.event.define.EventLogExposureLimitDefine;

public enum AgeTypeDefine {

    ALL(0, "전체"), //전체
    TWENTY_UNDER(1, "20대미만"),//20대미만
    TWENTY(2, "20대"), //20대
    THIRTY(3, "30대"), //30대
    FOURTY(4, "40대"), //40대
    FIFTY(5, "50대"), //50대
    SIXTY_MORE(6, "60대이상");//60대이상

    final int ageType;

    final String ageTypeStr;

    AgeTypeDefine(int ageType, String ageTypeStr) {
        this.ageType = ageType;
        this.ageTypeStr = ageTypeStr;
    }

    public static String getAgeTypeStr(Integer ageType) {
        if (ageType == null) {
            return "";
        } else {
            for (AgeTypeDefine define : AgeTypeDefine.values()) {
                if (define.ageType == ageType) {
                    return define.ageTypeStr;
                }
            }
            return null;
        }
    }

    public String getAgeStr() {
        return ageTypeStr;
    }
}
