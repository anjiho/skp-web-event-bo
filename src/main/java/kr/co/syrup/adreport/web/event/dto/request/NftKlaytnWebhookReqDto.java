package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class NftKlaytnWebhookReqDto {

    private Boolean success;

    private Object response;

    private Object error;

    private String requestId;
}
