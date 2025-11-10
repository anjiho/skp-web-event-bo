package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FutureSenseTokenInfoResDto {

    private Integer tokenId;

    private Integer timestamp;

    private String contractAddress;

    private String tokenURI;

    private String walletAddress;

    private String idx;

    private String from;

    private String to;

    private String transactionHash;

    private FutureSenseTokenDetailResDto tokenDetailInfo;

}
