//package kr.co.syrup.adreport.controller.rest.web.event;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import kr.co.syrup.adreport.framework.utils.StringTools;
//import kr.co.syrup.adreport.survey.go.service.SurveyGoStaticsService;
//import kr.co.syrup.adreport.web.event.define.*;
//import kr.co.syrup.adreport.web.event.dto.request.*;
//import kr.co.syrup.adreport.web.event.dto.request.api.OcbPointSaveReqDto;
//import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
//import kr.co.syrup.adreport.web.event.service.ArEventService;
//import lombok.extern.slf4j.Slf4j;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.map.ObjectWriter;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
//import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.io.IOException;
//import java.util.*;
//
//@Slf4j
//@WebAppConfiguration
//@SpringBootTest
//@ActiveProfiles("local")
//@AutoConfigureCache
//@AutoConfigureDataJpa
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestEntityManager
//@ImportAutoConfiguration
//@AutoConfigureMockMvc
//class WebEventControllerTest {
//
//    @Autowired
//    private SurveyGoStaticsService surveyGoStaticsService;
//
//    @Autowired
//    private ArEventService arEventService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void savePhotoEvent() {
//        EventSaveDto saveDto = new EventSaveDto();
//        EventBaseDto eventBaseInfo = new EventBaseDto();
//        EventDto arEventInfo = new EventDto();
//        EventButtonDto arEventButtonInfo = new EventButtonDto();
//        List<EventWinningDto> arEventWinningInfo = new ArrayList<>();
//        List<EventWinningButtonAddReqDto> arEventWinningButtonAddInfo = new ArrayList<>();
//
//        List<EventHtmlDto> arEventHtmlInfo = new ArrayList<>();
//        PhotoLogicalReqDto photoLogicalInfo = new PhotoLogicalReqDto();
//        PhotoContentsListReqDto photoContentsInfo = new PhotoContentsListReqDto();
//        OcbPointSaveReqDto ocbPointSaveInfo = new OcbPointSaveReqDto();
//
//        //EVENT_BASE_INFO
//        eventBaseInfo.setEventTitle("이벤트 제목1234");
//        eventBaseInfo.setContractStatus(ContractStatusDefine.시버스진행.code());
//        eventBaseInfo.setEventType(EventTypeDefine.PHOTO.name());
//        eventBaseInfo.setEventStartDate("2023-01-01");
//        eventBaseInfo.setEventEndDate("2023-12-31");
//
//        //AR_EVENT
//        arEventInfo.setEventLogicalType(EventLogicalTypeDefine.기본형.value());
//        arEventInfo.setLocationSettingYn(false);
//        arEventInfo.setArAttendConditionAllYn(false);
//        arEventInfo.setArAttendConditionSpecialLocationYn(false);
//        arEventInfo.setArAttendConditionHourlyYn(false);
//        arEventInfo.setArAttendConditionCodeYn(false);
//        arEventInfo.setArAttendTermType(null);
//        arEventInfo.setArBgImage("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaUrLRq79d905baedd05a1f853494d342b893cc.jpg");
//        arEventInfo.setArSkinImage("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaUrN0ze89460e565de2f6a521a6392dd1f7946.png");
//        arEventInfo.setDuplicateWinningType(StringDefine.N.name());
//        arEventInfo.setDuplicateWinningLimitType(0);
//        arEventInfo.setDuplicateWinningCount(0);
//        arEventInfo.setWinningPasswordYn(StringDefine.N.name());
//        arEventInfo.setInformationProvisionAgreementTextSetting(StringDefine.N.name());
//        arEventInfo.setAttendConditionMdnYn(false);
//        arEventInfo.setAttendConditionTargetYn(false);
//        arEventInfo.setWinningSearchType("MDN");
//        arEventInfo.setSmsAuthUseYn(StringDefine.N.name());
//        arEventInfo.setOcbPointSaveType(OcbPointSaveTypeDefine.PREV.name());
//        arEventInfo.setLoadingImgYn("N");
//
//        //AR_EVENT_BUTTON
//        arEventButtonInfo.setArButtonBgColorAssignType("BASIC");
//        arEventButtonInfo.setArButtonColorAssignType("BASIC");
//        arEventButtonInfo.setArButtonTextColorAssignType("BASIC");
//        arEventButtonInfo.setArButtonText("참여하기");
//
//        //AR_EVENT_WINNING
//        EventWinningDto winningInfo = new EventWinningDto();
//        winningInfo.setEventWinningSort(1);
//        winningInfo.setObjectMappingType("N");
//        winningInfo.setWinningType(WinningTypeDefine.OCB포인트.code());
//        winningInfo.setWinningTimeType("N");
//        winningInfo.setTotalWinningNumber(1000);
//        winningInfo.setDayWinningNumber(10);
//        winningInfo.setHourWinningNumber(10);
//        winningInfo.setWinningPercent("90");
//        winningInfo.setWinningImageUrl("https://sodarimg.syrup.co.kr/is/marketing/202208/17RHwYVEsB99557b5fec3f0ab14d3d6e6db4d2ae49.png");
//        winningInfo.setProductName("OCB포인트 상품");
//        winningInfo.setAttendCodeWinningType("N");
//        winningInfo.setSubscriptionYn("N");
//        winningInfo.setEtcDescImgSettingYn("Y");
//        winningInfo.setEtcDescImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202208/17RHwYVEsB99557b5fec3f0ab14d3d6e6db4d2ae49.png");
//        winningInfo.setOcbCouponId("ABCDEFGHIJK");
//
//        //AR_EVENT_WINNING_BUTTON
//        List<EventWinningButtonDto> arEventWinningButtonInfo = new ArrayList<>();
//        EventWinningButtonDto winningButtonDto = new EventWinningButtonDto();
//        winningButtonDto.setButtonSort(1);
//        winningButtonDto.setButtonActionType("DELIVERY");
//        winningButtonDto.setButtonText("당첨버튼");
//        winningButtonDto.setDeliveryNameYn(true);
//        winningButtonDto.setDeliveryPhoneNumberYn(true);
//        winningButtonDto.setDeliveryAddressYn(false);
//
//        //AR_EVENT_WINNING_BUTTON_ADD
//        EventWinningButtonAddReqDto buttonAddReqDto = new EventWinningButtonAddReqDto();
//        buttonAddReqDto.setFieldName("추가 필드이름");
//        buttonAddReqDto.setFieldType(TextFieldTypeDefine.INT.name());
//        buttonAddReqDto.setFieldLength(10);
//        arEventWinningButtonAddInfo.add(buttonAddReqDto);
//
//        winningButtonDto.setArEventWinningButtonAddInfo(arEventWinningButtonAddInfo);
//
//        arEventWinningButtonInfo.add(winningButtonDto);
//        winningInfo.setArEventWinningButtonInfo(arEventWinningButtonInfo);
//
//        //OCB_POINT_SAVE(winning)
//        OcbPointSaveReqDto winningOcbInfo = new OcbPointSaveReqDto();
//        winningOcbInfo.setOcbPointSaveCode("0123456789");
//        winningOcbInfo.setBusinessNumber("123-12-12345");
//        winningOcbInfo.setSaveTermType(1);
//        winningOcbInfo.setSaveMaxCustomerCount(200);
//        winningOcbInfo.setSavePoint(2);
//        winningInfo.setOcbPointSaveInfo(winningOcbInfo);
//
//        arEventWinningInfo.add(winningInfo);
//
//
//        //AR_EVENT_HTML
//        EventHtmlDto htmlDto = new EventHtmlDto();
//        htmlDto.setHtmlType("BUTTON");
//        htmlDto.setHtmlTypeSort(1);
//        htmlDto.setHtmlButtonType(HtmlButtonTypeDefine.CPREPO.name());
//        htmlDto.setDeviceLocationFindSettingYn("Y");
//        htmlDto.setDeviceLocationFindButtonText("위치찾기버튼");
//        htmlDto.setLocationFindExposureType(LocationFindExposureTypeDefine.MAP.name());
//        htmlDto.setFreePrintControlYn("Y");
//        htmlDto.setFreePrintCustomerCount(10000);
//
//        //AR_EVENT_DEVICE_GPS
//        List<EventDeviceGpsReqDto> gpsList = new ArrayList<>();
//        EventDeviceGpsReqDto gpsReqDto = new EventDeviceGpsReqDto();
//        gpsReqDto.setSort(1);
//        gpsReqDto.setDeviceName("디바이스명");
//        gpsReqDto.setGpsName("위치이름8887");
//        gpsReqDto.setDeviceGpsLatitude("37.5555");
//        gpsReqDto.setDeviceGpsLongitude("128.93048");
//        gpsList.add(gpsReqDto);
//
//        htmlDto.setArEventDeviceGpsInfo(gpsList);
//
//        arEventHtmlInfo.add(htmlDto);
//
//        //AR_PHOTO_LOGICAL
//        photoLogicalInfo.setTutorialYn("N");
//        photoLogicalInfo.setTabMenuTitle("asjdlajdsljkda");
//        photoLogicalInfo.setFilmResultImgUrl("https://alsdjlaskdjlaksjdlask123j.com");
//        photoLogicalInfo.setHashTagSettingYn("Y");
//        photoLogicalInfo.setHashTagValue(Arrays.asList("해시태그1","해시태그2","해시태그3","해시태그4"));
//        photoLogicalInfo.setAgreePopupText("동의팝업 문구");
//        photoLogicalInfo.setAgreePopupDetailLinkUrl("https://alsdjlaskdjlaksjdlask1ldfsljkf.com");
//        photoLogicalInfo.setAgreePopupInputText("동의팝업 입력문구");
//        photoLogicalInfo.setPhotoPrintButtonText("포토촬영버튼문구");
//        photoLogicalInfo.setPhotoGiveAwayButtonText("포토 당첨결과버튼문구1234");
//        photoLogicalInfo.setFilmResultFooterImgSettingYn("Y");
//        photoLogicalInfo.setFilmResultFooterImgUrl("cdn://alsdjlakjsdalskdjalskdja");
//
//        //AR_PHOTO_CONTENTS
//        List<PhotoContentsReqDto> frameContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> tabContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> filterContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> characterContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> stickerContentsInfo = new ArrayList<>();
//
//        PhotoContentsReqDto contentsReqDto1 = new PhotoContentsReqDto();
//        contentsReqDto1.setSort(1);
//        contentsReqDto1.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto1.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto1.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto1.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto1.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        frameContentsInfo.add(contentsReqDto1);
//        photoContentsInfo.setFrameContentsInfo(frameContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto2 = new PhotoContentsReqDto();
//        contentsReqDto2.setSort(1);
//        contentsReqDto2.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto2.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto2.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto2.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto2.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        tabContentsInfo.add(contentsReqDto2);
//        photoContentsInfo.setTabContentsInfo(tabContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto3 = new PhotoContentsReqDto();
//        contentsReqDto3.setSort(1);
//        contentsReqDto3.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto3.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto3.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto3.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto3.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        filterContentsInfo.add(contentsReqDto3);
//        photoContentsInfo.setFilterContentsInfo(filterContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto4 = new PhotoContentsReqDto();
//        contentsReqDto4.setSort(1);
//        contentsReqDto4.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto4.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto4.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto4.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto4.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        characterContentsInfo.add(contentsReqDto4);
//        photoContentsInfo.setCharacterContentsInfo(characterContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto5 = new PhotoContentsReqDto();
//        contentsReqDto5.setSort(1);
//        contentsReqDto5.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto5.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto5.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto5.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto5.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        stickerContentsInfo.add(contentsReqDto5);
//        photoContentsInfo.setStickerContentsInfo(stickerContentsInfo);
//
//        ocbPointSaveInfo.setOcbPointSaveCode("0123456789");
//        ocbPointSaveInfo.setBusinessNumber("123-12-12345");
//        ocbPointSaveInfo.setSaveTermType(1);
//        ocbPointSaveInfo.setSaveMaxCustomerCount(100);
//        ocbPointSaveInfo.setSavePoint(2);
//
//        saveDto.setEventBaseInfo(eventBaseInfo);
//        saveDto.setArEventInfo(arEventInfo);
//        saveDto.setArEventButtonInfo(arEventButtonInfo);
//        saveDto.setArEventWinningInfo(arEventWinningInfo);
//        saveDto.setArEventHtmlInfo(arEventHtmlInfo);
//        saveDto.setPhotoLogicalInfo(photoLogicalInfo);
//        saveDto.setPhotoContentsInfo(photoContentsInfo);
//        saveDto.setOcbPointSaveInfo(ocbPointSaveInfo);
//
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//
//        String requestJson = "";
//        try {
//            requestJson = ow.writeValueAsString(saveDto);
//        } catch (IOException ioe) {
//            log.error(ioe.getMessage());
//        }
//
//        try {
//            Map<String, String> input = new HashMap<>();
//            input.put("jsonStr", requestJson);
//            MockMultipartFile jsonFile = new MockMultipartFile("jsonStr", "", "application/json", requestJson.getBytes());
//            MvcResult mvcResult = mockMvc.perform(
//                    MockMvcRequestBuilders.multipart("/api/v1/web-event/photo/save")
//                            .file(jsonFile))
//                            .andReturn();
//
//            JsonObject resultJsonObj = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), JsonObject.class);
//
//            log.info("resultJsonObj", resultJsonObj.toString());
//
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @Test
//    void updatePhotoEvent() {
//        EventSaveDto saveDto = new EventSaveDto();
//        EventBaseDto eventBaseInfo = new EventBaseDto();
//        EventDto arEventInfo = new EventDto();
//        EventButtonDto arEventButtonInfo = new EventButtonDto();
//        List<EventWinningDto> arEventWinningInfo = new ArrayList<>();
//        List<EventWinningButtonAddReqDto> arEventWinningButtonAddInfo = new ArrayList<>();
//
//        List<EventHtmlDto> arEventHtmlInfo = new ArrayList<>();
//        PhotoLogicalReqDto photoLogicalInfo = new PhotoLogicalReqDto();
//        PhotoContentsListReqDto photoContentsInfo = new PhotoContentsListReqDto();
//        OcbPointSaveReqDto ocbPointSaveInfo = new OcbPointSaveReqDto();
//
//        //EVENT_BASE_INFO
//        eventBaseInfo.setEventTitle("이벤트 제목12345");
//        eventBaseInfo.setContractStatus(ContractStatusDefine.시버스진행.code());
//        eventBaseInfo.setEventType(EventTypeDefine.PHOTO.name());
//        eventBaseInfo.setEventStartDate("2023-01-01");
//        eventBaseInfo.setEventEndDate("2023-12-31");
//
//        //AR_EVENT
//        arEventInfo.setEventLogicalType(EventLogicalTypeDefine.기본형.value());
//        arEventInfo.setLocationSettingYn(false);
//        arEventInfo.setArAttendConditionAllYn(false);
//        arEventInfo.setArAttendConditionSpecialLocationYn(false);
//        arEventInfo.setArAttendConditionHourlyYn(false);
//        arEventInfo.setArAttendConditionCodeYn(false);
//        arEventInfo.setArAttendTermType(null);
//        arEventInfo.setArBgImage("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaUrLRq79d905baedd05a1f853494d342b893cc.jpg");
//        arEventInfo.setArSkinImage("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaUrN0ze89460e565de2f6a521a6392dd1f7946.png");
//        arEventInfo.setDuplicateWinningType(StringDefine.N.name());
//        arEventInfo.setDuplicateWinningLimitType(0);
//        arEventInfo.setDuplicateWinningCount(0);
//        arEventInfo.setWinningPasswordYn(StringDefine.N.name());
//        arEventInfo.setInformationProvisionAgreementTextSetting(StringDefine.N.name());
//        arEventInfo.setAttendConditionMdnYn(false);
//        arEventInfo.setAttendConditionTargetYn(false);
//        arEventInfo.setWinningSearchType("MDN");
//        arEventInfo.setSmsAuthUseYn(StringDefine.N.name());
//        arEventInfo.setOcbPointSaveType(OcbPointSaveTypeDefine.NONE.name());
//        arEventInfo.setLoadingImgYn("Y");
//        arEventInfo.setLoadingImgUrl("https://lasjdalskjdaldskjalkds.com");
//        arEventInfo.setEventExposureType(EventExposureTypeDefine.OCB.name());
//
//        //AR_EVENT_BUTTON
//        arEventButtonInfo.setArButtonBgColorAssignType("BASIC");
//        arEventButtonInfo.setArButtonColorAssignType("BASIC");
//        arEventButtonInfo.setArButtonTextColorAssignType("BASIC");
//        arEventButtonInfo.setArButtonText("참여하기");
//
//        //AR_EVENT_WINNING
//        EventWinningDto winningInfo = new EventWinningDto();
//        winningInfo.setArEventWinningId(1144);
//        winningInfo.setEventWinningSort(1);
//        winningInfo.setObjectMappingType("N");
//        winningInfo.setWinningType(WinningTypeDefine.OCB포인트.code());
//        winningInfo.setWinningTimeType("N");
//        winningInfo.setTotalWinningNumber(1000);
//        winningInfo.setDayWinningNumber(10);
//        winningInfo.setHourWinningNumber(10);
//        winningInfo.setWinningPercent("90");
//        winningInfo.setWinningImageUrl("https://sodarimg.syrup.co.kr/is/marketing/202208/17RHwYVEsB99557b5fec3f0ab14d3d6e6db4d2ae49.png");
//        winningInfo.setProductName("OCB포인트 상품");
//        winningInfo.setAttendCodeWinningType("N");
//        winningInfo.setSubscriptionYn("N");
//        winningInfo.setEtcDescImgSettingYn("Y");
//        winningInfo.setEtcDescImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202208/17RHwYVEsB99557b5fec3f0ab14d3d6e6db4d2ae49.png");
//        winningInfo.setOcbCouponId("ABCDEFGHIJK");
//
//        //AR_EVENT_WINNING_BUTTON
//        List<EventWinningButtonDto> arEventWinningButtonInfo = new ArrayList<>();
//        EventWinningButtonDto winningButtonDto = new EventWinningButtonDto();
//        winningButtonDto.setArEventWinningButtonId(1834);
//        winningButtonDto.setButtonSort(1);
//        winningButtonDto.setButtonActionType("DELIVERY");
//        winningButtonDto.setButtonText("당첨버튼");
//        winningButtonDto.setDeliveryNameYn(true);
//        winningButtonDto.setDeliveryPhoneNumberYn(true);
//        winningButtonDto.setDeliveryAddressYn(false);
//
//        //AR_EVENT_WINNING_BUTTON_ADD
//        EventWinningButtonAddReqDto buttonAddReqDto = new EventWinningButtonAddReqDto();
//        buttonAddReqDto.setId(3l);
//        buttonAddReqDto.setFieldName("추가 필드이름");
//        buttonAddReqDto.setFieldType(TextFieldTypeDefine.INT.name());
//        buttonAddReqDto.setFieldLength(10);
//        arEventWinningButtonAddInfo.add(buttonAddReqDto);
//
//        winningButtonDto.setArEventWinningButtonAddInfo(arEventWinningButtonAddInfo);
//
//        arEventWinningButtonInfo.add(winningButtonDto);
//        winningInfo.setArEventWinningButtonInfo(arEventWinningButtonInfo);
//
//        //OCB_POINT_SAVE(winning)
//        OcbPointSaveReqDto winningOcbInfo = new OcbPointSaveReqDto();
//        winningOcbInfo.setId(15);
//        winningOcbInfo.setOcbPointSaveCode("987456321");
//        winningOcbInfo.setBusinessNumber("123-12-12345");
//        winningOcbInfo.setSaveTermType(1);
//        winningOcbInfo.setSaveMaxCustomerCount(200);
//        winningOcbInfo.setSavePoint(2);
//        winningInfo.setOcbPointSaveInfo(winningOcbInfo);
//
//        arEventWinningInfo.add(winningInfo);
//
//
//        //AR_EVENT_HTML
//        EventHtmlDto htmlDto = new EventHtmlDto();
//        htmlDto.setEventHtmlId(987);
//        htmlDto.setHtmlType("BUTTON");
//        htmlDto.setHtmlTypeSort(1);
//        htmlDto.setHtmlButtonType(HtmlButtonTypeDefine.CPREPO.name());
//        htmlDto.setDeviceLocationFindSettingYn("Y");
//        htmlDto.setDeviceLocationFindButtonText("위치찾기버튼");
//        htmlDto.setLocationFindExposureType(LocationFindExposureTypeDefine.MAP.name());
//        htmlDto.setFreePrintControlYn("Y");
//        htmlDto.setFreePrintCustomerCount(10000);
//
//        //AR_EVENT_DEVICE_GPS
//        List<EventDeviceGpsReqDto> gpsList = new ArrayList<>();
//        EventDeviceGpsReqDto gpsReqDto = new EventDeviceGpsReqDto();
//        gpsReqDto.setId(8);
//        gpsReqDto.setSort(1);
//        gpsReqDto.setDeviceName("디바이스명");
//        gpsReqDto.setGpsName("위치이름8887");
//        gpsReqDto.setDeviceGpsLatitude("37.5555");
//        gpsReqDto.setDeviceGpsLongitude("128.93048");
//        gpsList.add(gpsReqDto);
//
//        htmlDto.setArEventDeviceGpsInfo(gpsList);
//
//        arEventHtmlInfo.add(htmlDto);
//
//        //AR_PHOTO_LOGICAL
//        photoLogicalInfo.setId(6);
//        photoLogicalInfo.setTutorialYn("Y");
//        photoLogicalInfo.setTabMenuTitle("2938402wekjdfl");
//        photoLogicalInfo.setFilmResultImgUrl("https://alsdjlaskdjlaksjdlask123j.com");
//        photoLogicalInfo.setHashTagSettingYn("Y");
//        photoLogicalInfo.setHashTagValue(Arrays.asList("해시태그1","해시태그2","해시태그3","해시태그4"));
//        photoLogicalInfo.setAgreePopupText("동의팝업 문구");
//        photoLogicalInfo.setAgreePopupDetailLinkUrl("https://alsdjlaskdjlaksjdlask1ldfsljkf.com");
//        photoLogicalInfo.setAgreePopupInputText("동의팝업 입력문구");
//        photoLogicalInfo.setPhotoPrintButtonText("포토촬영버튼문구");
//        photoLogicalInfo.setPhotoGiveAwayButtonText("포토 당첨결과버튼문구1234");
//        photoLogicalInfo.setFilmResultFooterImgSettingYn("Y");
//        photoLogicalInfo.setFilmResultFooterImgUrl("cdn://alsdjlakjsdalskdjalskdja");
//
//        //AR_PHOTO_CONTENTS
//        List<PhotoContentsReqDto> frameContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> tabContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> filterContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> characterContentsInfo = new ArrayList<>();
//        List<PhotoContentsReqDto> stickerContentsInfo = new ArrayList<>();
//
//        PhotoContentsReqDto contentsReqDto1 = new PhotoContentsReqDto();
//        contentsReqDto1.setId(24l);
//        contentsReqDto1.setSort(5);
//        contentsReqDto1.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto1.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto1.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto1.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto1.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        frameContentsInfo.add(contentsReqDto1);
//        photoContentsInfo.setFrameContentsInfo(frameContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto2 = new PhotoContentsReqDto();
//        contentsReqDto2.setId(25l);
//        contentsReqDto2.setSort(4);
//        contentsReqDto2.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto2.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto2.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto2.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto2.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        tabContentsInfo.add(contentsReqDto2);
//        photoContentsInfo.setTabContentsInfo(tabContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto3 = new PhotoContentsReqDto();
//        contentsReqDto3.setId(26l);
//        contentsReqDto3.setSort(3);
//        contentsReqDto3.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto3.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto3.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto3.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto3.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        filterContentsInfo.add(contentsReqDto3);
//        photoContentsInfo.setFilterContentsInfo(filterContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto4 = new PhotoContentsReqDto();
//        contentsReqDto4.setId(27l);
//        contentsReqDto4.setSort(2);
//        contentsReqDto4.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto4.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto4.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto4.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto4.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        characterContentsInfo.add(contentsReqDto4);
//        photoContentsInfo.setCharacterContentsInfo(characterContentsInfo);
//
//        PhotoContentsReqDto contentsReqDto5 = new PhotoContentsReqDto();
//        contentsReqDto5.setId(28l);
//        contentsReqDto5.setSort(1);
//        contentsReqDto5.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
//        contentsReqDto5.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
//        contentsReqDto5.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
//        contentsReqDto5.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
//        contentsReqDto5.setPhotoContentTabMenuType(PhotoContentTypeDefine.FRAME.name());
//        stickerContentsInfo.add(contentsReqDto5);
//        photoContentsInfo.setStickerContentsInfo(stickerContentsInfo);
//
//        ocbPointSaveInfo.setId(14);
//        ocbPointSaveInfo.setOcbPointSaveCode("0123456789");
//        ocbPointSaveInfo.setBusinessNumber("123-12-12345");
//        ocbPointSaveInfo.setSaveTermType(1);
//        ocbPointSaveInfo.setSaveMaxCustomerCount(100);
//        ocbPointSaveInfo.setSavePoint(2);
//
//        saveDto.setEventBaseInfo(eventBaseInfo);
//        saveDto.setArEventInfo(arEventInfo);
//        saveDto.setArEventButtonInfo(arEventButtonInfo);
//        saveDto.setArEventWinningInfo(arEventWinningInfo);
//        saveDto.setArEventHtmlInfo(arEventHtmlInfo);
//        saveDto.setPhotoLogicalInfo(photoLogicalInfo);
//        saveDto.setPhotoContentsInfo(photoContentsInfo);
//        saveDto.setOcbPointSaveInfo(ocbPointSaveInfo);
//
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//
//        String requestJson = "";
//        try {
//            requestJson = ow.writeValueAsString(saveDto);
//        } catch (IOException ioe) {
//            log.error(ioe.getMessage());
//        }
//
//        try {
//            Map<String, String> input = new HashMap<>();
//            String eventId = "000460";
//            input.put("jsonStr", requestJson);
//            MockMultipartFile jsonFile = new MockMultipartFile("jsonStr", "", "application/json", requestJson.getBytes());
//            MockMultipartFile eventIdFile = new MockMultipartFile("eventId", "", "application/json", eventId.getBytes());
//            MvcResult mvcResult = mockMvc.perform(
//                            MockMvcRequestBuilders.multipart("/api/v1/web-event/photo/update")
//                                    .file(eventIdFile)
//                                    .file(jsonFile))
//                    .andReturn();
//
//            JsonObject resultJsonObj = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), JsonObject.class);
//
//            log.info("resultJsonObj", resultJsonObj.toString());
//
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @Test
//    void testMigrationSurveyAnswer() {
//        String eventId = "000940";
//        int limitCount = 100;
//
//        ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);
//
//        List<List<Object>> answerList = surveyGoStaticsService.makeSurveyRawTableValue2(eventId, limitCount, arEvent.getArEventId());
//
//        for (List<Object> row : answerList) {
//            for (Object o : row) {
//                log.info(o.toString());
//            }
//        }
//    }
//}