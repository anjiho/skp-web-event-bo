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
@ApiModel(value = "광고 신청 현황 조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertisementApplyListRes implements Serializable {

    private static final long serialVersionUID = 7657648098810128448L;

    @ApiModelProperty(value = "totalCnt")
    private int totalCnt;

    @ApiModelProperty(value = "페이지")
    private int page;

    @ApiModelProperty(value = "크기")
    private int size;

    @ApiModelProperty(value = "조회 결과 리스트")
    private List<AdvertisementApplyDto> list;

}
