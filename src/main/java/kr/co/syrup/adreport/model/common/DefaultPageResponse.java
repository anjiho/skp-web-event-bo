package kr.co.syrup.adreport.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultPageResponse extends PageResponse {
    private static final long serialVersionUID = -6556497435871883152L;

    public DefaultPageResponse(Integer pageIdx, Integer listCnt, List<?> list) {
        super(pageIdx, listCnt, list);
    }
    public DefaultPageResponse() {
        super(null,null,null);
    }

    private String latitude;
    private String longitude;

}
