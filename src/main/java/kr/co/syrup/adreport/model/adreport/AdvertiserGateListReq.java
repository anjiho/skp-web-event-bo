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
@ApiModel(value = "광고 리포팅 GATE 목록 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserGateListReq implements Serializable {

    private static final long serialVersionUID = -5229734123143276108L;

    @ApiModelProperty(value = "광고주 MDN")
    private String mdn;

    @ApiModelProperty(value = "page")
    private String page = "1";

    @ApiModelProperty(value = "조회 기준 크기")
    private String size = "5";

}
