package kr.co.syrup.adreport.web.event.define;

public enum ScheduleDefine {

    WINNING_DELETE("당첨관련 기록 삭제 스케줄러"),
    WINNING_SUBSCRIPTION("당첨 응모 결과 스케줄러"),
    STAMP_WINNING_DELETE("스탬프 당첨관련 기록 삭제 스케줄러"),
    ;

    private String desc;

    ScheduleDefine(String desc) {
        this.desc = desc;
    }
}
