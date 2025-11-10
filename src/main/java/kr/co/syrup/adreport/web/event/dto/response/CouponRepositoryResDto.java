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
public class CouponRepositoryResDto implements Serializable {

    private CouponDetailInfoMapVO couponDetailInfo;

    private List<ArEventNftBenefitEntity> benefitInfo;


}
