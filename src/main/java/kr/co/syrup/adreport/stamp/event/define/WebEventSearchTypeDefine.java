package kr.co.syrup.adreport.stamp.event.define;

public enum WebEventSearchTypeDefine {
    EVENT_TITLE("00"), //이벤트명
    EVENT_ID("01"),//이벤트아이디 (서비스솔루션아이디)
    MARKETING_ID("02"); //마케팅아이디

    final String searchTypeCode;

    WebEventSearchTypeDefine(String searchTypeCode) {
        this.searchTypeCode = searchTypeCode;
    }

    public String getSearchTypeCode(){
        return searchTypeCode;
    }

}
