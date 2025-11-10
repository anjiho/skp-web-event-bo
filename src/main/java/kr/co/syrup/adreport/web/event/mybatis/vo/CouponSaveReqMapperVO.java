package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponSaveReqMapperVO implements Serializable {

    private static final long serialVersionUID = 2913240240979880470L;

    private Integer arEventId;

    private Integer arEventWinningId;

    private Long eventWinningLogId;

    private Long Id;

    private String ocbCouponId;

    private Integer giveAwayId;

    private Integer stpId;

    private Long stampEventWinningLogId;
}
