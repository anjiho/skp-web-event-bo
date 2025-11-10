package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBenefitEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftTokenInfoEntity;
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
public class ArEventNftRepositoryResDto implements Serializable {

    private static final long serialVersionUID = -5585224462541958222L;

    private Long id;

    private Long arEventNftWalletId;

    private Integer giveAwayId;

    private Long arEventNftTokenInfoId;

    private Date nftTranceDate;

    private Integer holderTranceStatus;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy.MM.dd HH시mm분", timezone="Asia/Seoul")
    private Date createdDate;

    private ArEventNftTokenInfoEntity nftTokenInfo;

    private List<ArEventNftBenefitEntity> nftBenefitInfo;

}
