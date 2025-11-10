package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.entity.ArEventNftBannerEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponRepositoryEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftRepositoryEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftWalletEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NftRepositoryResDto implements Serializable {

    private static final long serialVersionUID = -7995805327576405969L;

    private ArEventNftWalletEntity nftWalletInfo;

    private List<ArEventNftRepositoryResDto> nftRepositoryInfo;

    private List<ArEventNftBannerEntity> nftBannerInfo;

    private List<CouponDetailResDto> couponRepositoryInfo;

    private Integer diffServiceEndDateTodayCount;

}
