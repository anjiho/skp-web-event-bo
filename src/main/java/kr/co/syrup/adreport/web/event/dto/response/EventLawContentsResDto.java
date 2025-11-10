package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
public class EventLawContentsResDto implements Serializable {

    private static final long serialVersionUID = -2188475411768634224L;

    private String contents;

    private List<HashMap<String, Object>> hyperLinkInfo;
}
