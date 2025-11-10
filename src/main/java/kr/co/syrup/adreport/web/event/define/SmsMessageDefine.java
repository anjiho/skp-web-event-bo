package kr.co.syrup.adreport.web.event.define;

public enum SmsMessageDefine {

    SUBSCRIPTION("Play AR {productName} 경품응모결과 당첨되었습니다. 당첨내역보기에서 확인가능합니다.(확인) {targetUrl}"),
    NEW_PASS("Play AR 당첨조회 임시비밀번호 {newPass}"),
    MAIN_SMS_AUTH_AR("[PlayAR] 휴대폰번호 확인을 위해 인증번호 {smsCode} 를 입력해주세요."),
    MAIN_SMS_AUTH_SURVEY("[surveyGO] 휴대폰번호 확인을 위해 인증번호 {smsCode} 를 입력해주세요."),
    WINNING_SEARCH_AUTH("당첨정보 조회 SMS 인증 코드 {smsCode}")
    ;

    public String smsContents;

    SmsMessageDefine(String smsContents) {
        this.smsContents = smsContents;
    }

    public String content() {
        return this.smsContents;
    }

}
