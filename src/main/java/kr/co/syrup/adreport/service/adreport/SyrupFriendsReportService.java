package kr.co.syrup.adreport.service.adreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.model.adreport.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SyrupFriendsReportService {

    @Value("${domain.syrupfriends.api}")
    private String syrupfriendApiDomain;

    @Value("${syrupfriends.api.key}")
    private String syrupfriendApiKey;

    @Autowired
    private ApiHelperService apiHelperService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> getSFHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", syrupfriendApiKey);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public List<SFGateDto> getGateContractList(AdvertiserGateListReq condition) throws Exception {
        // https://skp.pointda.com/api/store/report/find/01077078902
        String url = syrupfriendApiDomain + "/api/store/report/find/" + condition.getMdn();

        /*
            {
                "img": "",
                "reportName": "시럽프렌즈",
                "name": "5월 커피 마마퀸 실적",
                "regDate": "20210323",
                "reportDate": "20210524",
                "startDate": "20210501",
                "endDate": "20210531",
                "MID": "34435867"
            }
         */
        List<SFGateDto> sfList = new ArrayList<>();
        List res = apiHelperService.callGetApi(url, null, getSFHeaders(), List.class);

        if (ProfileProperties.isLocal()) {
            res = new ArrayList();

            String sample = "{\n" +
                    "            \"img\": \"\",\n" +
                    "            \"reportName\": \"시럽프렌즈\",\n" +
                    "            \"name\": \"5월 커피 마마퀸 실적\",\n" +
                    "            \"regDate\": \"20210323\",\n" +
                    "            \"reportDate\": \"20210524\",\n" +
                    "            \"startDate\": \"20210501\",\n" +
                    "            \"endDate\": \"20210531\",\n" +
                    "            \"encMdn\": \"X2oFtt3dH8I6IAnyhxSIyw==\",\n" +
                    "            \"MID\": \"34435867\"\n" +
                    "        }";
            res.add(objectMapper.readValue(sample, SFGateDto.class));
        }

        if (res != null) {
            for (Object obj : res) {
                try {
                    SFGateDto sfGateDto = objectMapper.readValue(objectMapper.writeValueAsString(obj), SFGateDto.class);
                    if (sfGateDto == null) {
                        sfGateDto = new SFGateDto();
                    }

                    sfGateDto.setEncMdn(apiHelperService.encryptMdn(condition.getMdn()));
                    if (!StringTools.isNull2(sfGateDto.getReportDate()) && sfGateDto.getReportDate().length() <= 6) {
                        String reportDate = DateUtils.getMaxDayOfMonth("20" + sfGateDto.getReportDate(), "yyyyMM");
                        sfGateDto.setReportDate(reportDate);
                    }
                    sfList.add(sfGateDto);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return sfList;
    }

    public SFReportDto getReport4SF(SFReportReq condition) {
        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        // https://skp.pointda.com/api/store/report/34411541/202105
        StringBuilder url = new StringBuilder();
        url.append(syrupfriendApiDomain).append("/api/store/report");
        url.append("/");
        url.append(condition.getMid());
        url.append("/");
        if (condition.getReportDate().length() > 6) {
            url.append(condition.getReportDate().substring(0, 6));
        } else {
            url.append(condition.getReportDate());
        }

        SFReportDto ret = apiHelperService.callGetApi(url.toString(), null, getSFHeaders(), SFReportDto.class);

        return ret;
    }

    public SFReportCouponInfoDto couponMore(SFReportReq condition) {
        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        // https://skp.pointda.com/api/store/report/coupon/{mid}/{report_date}/{page}/{size}
        //
        StringBuilder url = new StringBuilder();
        url.append(syrupfriendApiDomain).append("/api/store/report/coupon");
        url.append("/");
        url.append(condition.getMid());
        url.append("/");
        url.append(condition.getReportDate());
        url.append("/");
        url.append(condition.getPage());
        url.append("/");
        url.append(condition.getSize());

        SFReportCouponInfoDto ret = apiHelperService.callGetApi(url.toString(), null, getSFHeaders(), SFReportCouponInfoDto.class);

        return ret;
    }

    public List<SFSendTargetListDto> getSendTargetList(SFReportReq condition) {
        condition.setMdn(apiHelperService.decryptMdn(condition.getMdn()));

        // https://skp.pointda.com/api/store/report/list/202105
        //
        StringBuilder url = new StringBuilder();
        url.append(syrupfriendApiDomain).append("/api/store/report/list");
        url.append("/");
        url.append(condition.getReportDate());

        List list = apiHelperService.callGetApi(url.toString(), null, getSFHeaders(), List.class);

        return list;
    }

}

