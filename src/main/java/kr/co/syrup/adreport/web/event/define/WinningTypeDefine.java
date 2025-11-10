package kr.co.syrup.adreport.web.event.define;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 당첨 종류 정의
 */
public enum WinningTypeDefine {

    꽝("FAIL"),
    기프티콘("GIFTICON"),
    기타("ETC"),
    NFT("NFT"),
    NFT쿠폰("NFTCP"),
    OCB쿠폰("OCBCP"),
    OCB포인트("OCBPT"),
    스탬프("STAMP")
    ;

    private String winningType;

    WinningTypeDefine(String winningType) {
        this.winningType = winningType;
    }

    public String code() {
        return winningType;
    }

    public static List<String> getWinningTypeListNotInFail() {
        return Stream.of(WinningTypeDefine.values())
                                .map(WinningTypeDefine::code)
                                .filter(code -> !PredicateUtils.isEqualsStr(code, WinningTypeDefine.꽝.code()))
                                .collect(Collectors.toList());
    }

    public static boolean isEtcWinning(String winningType) {
        if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.기타.code())) {
            return true;
        } else {
            return false;
        }
    }
}
