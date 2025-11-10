package kr.co.syrup.adreport.web.event.define;

//브릿지 설정 값 정의
public enum BridgeTypeDefine {
    NONE("NONE"),   //없음
    IMAGE_2D("IMAGE"),  //IMAGE(2D)
    GIF_2D("GIF"),  //GIF(2D)
    VIDEO_CDN("VIDEO"), //VIDEO(CDN)
    VIDEO_YOUTUBE("YOUTUBE"),   //VIDEO(Youtube)
    TREE_D("3D")    // 3D
    ;

    String bridgeTypeValue;

    BridgeTypeDefine(String bridgeTypeValue) {
        this.bridgeTypeValue = bridgeTypeValue;
    }
}
