package kr.co.syrup.adreport.stamp.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StampEventBaseDto extends WebEventBaseDto {
    private static final long serialVersionUID = -3934154668543065367L;

    // 서비스 종료일
    @JsonIgnore
    private String eventEndDate;

    // 실제 서비스 종료일
    @JsonIgnore
    private String realEventEndDate;

    @JsonIgnore
    private String qrCodeUrl;

    @JsonIgnore
    private String stpConnectYn;
}
