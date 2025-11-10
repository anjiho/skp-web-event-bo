package kr.co.syrup.adreport.stamp.event.define;

public enum StampStatusTypeDefine {
    STAMP_BEFORE, // 스탬프 미적립
    STAMP_AFTER_END,// 스탬프 적립완료 - 당첨시도 없음
    STAMP_AFTER_NEXT,// 스탬프 적립완료 - 당첨시도 가능
    GIVEAWAY_SUCCESS,// 당첨 성공
    GIVEAWAY_FAIL; // 당첨 실패
}
