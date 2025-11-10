package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class NftBannerReqDto implements Serializable {

    private static final long serialVersionUID = 7314383590405036941L;

    private Integer arNftBannerId;

    private String bannerImgUrl;

    private String bannerTargetUrl;

    private Integer bannerSort;
}
