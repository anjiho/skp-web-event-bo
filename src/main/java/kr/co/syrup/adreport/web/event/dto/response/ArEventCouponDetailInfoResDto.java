package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.stamp.event.model.StampEventGiveAwayDeliveryModel;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.CouponDetailInfoMapVO;
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
public class ArEventCouponDetailInfoResDto implements Serializable {

    private static final long serialVersionUID = 3103663205790698966L;

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

    private Integer arEventId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    private String nftCouponId;

    private Boolean isPayed;

    private String uploadExcelFileName;

    private Integer arEventWinningId;

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
    private String nftImgUrl;

    //NFT 상품 비활성 이미지 URL (NFT 추가건)
    private String nftInactiveImgUrl;

    //NFT 소유권이전일 등록여부( N : 설정안함 ,  Y : 날짜지정)
    private String nftOwnershipTransferDateAssignYn;

    //NFT 소유권이전일(YYYY-MM-DD HH:MM:SS) (NFT 추가건)
    private Date nftOwnershipTransferDate;

    //NFT 혜약 등록(N : 선택안함, Y : 혜택등록) (NFT 추가건)
    private String nftBenefitRegYn;

    //응모여부 (NFT 추가건)
    private String subscriptionYn;

    //응모 당첨수량(건수) (NFT 추가건)
    private Integer subscriptionWinningNumber;

    //응모 추첨일 (년월일시) (NFT 추가건)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private String subscriptionRaffleDate;

    //응모 추첨실행 여부
    private Boolean isSubscriptionRaffle;

    //응모 당첨 결과 발표일(년월일시) (NFT 추가건)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private String subscriptionWinningPresentationDate;

    // NFT 추가건 응모 당첨 결과 발표 여부
    private Boolean isSubscriptionWinningPresentation;

    // NFT 추가건 응모 결과 발표 스케쥴링 시간(스케쥴링)
    private String subscriptionRaffleScheduleDate;

    // NFT 추가건 NFT 토큰 엑셀 업로드 파일명
    private String nftExcelUploadFileName;

    // 서베이고 추가건 TEXT 등록여부 (YN)
    private String textRegYn;

    // 서베이고 추가건 TEXT 등록 TITLE 문구
    private String textTitle;

    // 서베이고 추가건 쿠폰보관함 바코드 표시 여부 (YN)
    private String repositoryBarcodeViewYn;

    // 서베이고 추가건 쿠폰보관함 난수번호 표시 여부 (YN)
    private String repositoryRandomViewYn;

    // 서베이고 추가건 보관함 버튼설정 여부 (YN)
    private String repositoryButtonSettingYn;

    // 서베이고 추가건 자동당첨 설정 여부(YN)
    private String autoWinningYn;

    // 포토 추가건 기타 설명 이미지 등록여부 (선택안함 : N / 선택함 : Y)
    private String etcDescImgSettingYn;

    // 포토 추가건 기타 설명 이미지 url
    private String etcDescImgUrl;

    // 고객당 당첨제한 타입 값
    private String userWinningType;

    // 고객당 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer userWinningLimitType;

    // 고객당 당첨제한 회수
    private Integer userWinningLimitCount;

    // 당첨 팝업 이미지 URL
    private String stpWinningPopupImgUrl;

    // 쿠폰 활성화 이미지 URL
    private String stpPanCouponActiveUrl;

    // 쿠폰 비활성화 이미지 URL
    private String stpPanCouponInactiveUrl;

    private ArEventNftCouponInfoResDto arEventNftCouponInfoEntity;

    private EventGiveAwayDeliveryResDto eventGiveAwayDeliveryEntity;

    private StampEventGiveAwayDeliveryModel stampEventGiveAwayDelivery;

    private List<ArEventNftBenefitEntity> benefitInfo;

    private CouponDetailInfoMapVO couponDetailInfo;
}
