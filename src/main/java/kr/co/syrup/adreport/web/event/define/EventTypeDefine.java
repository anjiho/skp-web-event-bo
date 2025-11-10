package kr.co.syrup.adreport.web.event.define;

/**
 * AR : AR이벤트, SURVEY : 서베이고, PHOTO : AR포토, STAMP : 스탬프
 */
public enum EventTypeDefine {
    AR, SURVEY, PHOTO, STAMP;

    public static boolean isStampEvent(String eventType) {
        if (eventType.equals(EventTypeDefine.STAMP.name())) {
            return true;
        } else {
            return false;
        }
    }
}
