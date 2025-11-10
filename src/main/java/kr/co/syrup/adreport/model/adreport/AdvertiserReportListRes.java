package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by emma on 2021. 3. 22..
 */
@Data
@ApiModel(value = "광고주 리포팅 조회 Response")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserReportListRes implements Serializable {

    private static final long serialVersionUID = 6587430573624146288L;

    @ApiModelProperty(value = "솔루션 계약 리스트")
    private List<AdvertiserReportDetailDto> list;

    @ApiModelProperty(value = "광고타겟정보")
    private TargetReportDetailDto targetInfo;

    @ApiModelProperty(value = "시럽프렌즈정보")
    private SFReportDto sfReportDto;
}
