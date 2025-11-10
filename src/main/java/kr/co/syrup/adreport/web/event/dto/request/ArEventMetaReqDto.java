package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArEventMetaReqDto implements Serializable {

    private static final long serialVersionUID = 1947758100404973809L;

    private String eventId;

    private String attendCode;

    private String latitude;

    private String longitude;

    private String traceNo;

    private String phoneNumber;

}
