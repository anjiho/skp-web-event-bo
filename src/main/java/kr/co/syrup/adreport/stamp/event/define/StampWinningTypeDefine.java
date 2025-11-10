package kr.co.syrup.adreport.stamp.event.define;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;

//스탬프형 당첨 종류 정의 (N : 당첨없음, RAFFLE : 추첨형, EXCHANGE : 교환형, Y : 당첨있음 )
public enum StampWinningTypeDefine {
    N, RAFFLE, EXCHANGE, Y;

    public static boolean isWinningRaffle(String stampWinningType) {
        if (PredicateUtils.isEqualsStr(stampWinningType, StampWinningTypeDefine.RAFFLE.name())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWinningExchange(String stampWinningType) {
        if (PredicateUtils.isEqualsStr(stampWinningType, StampWinningTypeDefine.EXCHANGE.name())) {
            return true;
        } else {
            return false;
        }
    }
}
