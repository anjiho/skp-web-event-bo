package kr.co.syrup.adreport.web.event.define;

/**
 * 통계 테이블 고정값 제목 정의
 */
public enum StaticsTableTitleDefine {

    페이지접속수("페이지 접속수"),
    AR호출수("AR 호출수"),
    AR호출성공수("AR 호출 성공수"),
    AR호출실패수("AR 호출 실패수"),
    당첨성공수("당첨 성공수"),
    꽝수("꽝수"),
    AR참여버튼누적기준일("AR 참여버튼 누적/기준일"),
    당첨성공누적기준일("당첨성공\r누적/기준일"),
    당첨성공수캐치당첨정보("당첨성공수\r추첨/정보 입력"),
    당첨성공수캐치정보입력("당첨성공수\r당첨캐치/당첨정보입력"),

    전체응모("전체응모"),

    NFT당첨성공누적기준일("NFT당첨성공\r누적/기준일"),

    서베이참여("서베이참여"),

    서베이제출완료("서베이\r제출완료"),

    스탬프메인접속누적기준일("스탬프메인페이지\n접속수"),

    스탬프판접속누적기준일("스탬프판\n접속수")

    ;



    private String staticsTableTitle;

    StaticsTableTitleDefine(String staticsTableTitle) {
        this.staticsTableTitle = staticsTableTitle;
    }

    public String value() {
        return staticsTableTitle;
    }
}
