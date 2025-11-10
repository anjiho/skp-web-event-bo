package kr.co.syrup.adreport.web.event.define;

public enum EventLogExposureLimitDefine {
    ID_SORT("getCountEventLogExposureByArEventIdAndObjectSort"),
    ID_SORT_CODE("getCountEventLogExposureByArEventIdAndObjectSortAndAttendCode"),
    ID_SORT_CODE_TODAY("getCountEventLogExposureByArEventIdAndObjectSortAndAttendCodeAndToday"),
    ID_SORT_HOUR("getCountEventLogExposureByArEventIdAndObjectSortCreatedHour"),
    ID_SORT_TODAY("getCountEventLogExposureByArEventIdAndObjectSortAndToday");

    private String methodName;

    EventLogExposureLimitDefine(String methodName) {
        this.methodName = methodName;
    }

    public static String getEventLogExposureLimitMethodName(String enumName) {
        for (EventLogExposureLimitDefine define : EventLogExposureLimitDefine.values()) {
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
