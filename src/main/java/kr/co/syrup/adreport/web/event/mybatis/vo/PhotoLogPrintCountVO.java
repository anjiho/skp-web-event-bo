package kr.co.syrup.adreport.web.event.mybatis.vo;

import kr.co.syrup.adreport.web.event.dto.request.SavePrintStatusReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoLogPrintCountVO implements Serializable {
    private static final long serialVersionUID = 141792562043232905L;

    private String eventId;

    private String clientUniqueKey;

    private String ocbMbrId;

    private String printResultStatus;

    private Date createdDate;

    public static PhotoLogPrintCountVO saveOf(SavePrintStatusReqDto reqDto) {
        PhotoLogPrintCountVO eventLogPvVO = new PhotoLogPrintCountVO();
        eventLogPvVO.setEventId(reqDto.getEventId());
        eventLogPvVO.setClientUniqueKey(reqDto.getClientUniqueKey());
        eventLogPvVO.setOcbMbrId(reqDto.getOcbMbrId());
        eventLogPvVO.setPrintResultStatus(reqDto.getPrintResultStatus());
        return eventLogPvVO;
    }
}
