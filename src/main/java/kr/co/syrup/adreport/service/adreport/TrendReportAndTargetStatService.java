package kr.co.syrup.adreport.service.adreport;

import kr.co.syrup.adreport.model.adreport.AdreportResponseWrapDto;
import kr.co.syrup.adreport.model.adreport.TargetReportDetailDto;
import kr.co.syrup.adreport.model.adreport.TargetReportDetailReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrendReportAndTargetStatService {
    @Value("${domain.sordar.internal}")
    String sodarDomainInternal;

    @Autowired
    ApiHelperService apiHelperService;

    public AdreportResponseWrapDto getTargetStat(TargetReportDetailReq condition) {
        String url = sodarDomainInternal + "/v3/adreport/targetInfo";

        AdreportResponseWrapDto ret = apiHelperService.callGetApi(url, condition, null, AdreportResponseWrapDto.class, TargetReportDetailDto.class);

        return ret;
    }
}
