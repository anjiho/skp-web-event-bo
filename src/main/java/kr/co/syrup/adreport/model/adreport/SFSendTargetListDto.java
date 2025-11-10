package kr.co.syrup.adreport.model.adreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by emma on 2021. 4. 6..
 */
@Data
@ApiModel(value = "광고 리포팅 (SF) - 발송대상 리스트 조회")
@EqualsAndHashCode(callSuper = false)
public class SFSendTargetListDto implements Serializable {
    private static final long serialVersionUID = -3652320632701161370L;

    @ApiModelProperty(value = "mid")
    @JsonProperty(value="MID")
    private String mid;

    @ApiModelProperty(value = "mdn")
    @JsonProperty(value="MDN")
    private String mdn;

    @ApiModelProperty(value = "가맹점명")
    private String name;

}
