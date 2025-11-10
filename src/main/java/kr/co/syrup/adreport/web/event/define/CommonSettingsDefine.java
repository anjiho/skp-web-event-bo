package kr.co.syrup.adreport.web.event.define;

public enum CommonSettingsDefine {

    //서베이고 로우 엑셀 다운로드 제한 ( value 설명 > -1 : 다운로드 실패, 0 : 다운로드 대기, 1 : 다운로드 실행중, 2 : 다운로드 완료 )
    SURVEY_EXCEL_ROW_LIMIT,
    //서베이고 응답 엑셀 다운로드 제한
    SURVEY_EXCEL_ANSWER_LIMIT,

    //당첨결과 엑셀 전송 제한
    GIVE_AWAY_RESULT_LIMIT,

    //응모 당첨결과 엑셀 전송 제한
    SUB_WINNING_RESULT_LIMIT,

    //스탬프 적립 통계 전송 제한
    STAMP_ACC_EXCEL_LIMIT

    ;
}
