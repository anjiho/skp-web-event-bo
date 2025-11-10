package kr.co.syrup.adreport.web.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GiveAwayReceiptReqDto implements Serializable {

    private static final long serialVersionUID = 5000833625025380459L;

    @ApiModelProperty(value = "AR, 서베이고 경폼 입력 인덱스")
    private int giveAwayId;

    @ApiModelProperty(value = "스탬프 경폼 입력 인덱스")
    private Long stpGiveAwayId;
}
