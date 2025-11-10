package kr.co.syrup.adreport.web.event.define;

public enum EventLogWinningLimitDefine {
    MAX("totalMax"),
    ID_CODE("getCountEventWinningLogByArEventIdAndAttendCodeNotFail"),
    ID_CODE_TODAY("getCountEventWinningLogByEventIdAndAttendCodeAndTodayNotFail"),
    ID_SORT_TODAY_HOUR("getCountEventWinningLogByArEventIdAndEventWinningSortAndDayAndHourNotFail"),
    ID_SORT_TODAY ("getCountEventWinningLogByArEventIdAndEventWinningSortAndTodayAndNotFail"),
    ID_SORT("getCountEventWinningLogByArEventIdAndEventWinningSortNotFail"),
    ID_WINNINGID_CODE("getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeNotFail"),
    ID_WINNINGID_CODE_TODAY("getCountEventWinningLogByArEventIdAndArEventWinningIdAndAttendCodeAndTodayNotFail"),
    ID_MDN(""),
    ID_MDN_TODAY(""),
    ID_WINNINGID_MDN(""),
    ID_WINNINGID_MDN_TODAY(""),
    ;

    private String methodName;

    EventLogWinningLimitDefine(String methodName) {
        this.methodName = methodName;
    }

    public static String getEventLogWinningLimitMethodName(String enumName) {
        for (EventLogWinningLimitDefine define : EventLogWinningLimitDefine.values()) {
            if (define.name().equals(enumName)) {
                return define.getMethodName();
            }
        }
        return null;
    }

    public String getMethodName() {
        return methodName;
    }
}
