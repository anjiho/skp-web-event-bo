package kr.co.syrup.adreport.web.event.service;

import com.google.gson.*;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.service.adreport.ApiHelperService;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.NftTokenTransferReqDto;
import kr.co.syrup.adreport.web.event.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class FutureSenseApiService {

    @Value("${future.sense.api.access.key}")
    private String futureSenseApiAccessKey;

    @Value("${future.sense.api.secret.key}")
    private String futureSenseApiSecretKey;

    @Value("${future.sense.api.host}")
    private String futureSenseApiHost;

    @Autowired
    private OkHttpService okHttpService;

    @Autowired
    private ApiHelperService apiHelperService;

    public List<FutureSenseTokenInfoResDto> getNftTokenHistoryByNftIdAndContractAddress(String nftId, String contractAddress) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + getFutureSenseApiBasicAuth());

        String url = futureSenseApiHost + "/history/" + nftId + "/contract/" + contractAddress;
        FutureSenseApiResDto resDto = okHttpService.callGetApi(url, headers, FutureSenseApiResDto.class);

        if (PredicateUtils.isNotNull(resDto)) {
            if (resDto.getSuccess()) {
                if (!PredicateUtils.isNullList(resDto.getResponse())) {
                    List<Object> responseList = resDto.getResponse();

                    String responseStr = new Gson().toJson(responseList);
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(responseStr);
                    JsonArray jsonArray = element.getAsJsonArray();

                    //List<FutureSenseTokenInfoResDto> tokenInfoResDto = GsonUtils.getObjectFromJsonArray(jsonArray, FutureSenseTokenInfoResDto.class);

                    //FutureSenseTokenDetailResDto tokenDetailResDto = okHttpService.callGetApi(tokenInfoResDto.get(0).getTokenURI(), headers, FutureSenseTokenDetailResDto.class);

                    //tokenInfoResDto.forEach(token -> {
                    //    token.setTokenDetailInfo(tokenDetailResDto);
                    //});
                    return GsonUtils.getObjectFromJsonArray(jsonArray, FutureSenseTokenInfoResDto.class);
                }
            }
        }
        return null;
    }

    public String transferToken(NftTokenTransferReqDto tokenTransferReqDto) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + getFutureSenseApiBasicAuth());

        String url = futureSenseApiHost + "/transfer";
        FutureSenseApiSingleResDto resDto = okHttpService.callPostApi(url, tokenTransferReqDto, headers, FutureSenseApiSingleResDto.class);
        //FutureSenseApiSingleResDto resDto = apiHelperService.callPostApi(url, tokenTransferReqDto, headers, FutureSenseApiSingleResDto.class);

        if (PredicateUtils.isNotNull(resDto)) {
            if (resDto.getSuccess()) {
                if (PredicateUtils.isNotNull(resDto.getResponse())) {
                    Object response = resDto.getResponse();

                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(response.toString());
                    JsonObject jsonObject = element.getAsJsonObject();

                    return jsonObject.get("requestId").getAsString();
                }
            }
        }
        return null;
    }

    /**
     * nft가 지갑에 정상적으로 이전에 되었는지 확인
     * @param nftId
     * @param contractAddress
     * @param walletAddress
     * @return
     */
    public boolean isSuccessTransferNftToken(String nftId, String contractAddress, String walletAddress) {
        List<FutureSenseTokenInfoResDto> transferNftHistory = this.getNftTokenHistoryByNftIdAndContractAddress(nftId, contractAddress);
        if (!PredicateUtils.isNullList(transferNftHistory)) {
            //NFT 전송 목록중에 tokenId 일치하지 않는 목록이 있는지 확인
            Optional<FutureSenseTokenInfoResDto> nftOptional = transferNftHistory
                    .stream()
                    .filter(nft -> PredicateUtils.isEqualsStr(nft.getIdx(), "0x0"))
                    .filter(nft -> !PredicateUtils.isEqualsStr(String.valueOf(nft.getTokenId()), nftId))
                    .findAny();

            //NFT 전송 목록중에 tokenId 일치하는 목록이 있으면 지갑으로 이전된 항목이 있는지 확인
            if (!nftOptional.isPresent()) {
                Optional<FutureSenseTokenInfoResDto> walletAddressOptional = transferNftHistory
                        .stream()
                        .filter(nft -> PredicateUtils.isEqualsStr(nft.getIdx(), "0x0"))
                        .filter(nft -> PredicateUtils.isEqualsStr(nft.getTo(), walletAddress))
                        .findAny();

                return walletAddressOptional.isPresent();
            }
        }
        return false;
    }

    private String getFutureSenseApiBasicAuth() {
        String authStr = futureSenseApiAccessKey + ":" + futureSenseApiSecretKey;
        return Base64.getEncoder().encodeToString((authStr).getBytes());
    }


}
