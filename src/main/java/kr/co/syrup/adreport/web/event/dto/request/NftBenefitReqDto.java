package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class NftBenefitReqDto implements Serializable {

    private static final long serialVersionUID = -7979560349971239152L;

    //인덱스
    private Integer arEventNftBenefitId;

    //혜택명
    private String nftBenefitName;

    //혜택 부가 설명
    private String nftBenefitDesc;

    //혜택 순서
    private Integer nftBenefitSort;
}
