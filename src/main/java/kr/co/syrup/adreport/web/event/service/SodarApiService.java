package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.service.adreport.ApiHelperService;
import kr.co.syrup.adreport.web.event.dto.request.SmsCallReqDto;
import kr.co.syrup.adreport.web.event.dto.response.api.SodarMemberResDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SodarApiService {

    @Value("${domain.sordar.internal}")
    private String smsSendUrl;

    @Value("${domain.sodar.ia.admin}")
    String sodarIaAdminDomain;

    @Value("${domain.sodar.ma.admin}")
    String sodarMaAdminDomain;

    @Autowired
    private ApiHelperService apiHelperService;

    @Autowired
    private OkHttpService okHttpService;

    /**
     * 소다 sms 발송
     * @param phoneNumber
     * @param content
     * @return
     */
    public <T> T sendSodarSms(String smsUriPath, String phoneNumber, String content, Class<T> tClass) {
        if (StringUtils.isNotEmpty(phoneNumber) || StringUtils.isNotEmpty(content)) {
            SmsCallReqDto condition = SmsCallReqDto.condition(phoneNumber, content);
            return apiHelperService.callPostApi(smsSendUrl + smsUriPath, condition, tClass);
        }
        return null;
    }

    public <T> T sendSodarSms(String smsUriPath, Object paramCondition, Class<T> tClass) {
        if (PredicateUtils.isNotNull(paramCondition)) {
            return apiHelperService.callPostApi(smsSendUrl + smsUriPath, paramCondition, tClass);
        }
        return null;
    }

    /**
     * 소다 IA 로그인 멤버 체크
     * @param cookies
     * @return
     */
    public SodarMemberResDto checkSodarLoginMember(Cookie[] cookies) {
        String domain = "";
        String makeCookie = "";
        for (Cookie cookie : cookies) {
            makeCookie += StringTools.joinStringsNoSeparator(cookie.getName(), "=", cookie.getValue(), "; ");
            //로그인 사인트 종류 확인
            if (PredicateUtils.isEqualsStr(cookie.getName(), "SITETYPE")) {
                //MA
                if (PredicateUtils.isEqualsStr(cookie.getValue(), "MA")) {
                    domain = sodarMaAdminDomain;
                } else {
                //IA
                    domain = sodarIaAdminDomain;
                }
            }
        }
        String url = StringTools.joinStringsNoSeparator(domain, "/member");

        HashMap<String, String> header = new HashMap<>();
        header.put("Cookie", makeCookie);

        return okHttpService.callGetApi(url, header, SodarMemberResDto.class);
    }

}
