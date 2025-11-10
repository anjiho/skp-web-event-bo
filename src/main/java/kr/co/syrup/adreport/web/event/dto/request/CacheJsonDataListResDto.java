package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheJsonDataListResDto implements Serializable {

    private static final long serialVersionUID = 6766845745345832691L;

    private String eventId;

    private String jsonType;

    private String fieldId;

    private String jsonValue;
}
