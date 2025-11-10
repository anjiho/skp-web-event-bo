package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by emma on 2021. 3. 22..
 */
@Data
@ApiModel(value = "광고주 리포팅 계약조회")
@EqualsAndHashCode(callSuper = false)
public class AdvertiserReportContractDetailDto extends AdvertiserFlickingDetailDto  {

    private static final long serialVersionUID = -4105626297462997164L;

}
