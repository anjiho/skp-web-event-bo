package kr.co.syrup.adreport.web.event.define;

import lombok.extern.slf4j.Slf4j;

public enum ErrorCodeDefine {

    CONNECTION_TIMEOUT(522, "Connection Time out Exception"),
    FILE_NOT_FOUND(523, "File Not Found Exception"),
    IOE_ERROR(524, "IOE Exception"),

    CUSTOM_ERROR_MOBEIL_SURBEY_PROCESS_ERROR(601, "설문결과 처리 중 에러"),
    CUSTOM_ERROR_LIMIT_SURVEY_GENDER_AGE(602, "설문참여 참여 제한 에러(성별/연령대)"),

    CUSTOM_ERROR_LIMIT_EXCEL_DOWNLOAD(603, "엑셀 다운로드 제한 에러"),
    CUSTOM_ERROR_ADD_ATTEND_CODE(604, "참여코드 추가 제한 에러"),

    CUSTOM_ERROR_SODAR_SAVE_ERROR(700, "SODAR 저장 시 에러 발생"),
    CUSTOM_ERROR_XSS(701, "XSS 에러 발생"),
    CUSTOM_ERROR_GET_DATA_IS_NULL(702, "가져온 데이터가 없음"),

    CUSTOM_ERROR_WEB_EVENT_BASE_NULL(800, "WEB_EVENT_BASE 없음"),
    CUSTOM_ERROR_EVENT_ID_NULL(801, "EVENT_ID 없음"),
    CUSTOM_ERROR_AR_EVENT_INFO_NULL(802, "AR_EVENT 정보 없음"),
    CUSTOM_ERROR_PARAM_ERROR(803, "필수 파라미터가 없음"),
    CUSTOM_ERROR_DUPLICATE_ATTEND_CODE(804, "참여코드 중복"),
    CUSTOM_ERROR_CONDITION(805, "참여가능한 오브젝트가 없습니다."),
    CUSTOM_ERROR_NOT_SERVICE_STATUS(806, "서비스중인 상태가 아님"),
    CUSTOM_ERROR_IS_IMPOSSIBLE_ATTEND_TIME(807, "참여가능한 시간이 아님"),
    CUSTOM_ERROR_NOT_ATTEND_CODE(808, "참여 코드가 없음"),
    CUSTOM_ERROR_USED_ATTEND_CODE(809, "사용된 참여코드"),
    CUSTOM_ERROR_NOT_MATCHING_GIVE_AWAY_PASSWORD(810, "경품비밀번호가 일치하지 않음"),
    CUSTOM_ERROR_EXITS_GIVE_AWAY_PASSWORD(811, "경품비밀번호가 존재함"),
    CUSTOM_ERROR_EXITS_WINNING_INFO(812, "이미 당첨이력이 존재함"),
    CUSTOM_ERROR_NULL_EVENT_GIVE_AWAY_DELIVERY(813, "경품배송정보가 없음"),
    CUSTOM_ERROR_ATTEND_TOTAL_LIMIT_COUNT(814, "참여코드 등록할수 있는개수가 초과(100,000)"),
    CUSTOM_ERROR_NULL_AR_EVENT_OBJECT_ID(815, "arEventObjectId가 없습니다."),
    CUSTOM_ERROR_LIMIT_WINNING_COUNT(816, "당첨수에 제한되었습니다."),
    CUSTOM_ERROR_NULL_WINNING_INFO(817, "당첨정보 데이터가 없음."),
    CUSTOM_ERROR_ALREADY_RECEIPT_GIVE_AWAY(818, "이미 수령이 완료된 경품"),
    CUSTOM_ERROR_ATTEND_COUNT_LIMIT_ATTEND_CODE(819, "참여코드 참여회수 초과"),
    CUSTOM_ERROR_PID_NULL(820, "등록된 PID 값이 없음"),
    CUSTOM_ERROR_GIFTICON_SEND_ERROR(821, "기프티콘 발송 에러"),
    CUSTOM_ERROR_NULL_TEST_SMS_RESULT(822, "test sms 발송 결과값이 없음"),
    CUSTOM_ERROR_STRING_LENGTH_ERROR(823, "문자열 길이가 에러입니다."),
    CUSTOM_ERROR_GIVE_AWAY_SAVE(824, "경품저장 데이터 저장 시 에러"),
    CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_NAME(825, "경품저장 데이터(이름) 저장 시 에러"),
    CUSTOM_ERROR_GIVE_AWAY_SAVE_PHONE_NUMBER(826, "경품저장 데이터(핸드폰번호) 저장 시 에러"),
    CUSTOM_ERROR_GIVE_AWAY_SAVE_ADDRESS(827, "경품저장 데이터(주소) 저장 시 에러"),
    CUSTOM_ERROR_GIVE_AWAY_SAVE_PASSWORD(828, "경품저장 데이터(비밀번호) 저장 시 에러"),
    CUSTOM_ERROR_GIVE_AWAY_SAVE_MEMBER_BIRTH_15(829, "경품저장 데이터(만15세) 저장 시 에러"),
    CUSTOM_ERROR_GIFTICON_RECEIVE_COUNT(830, "기프티콘 발급 개수 초과 에러"),
    CUSTOM_ERROR_GIVE_AWAY_SQL_ERROR(831, "경품저장 데이터 저장 시 SQL 에러"),
    CUSTOM_ERROR_MEMBER_BIRTH_EXITS_SPECIAL_CHARACTER(832, "경품저장 데이터(생년월일 자리수 8자리)에 특수문자가 포함 되어있는 에러"),
    CUSTOM_ERROR_EVENT_WINNING_BUTTON_NULL(833, "경품 버튼 데이터 에러"),
    CUSTOM_EVENT_LOG_WINNING_ID_DUPLICATE(834, "당첨 저장시 당첨 로그 아이디 중복"),
    CUSTOM_EVENT_NFT_BANNER_REG_LIMIT(835, "이벤트 NFT 배너 저장가능한 개수 에러"),
    CUSTOM_ERROR_PASSWORD_REGULAR_EXPRESSION(836, "경품저장 비밀번호 정규식 에러"),
    CUSTOM_ERROR_DUPLICATE_NFT_TOKEN(837, "NFT 토큰 정보 중복 에러"),
    CUSTOM_ERROR_DUPLICATE_NFT_WALLET_ADDRESS(838, "NFT 지갑 주소 중복 에러"),
    CUSTOM_ERROR_NULL_NFT_WALLET(839, "NFT 주소정보가 없음"),
    CUSTOM_ERROR_NULL_NFT_REPOSITORY(840, "NFT 저장소 정보가 없음"),
    CUSTOM_ERROR_NULL_NFT_TOKEN(841, "NFT 토큰가 없음"),
    CUSTOM_ERROR_NOT_TRANS_REPOSITORY_NFT_TOKEN(842, "사용자의 저장소에 이전이 안된 NFT 토큰"),
    CUSTOM_ERROR_STATUS_TRANS_NFT_REPOSITORY(843, "NFT가 이전이 가능한 상태가 아님"),
    CUSTOM_ERROR_NFT_TRANSFER(844, "NFT이전시 에러가 발생"),
    CUSTOM_ERROR_NFT_API_TRANSFER(845, "NFT이전 API 에러 발생"),
    CUSTOM_ERROR_SMS_SEND(846, "SMS 발송 에러"),
    CUSTOM_ERROR_EVENT_ATTEND_EXPIRED_DAY(847, "이벤트 참여 가능 기간만료"),
    CUSTOM_ERROR_IMPOSSIBLE_EVENT_MODIFY(848, "이벤트가 수정가능한 상태가 아님"),
    CUSTOM_ERROR_COMMON_LOG_PERSON_AGREE_ID_DUPLICATE(849, "공통 개인정보 활용 동의 저장시 아이디 중복 에러"),
    CUSTOM_ERROR_LIMIT_SMS_AUTH_SEND(858, "SMS 인증 발송 개수 초과 (3회)"),
    CUSTOM_ERROR_SMS_AUTH_CODE(850, "SMS 인증 코드 에러"),
    CUSTOM_ERROR_EXPIRED_SMS_AUTH_TIME(851, "SMS 인증 시간만료(3분초과)"),
    CUSTOM_ERROR_NULL_SURVEY_SUBJECT_CATEGORY_ID(852, "surveySubjectCategoryId 가 없습니다."),
    CUSTOM_ERROR_ATTEND_COUNT_LIMIT_SURVEY_TARGET(853, "목포달성 참여회수 초과"),
    CUSTOM_ERROR_ATTEND_COUNT_LIMIT_PHONE_NUMBER(854, "전화번호 참여회수 초과"),
    CUSTOM_ERROR_NO_ATTEND_TARGET(855, "성/연령별 참여조건이 아닙니다."),
    CUSTOM_ERROR_EXPIRED_SURVEY_LOG_ID(856, "이미 사용완료된 survey log id 입니다."),
    CUSTOM_ERROR_NOT_ISSUED_SURVEY_LOG_ID(857, "발급되지 않은 survey log id 입니다."),
    CUSTOM_ERROR_OCB_MBR_ID_NULL(859, "OK캐시백 MBRID 가 없습니다."),
    CUSTOM_ERROR_OCB_POINT_SAVE_STATUS(860, "OK캐시백 포인트 적립 가능한 상태가 아닙니다."),
    CUSTOM_ERROR_OCB_POINT_LIMIT_COUNT(861, "OK캐시백 포인트 적립 개수 제한"),
    CUSTOM_ERROR_OCB_POINT_SAVE_ALREADY(862, "OK캐시백 MBRID 기준 포인트 적립이 이력이 있음(1회초과)"),
    CUSTOM_ERROR_GIVE_AWAY_SAVE_ETC_EMPTY(863, "경품 저장 데이터(기타) 저장 시 에러"),
    CUSTOM_ERROR_OCB_SESSION_NULL(864, "OCB 세션 정보 NULL 에러"),
    CUSTOM_ERROR_OCB_SAVE_INFO_NULL(865, "OCB 포인트 정보 NULL 에러"),
    CUSTOM_ERROR_PID_NOT_LOCATION_MATCH(866, "PID가 현재 위치에 일치하지 않음"),
    CUSTOM_ERROR_STAMP_TR_TYPE(867, "스탬프 TR 유형 에러"),
    CUSTOM_ERROR_STAMP_TR_NULL(868, "스탬프 TR 정보 없음"),
    CUSTOM_ERROR_STAMP_GIVE_AWAY_IS_NULL(869, "스탬프 경품 저장시 이미 저장된 경품값이 없음"),
    CUSTOM_ERROR_DUPLICATE_VALUE(870, "중복된 값이 있음"),

    CUSTOM_ERROR_PAGE_NULL(866, "페이지를 입력해주세요"),
    CUSTOM_ERROR_PAGE_SIZE_NULL(867, "페이지 크기를 입력해주세요"),



    JSON_PARSE_EXCEPTION_ERROR(1001, "JSON PARSE EXCEPTION ERROR"),
    STRING_ENCODING_EXCEPTION_ERROR(1002, "JSON PARSE EXCEPTION ERROR"),

    DB_GET_ERROR(903, "DB GET ERROR"),

    SYSTEM_ERROR(999, "오류가 발생하였습니다. 잠시후 재시도 부탁드립니다.") // System Code
    ;

    int code;
    String msg;

    ErrorCodeDefine(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }

    public static String getEventErrorMessage(int code) {
        for (ErrorCodeDefine errCode : ErrorCodeDefine.values()) {
            if (code == errCode.code) {
                return errCode.msg();
            }
        }
        return null;
    }

    public static String getLogErrorMessage(int code) {
        for (ErrorCodeDefine errCode : ErrorCodeDefine.values()) {
            if (code == errCode.code) {
                return "error {} " + errCode.msg();
            }
        }
        return null;
    }
}
