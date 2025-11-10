package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheableInfoResDto implements Serializable {

    private Integer id;

    private String eventId;

    private Integer arEventId;

    private Integer sodarVersion;
}
