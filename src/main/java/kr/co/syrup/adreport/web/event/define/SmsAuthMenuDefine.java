package kr.co.syrup.adreport.web.event.define;

/**
 * SMS 인증 메뉴 종류 정의
 */
public enum SmsAuthMenuDefine {
    MAIN_ATTEND("메인페이지 참여")
    ,WINNING_SEARCH("당첨조회")
    ;

    final String smsAuthMenuStr;

    SmsAuthMenuDefine(String smsAuthMenuStr) {
        this.smsAuthMenuStr = smsAuthMenuStr;
    }
}
