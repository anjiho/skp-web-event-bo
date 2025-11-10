package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class StaticsReqDto implements Serializable {

    private static final long serialVersionUID = -2008844387099963525L;

    private String eventId;

    private String searchDay;

}
