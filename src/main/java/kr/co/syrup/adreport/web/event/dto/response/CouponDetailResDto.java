package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.stamp.event.model.StampEventGiveAwayDeliveryModel;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponDetailResDto implements Serializable {

    private static final long serialVersionUID = 9092006866601364379L;

    private Long id;

    private Long nftCouponInfoId;

    private Integer giveAwayId;

    private Boolean isUse = false;

    private Date useDate;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private Long eventWinningLogId;

    private String ocbCouponId;

    private Long stpGiveAwayId;

    private Long stampEventWinningLogId;

    private String OcbCouponImgUrl;

    private ArEventNftCouponInfoResDto arEventNftCouponInfoEntity;

    private EventGiveAwayDeliveryResDto eventGiveAwayDeliveryEntity;

    private StampEventGiveAwayDeliveryModel stampEventGiveAwayDelivery;


}
