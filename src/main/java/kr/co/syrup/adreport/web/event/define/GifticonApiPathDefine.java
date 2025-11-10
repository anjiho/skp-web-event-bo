package kr.co.syrup.adreport.web.event.define;

/**
 * 11번가 기프티콘 연동 URL PATH 정의
 */
public enum GifticonApiPathDefine {

    주문("/b2b/order.gc");

    public String path;

    GifticonApiPathDefine(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
