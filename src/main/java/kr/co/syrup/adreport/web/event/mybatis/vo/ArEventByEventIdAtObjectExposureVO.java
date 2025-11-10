package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ArEventByEventIdAtObjectExposureVO implements Serializable {

    private static final long serialVersionUID = -6148130606442129432L;

    private Integer arEventId;

    private String eventLogicalType;

    private Boolean locationSettingYn;

    private Boolean arAttendConditionCodeYn;

    private Boolean attendConditionMdnYn;

    // AR BG 이미지
    private String arBgImage;

    // AR 스킨 이미지
    private String arSkinImage;

    private String eventTitle;

    private String loadingImgYn;

    private String loadingImgUrl;

    private Integer arEventLogicalId;

    private String stpConnectYn;

}
