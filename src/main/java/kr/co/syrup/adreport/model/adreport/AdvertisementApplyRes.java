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
@ApiModel(value = "광고 신청 Response")
@EqualsAndHashCode(callSuper = false)
public class AdvertisementApplyRes implements Serializable {

    private static final long serialVersionUID = -2834548576948970266L;

    @ApiModelProperty(value = "신청 결과 (200 : 성공)")
    private String code;

}
