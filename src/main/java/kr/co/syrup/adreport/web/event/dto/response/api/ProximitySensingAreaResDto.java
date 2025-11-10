package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProximitySensingAreaResDto implements Serializable {

    private static final long serialVersionUID = 74984177284597040L;

    private String id;

    private String midName;

    private Double latitude;

    private Double longitude;

    private String objectUrl;

    private String bridgetUrl;

    private String changeUrl;
}
