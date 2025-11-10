package kr.co.syrup.adreport.stamp.event.define;

public enum StampThemeCodeDefine {
    기본_3배열("01"), 자유형("02");

    final String stampThemeCode;

    StampThemeCodeDefine(String themeCode) {
        this.stampThemeCode = themeCode;
    }

    public String code() {
        return stampThemeCode;
    }
}
