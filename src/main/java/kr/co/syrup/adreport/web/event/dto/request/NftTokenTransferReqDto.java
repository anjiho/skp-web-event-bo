package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class NftTokenTransferReqDto {

    private String contractAddress;

    private Integer tokenId;

    private String toWalletAddress;

    private String webhookURL;

    public static NftTokenTransferReqDto transferOf(String contractAddress, int tokenId, String toWalletAddress, String webhookUrl) {
        NftTokenTransferReqDto transferReqDto = new NftTokenTransferReqDto();
        transferReqDto.setContractAddress(contractAddress.trim());
        transferReqDto.setTokenId(tokenId);
        transferReqDto.setToWalletAddress(toWalletAddress.trim());
        transferReqDto.setWebhookURL(webhookUrl.trim());
        return transferReqDto;
    }
}
