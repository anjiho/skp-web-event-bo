package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_WINNING")
public class ArEventWinningEntity implements Serializable {

    private static final long serialVersionUID = 6768917241187597596L;

    // 아이디
    @Id
    @Column(name = "ar_event_winning_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 아이디
    private Integer arEventWinningId;

    // 이벤트 아이디
    private Integer arEventId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    // 당첨자 정보 설정 넘버
    private Integer eventWinningSort;

    // 오브젝트 매핑 선택 타입 값
    private String objectMappingType;

    // 매핑정보 설정 넘버
    private Integer objectMappingNumber;

    // 당첨 타입  값(기프티콘, 기타, 꽝, NFT)
    private String winningType;

    // 기프티콘 상품 코드 값
    private String gifticonProductCode;

    // 기프티콘 캠패인 ID 값
    private String gifticonCampaignId;

    // 당첨시간설정 여부  값
    private String winningTimeType;

    // 당첨 시작 시간(0 ~ 23)
    private Integer startWinningTime;

    // 당첨 종료 시간(1 ~ 24)
    private Integer endWinningTime;

    // 전체 당첨 수량
    private Integer totalWinningNumber;

    // 일 당첨 수량
    private Integer dayWinningNumber;

    // 시간당 당첨 수량
    private Integer hourWinningNumber;

    // 당첨률
    private String winningPercent;

    // 당첨 이미지 url
    private String winningImageUrl;

    // 당첨 상품명
    private String productName;

    // 참여번호당 당첨제한 타입 값
    private String attendCodeWinningType;

    // 참여번호당 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer attendCodeLimitType;

    // 참여번호당 당첨제한 회수
    private Integer attendCodeWinningCount;

    //NFT 상품 이미지 URL (NFT 추가건)
    @Column(name = "nft_img_url")
    private String nftImgUrl;

    //NFT 상품 비활성 이미지 URL (NFT 추가건)
    @Column(name = "nft_inactive_img_url")
    private String nftInactiveImgUrl;

    //NFT 소유권이전일 등록여부( N : 설정안함 ,  Y : 날짜지정)
    @Column(name = "nft_ownership_transfer_date_assign_yn", length = 1)
    private String nftOwnershipTransferDateAssignYn;

    //NFT 소유권이전일(YYYY-MM-DD HH:MM:SS) (NFT 추가건)
    @Column(name = "nft_ownership_transfer_date")
    private Date nftOwnershipTransferDate;

    //NFT 혜약 등록(N : 선택안함, Y : 혜택등록) (NFT 추가건)
    @Column(name = "nft_benefit_reg_yn", length = 1)
    private String nftBenefitRegYn;

    //응모여부 (NFT 추가건)
    @Column(name = "subscription_yn", length = 1)
    private String subscriptionYn;

    //응모 당첨수량(건수) (NFT 추가건)
    @Column(name = "subscription_winning_number")
    private Integer subscriptionWinningNumber;

    //응모 추첨일 (년월일시) (NFT 추가건)
    @Column(name = "subscription_raffle_date")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private String subscriptionRaffleDate;

    //응모 추첨실행 여부
    @Column(name = "is_subscription_raffle")
    private Boolean isSubscriptionRaffle;

    //응모 당첨 결과 발표일(년월일시) (NFT 추가건)
    @Column(name = "subscription_winning_presentation_date")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private String subscriptionWinningPresentationDate;

    // NFT 추가건 응모 당첨 결과 발표 여부
    @Column(name = "is_subscription_winning_presentation")
    private Boolean isSubscriptionWinningPresentation;

    // NFT 추가건 응모 결과 발표 스케쥴링 시간(스케쥴링)
    @Column(name = "subscription_raffle_schedule_date")
    private String subscriptionRaffleScheduleDate;

    // NFT 추가건 NFT 토큰 엑셀 업로드 파일명
    @Column(name = "nft_excel_upload_file_name")
    private String nftExcelUploadFileName;

    // 서베이고 추가건 TEXT 등록여부 (YN)
    @Column(name = "text_reg_yn")
    private String textRegYn;

    // 서베이고 추가건 TEXT 등록 TITLE 문구
    @Column(name = "text_title")
    private String textTitle;

    // 서베이고 추가건 쿠폰보관함 바코드 표시 여부 (YN)
    @Column(name = "repository_barcode_view_yn")
    private String repositoryBarcodeViewYn;

    // 서베이고 추가건 쿠폰보관함 난수번호 표시 여부 (YN)
    @Column(name = "repository_random_view_yn")
    private String repositoryRandomViewYn;

    // 서베이고 추가건 보관함 버튼설정 여부 (YN)
    @Column(name = "repository_button_setting_yn")
    private String repositoryButtonSettingYn;

    // 서베이고 추가건 자동당첨 설정 여부(YN)
    @Column(name = "auto_winning_yn")
    private String autoWinningYn;

    // 포토 추가건 기타 설명 이미지 등록여부 (선택안함 : N / 선택함 : Y)
    @Column(name = "etc_desc_img_setting_yn")
    private String etcDescImgSettingYn;

    // 포토 추가건 기타 설명 이미지 url
    @Column(name = "etc_desc_img_url")
    private String etcDescImgUrl;

    // 포토 추가건 OCB 쿠폰 ID (OCB 쿠폰 선택시 필수)
    @Column(name = "ocb_coupon_id")
    private String ocbCouponId;

    // 고객당 당첨제한 타입 값
    @Column(name = "user_winning_type")
    private String userWinningType;

    // 고객당 당첨제한 (전체 : 0 , 1일 : 1)
    @Column(name = "user_winning_limit_type")
    private Integer userWinningLimitType;

    // 고객당 당첨제한 회수
    @Column(name = "user_winning_limit_count")
    private Integer userWinningLimitCount;

    // 당첨 팝업 이미지 URL
    @Column(name = "stp_winning_popup_img_url")
    private String stpWinningPopupImgUrl;

    // 쿠폰 활성화 이미지 URL
    @Column(name = "stp_pan_coupon_active_url")
    private String stpPanCouponActiveUrl;

    // 쿠폰 비활성화 이미지 URL
    @Column(name = "stp_pan_coupon_inactive_url")
    private String stpPanCouponInactiveUrl;

    // 생성자
    private String createdBy;

    // 생성일
    @Column(name = "created_date")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    @Transient
    private Long uploadFileSeqNum;

    /**z
     * insert 전 default 값 셋팅
     */
    @PrePersist
    public void prePersist() {
        this.winningTimeType = PredicateUtils.isNull(this.winningTimeType) || StringUtils.isEmpty(this.winningTimeType) ? StringDefine.N.name() : this.winningTimeType;
        this.attendCodeWinningType = PredicateUtils.isNull(this.attendCodeWinningType) || StringUtils.isEmpty(this.attendCodeWinningType) ? StringDefine.N.name() : this.attendCodeWinningType;
        this.attendCodeWinningCount = PredicateUtils.isNull(this.attendCodeWinningCount) ? 0 : this.attendCodeWinningCount;
        this.isSubscriptionRaffle = PredicateUtils.isNull(this.isSubscriptionRaffle) ? false : true;
        this.isSubscriptionWinningPresentation = PredicateUtils.isNull(this.isSubscriptionWinningPresentation) ? false : true;
        this.nftOwnershipTransferDateAssignYn = PredicateUtils.isNull(this.nftOwnershipTransferDateAssignYn) ? StringDefine.N.name() : StringDefine.Y.name();
        this.subscriptionWinningNumber = PredicateUtils.isNull(this.subscriptionWinningNumber) ? 0 : this.subscriptionWinningNumber;
        this.isSubscriptionRaffle = PredicateUtils.isNull(this.isSubscriptionRaffle) ? false : true;
        this.isSubscriptionWinningPresentation = PredicateUtils.isNull(this.isSubscriptionWinningPresentation) ? false : true;
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }
}
