package kr.co.syrup.adreport.stamp.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.syrup.adreport.web.event.dto.request.EventHtmlDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StampEventHtmlDto extends EventHtmlDto {

    private static final long serialVersionUID = 7158816098984282142L;

    @JsonIgnore
    private Integer eventHtmlId;

    @JsonIgnore
    private String eventId;

    @JsonIgnore
    private Integer arEventId;

}