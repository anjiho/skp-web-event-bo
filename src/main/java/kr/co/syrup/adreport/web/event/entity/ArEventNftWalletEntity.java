package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.NftWalletTypeDefine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ar_event_nft_wallet")
public class ArEventNftWalletEntity implements Serializable {

    private static final long serialVersionUID = 4983478118716809580L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_event_nft_wallet_id", nullable = false)
    private Long id;

    @Column(name = "ar_event_id", nullable = false)
    private Integer arEventId;

    @Column(name = "user_phone_number", nullable = false, length = 200)
    private String userPhoneNumber;

    @Column(name = "nft_wallet_address", length = 200)
    private String nftWalletAddress;

    @Column(name = "nft_wallet_type", nullable = false, length = 20)
    private String nftWalletType;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PrePersist
    private void prePersist() {
        this.nftWalletType = StringUtils.isEmpty(nftWalletType) ? NftWalletTypeDefine.KAS.name() : this.nftWalletType;
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventNftWalletEntity saveOf(int arEventId, String userPhoneNumber, String nftWalletAddress) {
        ArEventNftWalletEntity arEventNftWalletEntity = new ArEventNftWalletEntity();
        arEventNftWalletEntity.setArEventId(arEventId);
        arEventNftWalletEntity.setUserPhoneNumber(userPhoneNumber.trim());
        arEventNftWalletEntity.setNftWalletAddress(nftWalletAddress.trim());
        return arEventNftWalletEntity;
    }

    public static ArEventNftWalletEntity updateOf(ArEventNftWalletEntity savedNftWalletEntity, String nftWalletAddress) {
        savedNftWalletEntity.setNftWalletAddress(nftWalletAddress.trim());
        return savedNftWalletEntity;
    }

}