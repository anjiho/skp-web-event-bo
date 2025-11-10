package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventIdReqDto implements Serializable {

    private static final long serialVersionUID = 6827803351571029826L;

    private String eventId;

    private int limitCount;

    private String traceNo;
}
