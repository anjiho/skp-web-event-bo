package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.dto.response.api.ProximitySensingAreaResDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProximityResDto implements Serializable {

    private static final long serialVersionUID = -8217671912162011196L;

    private String tid;

    private String eventId;

    private String eventName;

    private String eventExist;

    private String sensingType;

    private Integer sensingAreaCount;

    private List<ProximitySensingAreaResDto> sensingArea;
}
