package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ar_event_nft_benefit")
public class ArEventNftBenefitEntity implements Serializable {

    private static final long serialVersionUID = 2267969975976044392L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_event_nft_benefit_id", nullable = false)
    private Integer arEventNftBenefitId;

    @Column(name = "ar_event_winning_id", nullable = false)
    private Integer arEventWinningId;

    @Column(name = "nft_benefit_name", length = 100)
    private String nftBenefitName;

    @Column(name = "nft_benefit_desc", length = 200)
    private String nftBenefitDesc;

    @Column(name = "nft_benefit_sort")
    private Integer nftBenefitSort;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PrePersist
    private void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventNftBenefitEntity saveOf(int arEventWinningId, String nftBenefitName, String nftBenefitDesc) {
        ArEventNftBenefitEntity arEventNftBenefit = new ArEventNftBenefitEntity();
        arEventNftBenefit.setArEventWinningId(arEventWinningId);
        arEventNftBenefit.setNftBenefitName(nftBenefitName.trim());
        arEventNftBenefit.setNftBenefitDesc(nftBenefitDesc.trim());
        return arEventNftBenefit;
    }

    public static ArEventNftBenefitEntity updateOf(ArEventNftBenefitEntity arEventNftBenefit, int arEventWinningId) {
        if (PredicateUtils.isNotNull(arEventNftBenefit)) {
            arEventNftBenefit.setArEventWinningId(arEventWinningId);
            arEventNftBenefit.setNftBenefitName(arEventNftBenefit.getNftBenefitName().trim());
            arEventNftBenefit.setNftBenefitDesc(arEventNftBenefit.getNftBenefitDesc().trim());
            arEventNftBenefit.setLastModifiedDate(DateUtils.returnNowDate());
            return arEventNftBenefit;
        }
        return null;
    }

}