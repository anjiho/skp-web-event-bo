package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 플리킹 목록 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserFlickingListRes implements Serializable {

    private static final long serialVersionUID = 5628693682633163418L;

    @ApiModelProperty(value = "총 count")
    private int totalCnt;

    @ApiModelProperty(value = "플리킹 list")
    private List<AdvertiserFlickingDto> list;

}
