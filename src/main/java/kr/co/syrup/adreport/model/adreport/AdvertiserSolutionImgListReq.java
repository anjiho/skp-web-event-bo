package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 솔루션별 이미지 목록 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserSolutionImgListReq implements Serializable {

    private static final long serialVersionUID = -86481430072938505L;

    @ApiModelProperty(value = "마케팅 ID")
    private String marketingId;

    @ApiModelProperty(value = "광고주 MDN")
    private String mdn;

}
