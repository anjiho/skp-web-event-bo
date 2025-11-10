package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponRepositoryEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.CouponDetailInfoMapVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventCouponDetailResDto implements Serializable {

    private static final long serialVersionUID = 7119374034314617709L;

    //ArEventNftCouponRepositoryEntity couponInfo;

    CouponRepositoryResDto couponInfo;

    CouponDetailInfoMapVO couponDetailInfo;

    List<ArEventNftBenefitEntity> benefitInfo;
}
