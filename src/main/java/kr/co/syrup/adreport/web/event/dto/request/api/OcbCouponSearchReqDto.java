package kr.co.syrup.adreport.web.event.dto.request.api;

import kr.co.syrup.adreport.framework.utils.EventUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class OcbCouponSearchReqDto implements Serializable {

    private static final long serialVersionUID = 2465240807786191792L;

    private String serviceCode;

    private String chnl;

    private String version;

    private String traceNo;

    private Map<String, Object> requestData;

    private Integer page;

    private Integer size;

    private List<Map<String, Object>> searchList;

    public static OcbCouponSearchReqDto ofRequest(List<Map<String, Object>> searchList) {
        OcbCouponSearchReqDto reqDto = new OcbCouponSearchReqDto();
        reqDto.setServiceCode("0005");
        reqDto.setChnl("01");
        reqDto.setVersion("2.0");
        reqDto.setTraceNo(EventUtils.getTraceNo());

        Map<String, Object>requestDataMap = new HashMap<>();
        requestDataMap.put("containDetailInfo", true);
        requestDataMap.put("containMerchantInfo", true);
        requestDataMap.put("page", 1);
        requestDataMap.put("size", 100);
        requestDataMap.put("searchList", searchList);
        reqDto.setRequestData(requestDataMap);

        return reqDto;
    }
}
