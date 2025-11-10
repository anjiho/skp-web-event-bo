package kr.co.syrup.adreport.stamp.event.dto.response;

import io.swagger.annotations.ApiModelProperty;
import kr.co.syrup.adreport.stamp.event.dto.WebEventBaseDto;
import kr.co.syrup.adreport.web.event.dto.request.EventBaseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class WebEventStampTypeListResDto implements Serializable {

    private static final long serialVersionUID = 7790094615791233534L;

    @ApiModelProperty(value = "totalCnt")
    private long totalCnt;

    @ApiModelProperty(value = "page")
    private int page;

    @ApiModelProperty(value = "size")
    private int size;

    // 이벤트 리스트
    @ApiModelProperty(value = "이벤트 목록")
    private List<WebEventBaseDto> eventList;


}
