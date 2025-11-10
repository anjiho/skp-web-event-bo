package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointSaveResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventRepositoryButtonEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningTextEntity;
import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArEventWinningResDto implements Serializable {

    private static final long serialVersionUID = -641184890701031577L;
    // 아이디
    private Integer arEventWinningId;

    // 이벤트 아이디
    private Integer arEventId;

    // 당첨자 정보 설정 넘버
    private Integer eventWinningSort;

    // 오브젝트 매핑 선택 타입 값
    private String objectMappingType;

    // 매핑정보 설정 넘버
    private Integer objectMappingNumber;

    // 당첨 타입  값(기프티콘, 기타, 꽝)
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

    // 참여번호당 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer attendCodeLimitType;

    // 참여번호당 당첨제한 타입 값
    private String attendCodeWinningType;

    // 참여번호당 당첨제한 회수
    private Integer attendCodeWinningCount;

    //NFT 상품 이미지 URL (NFT 추가건)
    @Column(name = "nft_img_url")
    private String nftImgUrl;

    //NFT 상품 비활성 이미지 URL (NFT 추가건)
    @Column(name = "nft_inactive_img_url")
    private String nftInactiveImgUrl;

    //NFT 소유권이전일 등록여부( N : 설정안함 ,  Y : 날짜지정)
    private String nftOwnershipTransferDateAssignYn;

    //NFT 소유권이전일(YYYY-MM-DD HH:MM:SS) (NFT 추가건)
    private String nftOwnershipTransferDate;

    //NFT 혜약 등록(N : 선택안함, Y : 혜택등록) (NFT 추가건)
    private String nftBenefitRegYn;

    //응모여부(NFT 추가건)
    private String subscriptionYn;

    //응모 당첨수량(건수) (NFT 추가건)
    private Integer subscriptionWinningNumber;

    //응모 추첨일 (년월일) (NFT 추가건)
    private String subscriptionRaffleDate;

    //응모 추첨시간 (시간) (NFT 추가건)
    private String subscriptionRaffleTime;

    //응모 당첨 결과 발표일(년월일시) (NFT 추가건)
    private String subscriptionWinningPresentationDate;

    //응모 당첨 결과 발표일(시) (NFT 추가건)
    private String subscriptionWinningPresentationTime;

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

    // 포토 추가건 OCB 쿠폰 ID (OCB 쿠폰 선택시 필수)
    private String ocbCouponId;

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

    // 생성자
    private String createdBy;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    private List<ArEventNftBenefitEntity> nftBenefitInfo;

    private List<ArEventWinningButtonResDto> arEventWinningButtonInfo;

    private Integer nftTokenCount;

    private String nftExcelUploadFileName;

    private String couponExcelUploadFileName;

    private List<ArEventWinningTextEntity> winningTextInfo;

    private List<ArEventRepositoryButtonEntity> repositoryButtonInfo;

    private OcbPointSaveResDto ocbPointSaveInfo;
}
