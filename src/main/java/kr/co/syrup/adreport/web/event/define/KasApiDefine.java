package kr.co.syrup.adreport.web.event.define;

import org.springframework.http.HttpMethod;

public enum KasApiDefine {
    //지갑 생성
    CREATE_WALLET("https://wallet-api.klaytnapi.com/v2/account", HttpMethod.POST.name()),

    //지갑 조회
    SEARCH_WALLET("https://wallet-api.klaytnapi.com/v2/account/{account}", HttpMethod.GET.name()),

    //계약 생성
    CREATE_CONTRACT("https://kip17-api.klaytnapi.com/v2/contract", HttpMethod.POST.name()),

    //계약 목록 조회
    SEARCH_CONTRACTS("https://kip17-api.klaytnapi.com/v2/contract", HttpMethod.GET.name()),

    //계약 조회
    SEARCH_CONTRACT("https://kip17-api.klaytnapi.com/v2/contract/{contract}", HttpMethod.GET.name()),

    //토큰 전송
    SEND_TOKEN("https://kip17-api.klaytnapi.com/v2/contract/{contract}/token/{token}", HttpMethod.POST.name())
    ;

    private final String url;

    private final String method;

    KasApiDefine(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String url() {
        return this.url;
    }



}
