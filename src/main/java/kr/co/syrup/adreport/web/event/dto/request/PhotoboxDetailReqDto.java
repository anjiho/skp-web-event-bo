package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoboxDetailReqDto implements Serializable {
    private static final long serialVersionUID = -8688239022083287721L;

    private String eventId;
    private String traceNo;
}
