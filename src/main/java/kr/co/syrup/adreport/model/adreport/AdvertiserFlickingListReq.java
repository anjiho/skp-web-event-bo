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
@ApiModel(value = "광고 리포팅 Flicking 목록 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserFlickingListReq implements Serializable {
    private static final long serialVersionUID = 6649021646319566282L;

    @ApiModelProperty(value = "광고주 MDN")
    private String mdn;

    @ApiModelProperty(value = "마케팅 ID")
    private String marketingId;

    @ApiModelProperty(value = "리포팅 기준일자")
    private String realServiceEndDate;
}
