package kr.co.syrup.adreport.stamp.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponInfoEntity;
import kr.co.syrup.adreport.web.event.entity.EventGiveAwayDeliveryEntity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
public class ArEventCouponRepositoryResDto implements Serializable {

    private static final long serialVersionUID = -5320857418024224111L;

    private Long id;

    @Column(name = "nft_coupon_info_id", nullable = false)
    private Long nftCouponInfoId;

    @Column(name = "give_away_id", nullable = false)
    private Integer giveAwayId;

    @Column(name = "is_use", nullable = false)
    private Boolean isUse = false;

    @Column(name = "use_date")
    private Date useDate;

    @Column(name = "created_date", nullable = false)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    @Column(name = "event_winning_log_id")
    private Long eventWinningLogId;

    @Column(name = "ocb_coupon_id")
    private String ocbCouponId;

    @Column(name = "stp_give_away_id")
    private Long stpGiveAwayId;

    @Column(name = "stamp_event_winning_log_id")
    private Long stampEventWinningLogId;

}
