package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.service.adreport.ApiHelperService;
import kr.co.syrup.adreport.web.event.dto.request.ProximityApiReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ProximityResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.CouponInfoResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointApiResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbSessionApiResDto;
import kr.co.syrup.adreport.web.event.dto.response.api.PicasoCouponInfoApiResDto;
import kr.co.syrup.adreport.web.event.entity.OcbPointSaveEntity;
import kr.co.syrup.adreport.web.event.entity.repository.OcbPointSaveEntityRepository;
import kr.co.syrup.adreport.web.event.logic.SkApiLogic;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
@AutoConfigureMockMvc
class OcbApiServiceTest {

    @Autowired
    private OcbApiService ocbApiService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private OcbPointSaveEntityRepository ocbPointSaveEntityRepository;

    @Autowired
    private ApiHelperService apiHelperService;

    @Autowired
    private OkHttpService okHttpService;

    @Test
    void getOcbSessionApi() {
        OcbSessionApiResDto resDto = ocbApiService.getOcbSessionApi("T14f7e6fd56054650");
        log.info("mbrId >>" + resDto.getMbrId());
    }

    @Test
    void requestOcbPointSave() {
        OcbPointSaveEntity entity = ocbPointSaveEntityRepository.findById(14).orElseGet(OcbPointSaveEntity::new);
        Assert.assertNotNull(entity);
        OcbPointApiResDto dto = ocbApiService.requestOcbPointSaveApi(entity, "Tdb8eb82dff3c4fd1", "000460");
        log.info("obj >>" + dto);
    }

    @Test
    void testGetOcbCouponInfoApi() {
        String requestId = StringTools.joinStringsNoSeparator("wevt" + DateUtils.returnNowDateByYyyymmddhhmmss());
        log.error("requestId > " + requestId);
    }

    @Test
    void testProximityDocentApi() {
        String apiUrl = "https://bsmsapidev.syrup.co.kr/v1/proximity/absolute-search?eventId=P_20230729_100004&appType=WEB&lat=35.21774434&lon=129.08259808&radius=100&mradius=120000";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "SKP-PROX DpawBxtxjPlpYXtLVgei3d6djiaDRqG8");
        headers.put("Content-Type", "application/json");


        ProximityResDto resDto = apiHelperService.callGetApi(apiUrl, null, headers, ProximityResDto.class);
        log.info("res >> " + resDto.toString());
    }

    @Test
    void testProximityApi() {
        String apiUrl = "https://bsmsapidev.syrup.co.kr/v1/proximity/event-search";
        ProximityApiReqDto condition = ProximityApiReqDto.condition("12345", "34.1234", "126.1234");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "SKP-PROX " + "DpawBxtxjPlpYXtLVgei3d6djiaDRqG8");
        headers.put("x-skp-client-id", "0001111");
        headers.put("Content-Type", "application/json");

        ProximityResDto proximityResDto = okHttpService.callGetApi(StringTools.buildUrlWithParamsByModel(apiUrl, condition), headers, ProximityResDto.class);
        log.info("res >> " + proximityResDto.toString());
    }
}