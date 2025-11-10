package kr.co.syrup.adreport.web.event.define;

import kr.co.syrup.adreport.framework.utils.EnumUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;

/**
 * 이벤트 종류 정의
 */
public enum EventLogicalTypeDefine {

    기본형("BASIC"),
    브릿지형("BRIDGE"),
    미션클리어형("MISSION"),
    이미지스캐닝형("SCANNING"),
    드래그앤드랍("DRAG_DROP"),
    서베이고_기본형("SURVEY"),
    퀴즈형("QUIZ"),
    분석형("ANALYSIS"),
    대화형("TALK"),
    포토_기본형("PHOTO_BASIC")
    ;

    private String eventLogicalType;

    EventLogicalTypeDefine(String eventLogicalType) {
        this.eventLogicalType = eventLogicalType;
    }

    public String value() {
        return eventLogicalType;
    }

    public static Boolean isSurveyEvent(String eventLogicalType) {
        if (PredicateUtils.isNotNull(eventLogicalType)) {
            if (PredicateUtils.isEqualsStr(EventLogicalTypeDefine.서베이고_기본형.eventLogicalType, eventLogicalType)
                    || PredicateUtils.isEqualsStr(EventLogicalTypeDefine.퀴즈형.eventLogicalType, eventLogicalType)
                    || PredicateUtils.isEqualsStr(EventLogicalTypeDefine.분석형.eventLogicalType, eventLogicalType)
                    || PredicateUtils.isEqualsStr(EventLogicalTypeDefine.대화형.eventLogicalType, eventLogicalType)
            ) {
                return true;
            }
        }
        return false;
    }
}
