package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GiveAwayDeliveryListMapperVO implements Serializable {

    private static final long serialVersionUID = -371754192632630799L;

    private Integer giveAwayId;

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

    private String useDate;
}
