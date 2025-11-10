package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.dto.request.PhotoContentsListReqDto;
import kr.co.syrup.adreport.web.event.dto.request.PhotoLogicalReqDto;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Web AR API 규격서 Response DTO
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebArObjectResDto implements Serializable {

    private static final long serialVersionUID = -4998518721685508899L;

    private String eventId;

    private String eventTitle;

    private String eventLogicalType;

    private String arBgImage;

    private String arSkinImage;

    private String attendCode;

    private String loadingImgYn;

    private String loadingImgUrl;

    private List<ArEventObjectResDto> arObjectInfo;

    private ArEventLogicalResDto arEventLogicalInfo;

    private List<ArEventScanningImageResDto> arScanningImageInfo;

    private PhotoLogicalResDto photoLogicalInfo;

    private PhotoContentsListReqDto photoContentsInfo;
}
