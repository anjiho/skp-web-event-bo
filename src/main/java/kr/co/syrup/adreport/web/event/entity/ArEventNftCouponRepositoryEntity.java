package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.model.StampEventGiveAwayDeliveryModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ar_event_nft_coupon_repository")
public class ArEventNftCouponRepositoryEntity implements Serializable {

    private static final long serialVersionUID = 6570207890866773899L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_nft_coupon_repository_id", nullable = false)
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

    @Transient
    private StampEventGiveAwayDeliveryModel stampEventGiveAwayDelivery;

    @Transient
    private String ocbCouponImgUrl;

    @PrePersist
    private void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
        this.isUse = false;
    }

    public static ArEventNftCouponRepositoryEntity saveOf(int giveAwayId, long nftCouponId) {
        ArEventNftCouponRepositoryEntity repositoryEntity = new ArEventNftCouponRepositoryEntity();
        repositoryEntity.setGiveAwayId(giveAwayId);
        repositoryEntity.setNftCouponInfoId(nftCouponId);
        return repositoryEntity;
    }

    public static ArEventNftCouponRepositoryEntity preSaveOf(long nftCouponId, long eventWinningLogId) {
        ArEventNftCouponRepositoryEntity repositoryEntity = new ArEventNftCouponRepositoryEntity();
        repositoryEntity.setNftCouponInfoId(nftCouponId);
        repositoryEntity.setEventWinningLogId(eventWinningLogId);
        return repositoryEntity;
    }

    public static ArEventNftCouponRepositoryEntity useOf(ArEventNftCouponRepositoryEntity entity) {
        entity.setIsUse(true);
        entity.setUseDate(DateUtils.returnNowDate());
        return entity;
    }

    public static ArEventNftCouponRepositoryEntity saveOfOcbCoupon(int giveAwayId, String ocbCouponId) {
        ArEventNftCouponRepositoryEntity repositoryEntity = new ArEventNftCouponRepositoryEntity();
        repositoryEntity.setGiveAwayId(giveAwayId);
        repositoryEntity.setOcbCouponId(ocbCouponId);
        return repositoryEntity;
    }

}