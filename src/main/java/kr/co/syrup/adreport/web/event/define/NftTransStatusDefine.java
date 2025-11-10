package kr.co.syrup.adreport.web.event.define;

public enum NftTransStatusDefine {
    //소유권 이전전
    BEFORE(0),
    //소유권 이전요청
    REQUEST(1),
    //소유권 이전완료
    SUCCESS(2);

    private int nftTransStatusCode;

    NftTransStatusDefine(int nftTransStatusCode) {
        this.nftTransStatusCode = nftTransStatusCode;
    }

    public int code() {
        return this.nftTransStatusCode;
    }
}
