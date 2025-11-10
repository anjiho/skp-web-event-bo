package kr.co.syrup.adreport.stamp.event.define;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;

// MDN : 핸드폰번호, ATTEND : 참여번호
public enum StampWinningAttendTypeDefine {
    MDN, ATTEND;

    public static boolean isMdn(String stpAttendAuthCondition) {
        if (PredicateUtils.isEqualsStr(stpAttendAuthCondition, MDN.name())) {
            return true;
        }
        return false;
    }

}
