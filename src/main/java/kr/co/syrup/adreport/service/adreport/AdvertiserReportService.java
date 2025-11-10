package kr.co.syrup.adreport.service.adreport;

import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.model.adreport.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdvertiserReportService {

    @Value("${domain.sordar.internal}")
    String sodarDomain;

    @Autowired
    private SyrupFriendsReportService syrupFriendsReportService;

    @Autowired
    private ApiHelperService apiHelperService;

    public AdreportResponseWrapDto getGateContractList(AdvertiserGateListReq condition) throws Exception {
        String url = sodarDomain + "/v3/adreport/gateList";

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, AdvertiserGateListRes.class);

        AdvertiserGateListRes res = (AdvertiserGateListRes) ret.getData();

        if (res == null) {
            res = new AdvertiserGateListRes();
        }

        try {
            res.setEncMdn(apiHelperService.encryptMdn(condition.getMdn()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // 첫페이지일 경우만 시럽프렌즈 조회
        if ("1".equals(condition.getPage())) {

            if (ProfileProperties.isLocal()) condition.setMdn("01028612528");

            List<SFGateDto> sfGateDtoList = syrupFriendsReportService.getGateContractList(condition);
            res.setList4SF(sfGateDtoList);
        }

        ret.setData(res);

        return ret;
    }

    public AdreportResponseWrapDto getFlickingContractList(AdvertiserFlickingListReq condition) {
        String url = sodarDomain + "/v3/adreport/flickingList";

        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, AdvertiserFlickingListRes.class);
        return ret;
    }

    public AdreportResponseWrapDto getReportList(AdvertiserReportListReq condition) throws Exception {
        String url = sodarDomain + "/v3/adreport/reportList";

        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, AdvertiserReportListRes.class);

        return ret;
    }

    public AdreportResponseWrapDto getDayStats(AdvertiserDayStatsReq condition) {
        String url = sodarDomain + "/v3/adreport/dayStatsList";

        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, AdvertiserDayStatsRes.class);

        return ret;
    }

    public AdreportResponseWrapDto getMonthStats(AdvertiserMonthStatsReq condition) {
        String url = sodarDomain + "/v3/adreport/monthStatsList";

        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, AdvertiserMonthStatsRes.class);

        return ret;
    }


    public AdreportResponseWrapDto advertisementApply(AdvertisementApplyReq condition) {
        String url = sodarDomain + "/v3/adreport/apply";

        AdreportResponseWrapDto ret = apiHelperService.callPostApi(url, condition, null, AdreportResponseWrapDto.class, AdvertisementApplyRes.class);

        return ret;
    }

    public AdreportResponseWrapDto getSolutionImageList(AdvertiserSolutionImgListReq condition) {
        String url = sodarDomain + "/v3/adreport/solutionImgList";

        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, List.class);

        return ret;
    }

    public AdreportResponseWrapDto receiveCheckUpdate(AdreportReceiveCheckUpdateReq condition) {
        String url = sodarDomain + "/v3/adreport/receiveCheckUpdate";

        AdreportResponseWrapDto ret = apiHelperService.callPostApi(url, condition, null, AdreportResponseWrapDto.class, List.class);

        return ret;
    }
}
