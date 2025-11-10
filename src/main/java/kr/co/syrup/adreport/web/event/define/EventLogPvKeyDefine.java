package kr.co.syrup.adreport.web.event.define;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.utils.EnumUtils;

/**
 * WEB_AR PAGE ACTION ID 정의
 */
public enum EventLogPvKeyDefine {

    MAIN_0("/main", "", "이벤트 메인 진입", true, false, false),
    MAIN_1("/main", "tap.btn", "버튼 클릭", true, true, false),
    MAIN_2("/main", "tap.share_btn", "공유하기", false, false, false),
    MAIN_POPUP_0("/main/popup", "", "팝업 진입", true, false, false),
    MAIN_EVENT_BENEFIT_0("/main/event/benefit", "", "당첨형 당첨팝업", true, true, false),
    MAIN_EVENT_BENEFIT_1("/main/event/benefit", "tap.info_btn", "당첨정보입력 버튼", false, true, false),
    MAIN_EVENT_BENEFIT_2("/main/event/benefit", "tap.url_btn", "url 연결", false, true, false),
    MAIN_EVENT_BENEFIT_3("/main/event/benefit", "tap.closing", "AR 계속하기(닫기)", false, true, false),
    MAIN_ENTERINFO_0("/main/enterinfo", "",  "경품정보 입력창 진입", true, true, false),
    MAIN_ENTERINFO_1("/main/enterinfo", "tap.okbtn", "동의하고 작성완료 버튼 터치", false, true, true),
    MAIN_HISTORY_0("/main/history", "",  "당첨이력조회", false, false, false),
    MAIN_HISTORY_1("/main/history", "tap.okbtn", "동의하고 조회 버튼 터치", false, false, false),
    MAIN_HISTORY_2("/main/history", "tap.receive_btn", "수령확인 버튼 ",  false, true, false),
    MAIN_LOCKER_0("/main/locker", "", "보관함 진입", false, false, true),
    MAIN_LOCKER_1("/main/locker", "bottom_tap.okbtn", "조회하기 버튼 터치", false, false, true),
    MAIN_LOCKER_LIST_0("/main/locker/list", "", "보관함리스트 조회", false, false, true),
    MAIN_LOCKER_LIST_1("/main/locker/list", "tap.list", "리스트 내 쿠폰/NFT 터치", false, true, true),
    MAIN_LOCKER_LIST_2("/main/locker/list", "tap.banner", "배너 터치", false, true, true),
    MAIN_LOCKER_LIST_DETAIL_POPUP_0("/main/locker/list/detail/popup", "", "쿠폰사용하기 팝업", false, false, false),
    MAIN_LOCKER_LIST_DETAIL_POPUP_1("/main/locker/list/detail/popup", "tap.complete", "쿠폰 사용 확인 버튼", false, true, false),

    MAIN_PHOTO_0("/main/photo", "", "AR포토 촬영결과페이지", false, false, false),
    MAIN_PHOTO_1("/main/photo", "tap.btn_1", "저장 버튼 클릭", false, false, false),
    MAIN_PHOTO_2("/main/photo", "tap.btn_2", "공유 버튼 클릭", false, false, false),
    MAIN_PHOTO_3("/main/photo", "tap.btn_3", "해시태그복사 버튼 클릭", false, false, false),
    MAIN_PHOTO_4("/main/photo", "tap.btn_4", "경품추첨 버튼 클릭", false, false, false),
    MAIN_PHOTO_5("/main/photo", "tap.btn_5", "사진출력 버튼 클릭", false, false, false),
    MAIN_PHOTO_POPUP_0("/main/photo/popup", "", "공유하기 클릭시 동의 팝업", false, false, false),
    MAIN_PHOTO_POPUP_1("/main/photo/popup", "tap.btn_1", "동의 버튼 클릭", false, false, false),
    MAIN_PHOTOBOX_0("/main/photobox", "", "AR포토함 페이지", false, false, false),
    MAIN_PHOTOBOX_DETAIL_0("/main/photobox/detail", "", "AR포토함 페이지", false, false, false),
    MAIN_PHOTOBOX_DETAIL_1("/main/photobox_detail", "tap.btn_1", "AR포토함 페이지", false, false, false)

    ;

    //page_id #logKey
    final String pageId;

    //action_id #logKey
    final String actionId;

    //설명
    final String description;

    //연계로그 #body type 값 존재여부
    final Boolean isType;

    //연계로그 #body order 값 존재여부
    final Boolean isOrder;

    //연계로그 #body code 값 존재여부
    final Boolean isCode;

    EventLogPvKeyDefine(String pageId, String actionId, String description, Boolean isType, Boolean isOrder, Boolean isCode) {
        this.pageId = pageId;
        this.actionId = actionId;
        this.description = description;
        this.isType = isType;
        this.isOrder = isOrder;
        this.isCode = isCode;
    }

    public String getPageId() {
        return this.pageId;
    }

    public String getActionId() {
        return this.actionId;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getIsType() {
        return this.isType;
    }

    public Boolean getIsOrder() {
        return this.isOrder;
    }

    public Boolean getIsCode() {
        return this.isCode;
    }

    public static EventLogPvKeyDefine getByCode(String code) {
        return EnumUtils.enumValueOf(EventLogPvKeyDefine.class, code);
    }
}
