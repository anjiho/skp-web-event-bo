package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProximityReqDto implements Serializable {

    private static final long serialVersionUID = -9194184526091091638L;

    private String eventId;

    private String latitude;

    private String longitude;

    private String traceNo;
}
