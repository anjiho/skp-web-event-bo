package kr.co.syrup.adreport.framework.common;

import kr.co.syrup.adreport.framework.utils.EnumUtils;

public enum ResultCodeEnum {

    SYSTEM_ERROR("999", "오류가 발생하였습니다. 잠시후 재시도 부탁드립니다.") // System Code
    , SUCCESS_OK("200", "success") // 성공
    , ACCESS_DENIED("403", "accessDenied") //
    , PAGE_NOT_FOUND("404", "PAGE NOT FOUND") //

    , CUSTOM_ERROR_MOBILE_SURVEY_PROCESS_ERROR("601", "설문결과 처리 중 에러")
    ,CUSTOM_ERROR_LIMIT_SURVEY_GENDER_AGE("602", "설문참여 참여 제한 에러(성별/연령대)")
    ,CUSTOM_ERROR_LIMIT_EXCEL_DOWNLOAD("603", "서베이고 엑셀 다운로드 제한 에러")

    ,CUSTOM_ERROR_SODAR_SAVE_ERROR("700", "SODAR 저장 시 에러 발생")
    ,CUSTOM_ERROR_XSS("701", "XSS 에러 발생")
    ,CUSTOM_ERROR_GET_DATA_IS_NULL("702", "가져온 데이터가 없음")

    ,CUSTOM_ERROR_WEB_EVENT_BASE_NULL("800", "WEB_EVENT_BASE 없음")
    ,CUSTOM_ERROR_EVENT_ID_NULL("801", "EVENT_ID 없음")
    ,CUSTOM_ERROR_GET_NULL("802", "값을 가져올때 NULL")
    ,CUSTOM_ERROR_PARAM_ERROR("803", "필수 파라미터가 없음")
    ,CUSTOM_ERROR_NOT_SERVICE_STATUS("806", "서비스중인 상태가 아님")
    ,CUSTOM_ERROR_NOT_ATTEND_CODE("808", "참여 코드가 없음")
    ,CUSTOM_ERROR_USED_ATTEND_CODE("809", "사용된 참여코드")
    ,CUSTOM_ERROR_ATTEND_TOTAL_LIMIT_COUNT("814", "참여코드 등록할수 있는개수가 초과(100,000)")
    ,CUSTOM_ERROR_LIMIT_WINNING_COUNT("816", "당첨수에 제한되었습니다.")
    ,CUSTOM_ERROR_ALREADY_RECEIPT_GIVE_AWAY("818", "이미 수령이 완료된 경품")
    ,CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE("819", "참여코드 참여회수 초과")
    ,CUSTOM_ERROR_GIFTICON_SEND_ERROR("821", "기프티콘 발송 에러")
    ,CUSTOM_ERROR_NULL_TEST_SMS_RESULT("822", "test sms 발송 결과값이 없음")
    ,CUSTOM_ERROR_MEMBER_BIRTH_LENGTH("823", "경품저장 데이터(생년월일 자리수 8자리) 저장 시 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE("824", "경품저장 데이터 저장 시 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME("825", "경품저장 데이터(이름) 저장 시 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER("826", "경품저장 데이터(핸드폰번호) 저장 시 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS("827", "경품저장 데이터(주소) 저장 시 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE_PASSWORD("828", "경품저장 데이터(비밀번호) 저장 시 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15("829", "경품저장 데이터(만15세) 저장 시 에러")
    ,CUSTOM_ERROR_GIFTICON_RECEIVE_COUNT("830", "기프티콘 발급 개수 초과 에러")
    ,CUSTOM_ERROR_GIVE_AWAY_SQL_ERROR("831", "경품저장 저장 시 SQL 에러")
    ,CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER("832", "경품저장 데이터(생년월일 자리수 8자리)에 특수문자가 포함 되어있는 에러")
    ,CUSTOM_ERROR_EVENT_WINNING_BUTTON_NULL("833", "경품 버튼 데이터 에러")
    ,CUSTOM_EVENT_LOG_WINNING_ID_DUPLICATE("834", "당첨 저장시 당첨 로그 아이디 중복")
    ,CUSTOM_EVENT_NFT_BANNER_REG_LIMIT("835", "이벤트 NFT 배너 저장가능한 개수 에러")
    ,CUSTOM_ERROR_PASSWORD_REGULAR_EXPRESSION("836", "경품저장 비밀번호 정규식 에러")
    ,CUSTOM_ERROR_DUPLICATE_NFT_TOKEN("837", "NFT 토큰 정보 중복 에러")
    ,CUSTOM_ERROR_DUPLICATE_NFT_WALLET_ADDRESS("838", "NFT 지갑 주소 중복 에러")
    ,CUSTOM_ERROR_NULL_NFT_WALLET("839", "NFT 주소정보가 없음")
    ,CUSTOM_ERROR_NULL_NFT_REPOSITORY("840", "NFT 정보가 없음")
    ,CUSTOM_ERROR_NULL_NFT_TOKEN("841", "NFT 토큰이 없음")
    ,CUSTOM_ERROR_USED_NFT_TOKEN("842", "이미 사용된 NFT 토큰")
    ,CUSTOM_ERROR_STATUS_TRANS_NFT_REPOSITORY("843", "NFT가 이전이 가능한 상태가 아님")
    ,CUSTOM_ERROR_NFT_TRANSFER("844", "NFT이전시 에러가 발생")
    ,CUSTOM_ERROR_NFT_API_TRANSFER("845", "NFT이전시 API 에러가 발생")
    ,CUSTOM_ERROR_SMS_SEND("846", "SMS 발송 에러")
    ,CUSTOM_ERROR_EVENT_ATTEND_EXPIRED_DAY("847", "이벤트 참여 가능 기간만료")
    ,CUSTOM_ERROR_IMPOSSIBLE_EVENT_MODIFY("848", "이벤트가 수정가능한 상태가 아님")
    ,CUSTOM_ERROR_COMMON_LOG_PERSON_AGREE_ID_DUPLICATE("849", "공통 개인정보 활용 동의 저장시 아이디 중복 에러")
    ,CUSTOM_ERROR_LIMIT_SMS_AUTH_SEND("858", "SMS 인증 발송 개수 초과 (5회)")
    ,CUSTOM_ERROR_SMS_AUTH_CODE("850", "SMS 인증 코드 에러")
    ,CUSTOM_ERROR_EXPIRED_SMS_AUTH_TIME("851", "SMS 인증 시간만료(3분초과)")
    ,CUSTOM_ERROR_NULL_SURVEY_SUBJECT_CATEGORY_ID("852", "surveySubjectCategoryId 가 없습니다.")
    ,CUSTOM_ERROR_ATTEND_COUNT_LIMIT_SURVEY_TARGET("853", "목포달성 참여회수 초과")
    ,CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER("854", "전화번호 참여회수 초과")
    ,CUSTOM_ERROR_NO_ATTEND_TARGET("855", "성/연령별 참여조건이 아닙니다.")
    ,CUSTOM_ERROR_EXPIRED_SURVEY_LOG_ID("856", "이미 사용완료된 survey log id 입니다.")
    ,CUSTOM_ERROR_NOT_ISSUED_SURVEY_LOG_ID("857", "발급되지 않은 survey log id 입니다.")
    ,CUSTOM_ERROR_OCB_MBR_ID_NULL("859", "OK캐시백 MBRID 가 없습니다.")
    ,CUSTOM_ERROR_OCB_POINT_SAVE_STATUS("860", "OK캐시백 포인트 적립 가능한 상태가 아닙니다.")
    ,CUSTOM_ERROR_OCB_POINT_LIMIT_COUNT("861", "OK캐시백 포인트 적립 개수 제한")
    ,CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY("862", "OK캐시백 MBRID 기준 포인트 적립이 이력이 있음(1회초과)")
    ,CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY("863", "경품 저장 데이터(기타) 저장 시 에러")
    ,CUSTOM_ERROR_OCB_SESSION_NULL("864", "OCB 세션 정보 NULL 에러")
    ,CUSTOM_ERROR_OCB_SAVE_INFO_NULL("865", "OCB 포인트 정보 NULL 에러")
    ,CUSTOM_ERROR_PID_NOT_LOCATION_MATCH("866", "PID가 현재 위치에 일치하지 않음")
    ,CUSTOM_ERROR_STAMP_TR_TYPE("867", "스탬프 TR 유형 에러")
    ,CUSTOM_ERROR_STAMP_TR_NULL("868", "스탬프 TR 정보 없음")
    ,CUSTOM_ERROR_STAMP_GIVE_AWAY_IS_NULL("869", "스탬프 경품 저장시 이미 저장된 경품값이 없음")
    ,CUSTOM_ERROR_DUPLICATE_VALUE("870", "중복된 값이 있음")

    , PARAMETER_ERROR("901", "parameter error") // 시스템 오류 사용자 노티 안함 900번대

    , PARAMETER_ERROR_MDNISNOTNULL("902","광고주 MDN을 입력해주세요") // MDN 파라미터 오류
    , PARAMETER_ERROR_MARKETINGIDISNOTNULL("903","마케팅 ID를 입력해주세요") // 마케팅 ID 파라미터 오류
    , PARAMETER_ERROR_MIDISNOTNULL("904","광고주 MID를 입력해주세요") // MID 파라미터 오류
    , PARAMETER_ERROR_REPORTDATEISNOTNULL("905","조회기준월(yyyyMM)을 입력해주세요") //
    , PARAMETER_ERROR_NOSEARCHDATA("906","조회할 수 없는 계약입니다") //

    ,JSON_PARSE_EXCEPTION_ERROR("1001", "JSON PARSE EXCEPTION ERROR")
    ,STRING_ENCODING_EXCEPTION_ERROR("1002", "JSON PARSE EXCEPTION ERROR")


    , AD_APPLY_ERROR("1101","광고가 신청되지 않았습니다. 잠시후 재신청 부탁드립니다.") // 광고 신청 오류

    , IP_ACCESS_DENY_ERROR("1102", "IP 접근 권한이 없습니다.")

    , REST_API_CALL_ERROR("1201", "API 통신시 알 수 없는 에러가 발생되었습니다.")    //API통신 에러

    , DUPLICATE_UNIQUE_KEY("1202", "유니크 키 값 중복")

    , SODAR_MEMBER_IS_NULL("1203", "소다 멤버 세션 확인 에러")


    // client에서 무시하는 오류 코드 9000번대
    , IGNORE_ERROR("9000", "systemerror") //

    // AES
    , ERROR_AESCODEC("991","AES encode/decode Error")

    //사용자 노출 에러 메시지
    , USER_NOTI_ERROR("1000","")

    //end
    ;

    private String code;
    private String desc;

    ResultCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String toString() {
        return String.valueOf(code);
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    public static ResultCodeEnum getByCode(String code) {
        return EnumUtils.enumValueOf(ResultCodeEnum.class, code);
    }

}
