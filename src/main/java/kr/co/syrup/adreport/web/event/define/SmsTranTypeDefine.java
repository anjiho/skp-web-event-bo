package kr.co.syrup.adreport.web.event.define;

public enum SmsTranTypeDefine {

    SMS(4), MMS(6), AT(7), FT(8), RCS(11);

    private final int smsTransTypeKey;

    SmsTranTypeDefine(int smsTransTypeKey) {
        this.smsTransTypeKey = smsTransTypeKey;
    }

    public int key() {
        return this.smsTransTypeKey;
    }
}
