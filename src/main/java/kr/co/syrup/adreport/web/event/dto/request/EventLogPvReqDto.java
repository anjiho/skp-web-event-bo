package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventLogPvReqDto implements Serializable {

    private static final long serialVersionUID = -2035283671912163968L;

    private String eventId;

    private Integer arEventId;

    private String pvLogType;

    private String browserVersion;

    private String br;

    private String type;

    private String order;

    private String code;
}
