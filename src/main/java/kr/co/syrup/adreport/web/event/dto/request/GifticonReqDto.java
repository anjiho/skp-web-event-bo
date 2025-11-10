package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GifticonReqDto implements Serializable {

    private static final long serialVersionUID = 83009253341296585L;

    private String eventId;

    private Integer arEventWinningId;

    private String rcvrMdn;

    private String traceNo;
}
