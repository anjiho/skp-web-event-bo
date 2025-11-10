package kr.co.syrup.adreport.web.event.mybatis.vo;

import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventRepositoryButtonEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CouponDetailInfoMapVO implements Serializable {

    private static final long serialVersionUID = -2280361297023110785L;

    private String nftCouponId;

    private String couponCode;

    private Boolean isUse;

    private String nftImgUrl;

    private String nftInactiveImgUrl;

    private Integer arEventWinningId;

    private String productName;

    private String repositoryBarcodeViewYn;

    private String repositoryRandomViewYn;

    private String repositoryButtonSettingYn;

    private String etcDescImgSettingYn;

    private String etcDescImgUrl;

    private String stpPanCouponActiveUrl;

    private String stpPanCouponInActiveUrl;

    private List<ArEventRepositoryButtonEntity> couponRepositoryButtonList;

    private List<ArEventNftBenefitEntity> benefitInfo;
}
