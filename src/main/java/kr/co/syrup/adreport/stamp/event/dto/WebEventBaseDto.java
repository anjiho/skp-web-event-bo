package kr.co.syrup.adreport.stamp.event.dto;

import io.swagger.annotations.ApiModelProperty;
import kr.co.syrup.adreport.web.event.dto.request.EventBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class WebEventBaseDto extends EventBaseDto {


    private static final long serialVersionUID = 2330068457548410162L;

    @ApiModelProperty(value = "이벤트 ID")
    private String eventId;
}
