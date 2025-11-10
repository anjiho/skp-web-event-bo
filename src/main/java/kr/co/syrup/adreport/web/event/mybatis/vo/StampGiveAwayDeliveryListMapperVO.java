package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StampGiveAwayDeliveryListMapperVO {

    private Long stpGiveAwayId;

    private String productName;

    private String name;

    private String phoneNumber;

    private String zipCode;

    private String address;

    private String addressDetail;

    private String memberBirth;

    private String createdDate;

    private String nftCouponId;

    private String isUse;

    private String nftTokenId;

    private String nftWalletAddress;
}
