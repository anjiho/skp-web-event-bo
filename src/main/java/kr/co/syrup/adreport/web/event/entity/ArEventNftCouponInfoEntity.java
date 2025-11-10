package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ar_event_nft_coupon_info")
public class ArEventNftCouponInfoEntity implements Serializable {

    private static final long serialVersionUID = -1801473719729776011L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "ar_event_id")
    private Integer arEventId;

    // 스탬프 메인 인덱스
    @Column(name = "stp_id")
    private Integer stpId;

    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    @Column(name = "nft_coupon_id", nullable = false)
    private String nftCouponId;

    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;

    @Column(name = "upload_excel_file_name", nullable = false, length = 200)
    private String uploadExcelFileName;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @PrePersist
    public void prePersist() {
        this.isPayed = false;
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventNftCouponInfoEntity excelUploadOf(String nftCouponId, String uploadExcelFileName) {
        ArEventNftCouponInfoEntity couponInfoEntity = new ArEventNftCouponInfoEntity();
        couponInfoEntity.setNftCouponId(nftCouponId.trim());
        couponInfoEntity.setUploadExcelFileName(uploadExcelFileName);
        return couponInfoEntity;
    }

    public static ArEventNftCouponInfoEntity addExcelUploadOf(int arEventId, int arEventWinningId, String nftCouponId, String uploadExcelFileName, boolean isStamp) {
        ArEventNftCouponInfoEntity couponInfoEntity = new ArEventNftCouponInfoEntity();
        if (!isStamp) {
            couponInfoEntity.setArEventId(arEventId);
            couponInfoEntity.setStpId(0);
        } else {
            couponInfoEntity.setArEventId(0);
            couponInfoEntity.setStpId(arEventId);
        }
        couponInfoEntity.setArEventWinningId(arEventWinningId);
        couponInfoEntity.setNftCouponId(nftCouponId);
        couponInfoEntity.setUploadExcelFileName(uploadExcelFileName);
        return couponInfoEntity;
    }

    public static ArEventNftCouponInfoEntity transferOf(ArEventNftCouponInfoEntity couponInfo) {
        couponInfo.setId(couponInfo.getId());
        couponInfo.setIsPayed(true);
        return couponInfo;
    }



}