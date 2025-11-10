package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.PhotoContentsListReqDto;
import kr.co.syrup.adreport.web.event.dto.request.PhotoContentsReqDto;
import kr.co.syrup.adreport.web.event.dto.request.PhotoLogicalReqDto;
import kr.co.syrup.adreport.web.event.dto.response.CacheJsonDataResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
class ArEventServiceTest {

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AES256Utils aes256Utils;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private LogService logService;

    @Autowired
    private BatchService batchService;


    @Test
    void saveAllArEventDeviceGps() {
        List<ArEventDeviceGpsEntity> list = new ArrayList<>();

        ArEventDeviceGpsEntity entity1 = new ArEventDeviceGpsEntity();
        entity1.setId(5);
        entity1.setSort(1);
        entity1.setDeviceName("디바이스명");
        entity1.setGpsName("위치이름8887");
        entity1.setDeviceGpsLatitude("37.5555");
        entity1.setDeviceGpsLongitude("128.93048");

        ArEventDeviceGpsEntity entity2 = new ArEventDeviceGpsEntity();
        entity2.setId(6);
        entity2.setSort(2);
        entity2.setDeviceName("위치이름9393");
        entity2.setGpsName("위치이름9393");
        entity2.setDeviceGpsLatitude("32.2345");
        entity2.setDeviceGpsLongitude("126.120398");

        list.add(entity1);
        list.add(entity2);

        arEventService.saveAllArEventDeviceGps(1, list);
    }

    @Test
    void saveAllArPhotoContents() {
        //List<ArPhotoContentsEntity> list = new ArrayList<>();
        List<PhotoContentsReqDto> list = new ArrayList<>();


        //ArPhotoContentsEntity entity1 = new ArPhotoContentsEntity();
        PhotoContentsReqDto entity1 = new PhotoContentsReqDto();
        //entity1.setPhotoContentType(PhotoContentTypeDefine.STICKER.name());
        entity1.setId(24l);
        entity1.setSort(1);
        entity1.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
        entity1.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f22.png");
        entity1.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f244.png");
        entity1.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f355.png");
        entity1.setPhotoContentTabMenuType(PhotoContentTypeDefine.STICKER.name());

        //ArPhotoContentsEntity entity2 = new ArPhotoContentsEntity();
        PhotoContentsReqDto entity2 = new PhotoContentsReqDto();
        entity2.setId(25l);
        //entity2.setPhotoContentType(PhotoContentTypeDefine.CHARACTER.name());
        entity2.setSort(2);
        entity2.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
        entity2.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
        entity2.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
        entity2.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
        entity2.setPhotoContentTabMenuType(PhotoContentTypeDefine.CHARACTER.name());

        PhotoContentsReqDto entity3 = new PhotoContentsReqDto();
        entity3.setId(26l);
        //entity2.setPhotoContentType(PhotoContentTypeDefine.CHARACTER.name());
        entity3.setSort(3);
        entity3.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
        entity3.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
        entity3.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
        entity3.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
        entity3.setPhotoContentTabMenuType(PhotoContentTypeDefine.STICKER.name());

        PhotoContentsReqDto entity4 = new PhotoContentsReqDto();
        entity4.setId(27l);
        //entity2.setPhotoContentType(PhotoContentTypeDefine.CHARACTER.name());
        entity4.setSort(4);
        entity4.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
        entity4.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
        entity4.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
        entity4.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
        entity4.setPhotoContentTabMenuType(PhotoContentTypeDefine.CHARACTER.name());

        PhotoContentsReqDto entity5 = new PhotoContentsReqDto();
        entity5.setId(28l);
        //entity2.setPhotoContentType(PhotoContentTypeDefine.CHARACTER.name());
        entity5.setSort(5);
        entity5.setPhotoContentChoiceType(PhotoContentChoiceTypeDefine.DIRECT.name());
        entity5.setPhotoFileName("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f11.png");
        entity5.setPhotoThumbnailImgUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f233.png");
        entity5.setPhotoOriginalFileUrl("https://sodarimg.syrup.co.kr/is/marketing/202304/17TTaWTdqc3a665d65617a1c6c190c34cc3f0a0f8f366.png");
        entity5.setPhotoContentTabMenuType(PhotoContentTypeDefine.FILTER.name());

        list.add(entity1);
        list.add(entity2);
        list.add(entity3);
        list.add(entity4);
        list.add(entity5);

        arEventService.saveAllArPhotoContents(100, "", convertPhotoContentsReqDtoToArPhotoContentsEntity(list));

    }

    @Test
    void saveEvent() {
        ArEventEntity arEventEntity = ArEventEntity.ofTest("000900", EventTypeDefine.PHOTO.name());
        arEventService.saveEvent(arEventEntity);
    }

    @Test
    void updateArEvent() {
        ArEventEntity arEventEntity = arEventService.findArEventById(482);
        arEventEntity.setLoadingImgUrl("http://askdalksjdlaksjd111.com");
        arEventService.updateArEvent(arEventEntity);
    }

    @Test
    public void testSaveOcbPointSave() {
        OcbPointSaveEntity entity = new OcbPointSaveEntity();
        entity.setId(1);
        entity.setOcbPointSaveCode("0123456789");
        entity.setBusinessNumber("123-12-12345");
        entity.setSaveTermType(1);
        entity.setSaveMaxCustomerCount(200);
        entity.setSavePoint(2);

        arEventService.saveOcbPointSave(482, entity);
    }

    @Test
    void saveArPhotoLogical() {
        //ArPhotoLogicalEntity entity = new ArPhotoLogicalEntity();
        PhotoLogicalReqDto entity = new PhotoLogicalReqDto();
        entity.setId(1);
        entity.setTutorialYn("Y");
        entity.setTabMenuTitle("asjdlajdsljkda");
        entity.setFilmResultImgUrl("https://alsdjlaskdjlaksjdlask123j.com");
        entity.setHashTagSettingYn("Y");
        entity.setHashTagValue(Arrays.asList("해시태그1","해시태그2","해시태그3","해시태그4"));
        entity.setAgreePopupText("동의팝업 문구");
        entity.setAgreePopupDetailLinkUrl("https://alsdjlaskdjlaksjdlask1ldfsljkf.com");
        entity.setAgreePopupInputText("동의팝업 입력문구");
        entity.setPhotoPrintButtonText("포토촬영버튼문구");
        entity.setPhotoGiveAwayButtonText("포토 당첨결과버튼문구1234");
        entity.setFilmResultFooterImgSettingYn("Y");
        entity.setFilmResultFooterImgUrl("cdn://alsdjlakjsdalskdjalskdja");
        arEventService.saveArPhotoLogical(482, modelMapper.map(entity, ArPhotoLogicalEntity.class));
    }



    @Test
    void findArPhotoContentsByArEventId() {
        List<ArPhotoContentsEntity> list = arEventService.findArPhotoContentsByArEventId(494);
        if (!PredicateUtils.isNullList(list)) {
            TreeMap<String, List<ArPhotoContentsEntity>> treeMap = list.stream()
                    .sorted(Comparator.comparing(ArPhotoContentsEntity::getSort))
                    .collect(Collectors.groupingBy(ArPhotoContentsEntity::getPhotoContentType, TreeMap::new, Collectors.toList()));

            log.info("treeMap");
            PhotoContentsListReqDto photoContentsInfo = new PhotoContentsListReqDto();

            treeMap.forEach((key, value) -> {
               if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.FRAME.name())) {
                   photoContentsInfo.setFrameContentsInfo(convertArPhotoContentsEntityToPhotoContentsReqDto(value));
               }
                if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.TAB.name())) {
                    photoContentsInfo.setTabContentsInfo(convertArPhotoContentsEntityToPhotoContentsReqDto(value));
                }
                if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.FILTER.name())) {
                    photoContentsInfo.setFilterContentsInfo(convertArPhotoContentsEntityToPhotoContentsReqDto(value));
                }
                if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.CHARACTER.name())) {
                    photoContentsInfo.setCharacterContentsInfo(convertArPhotoContentsEntityToPhotoContentsReqDto(value));
                }
                if (PredicateUtils.isEqualsStr(key, PhotoContentTypeDefine.STICKER.name())) {
                    photoContentsInfo.setStickerContentsInfo(convertArPhotoContentsEntityToPhotoContentsReqDto(value));
                }

            });
            log.info(">>>>>>>" + photoContentsInfo.toString());
        }
        log.info("1234");
    }

    @Test
    void encryptTest() {
        System.out.println(aes256Utils.encrypt("01096663779"));
    }

    @Test
    void asyncTest() {
        CompletableFuture.supplyAsync(() -> asyncTestReturn())
                .thenAccept(s -> {
                    try {
                        if (PredicateUtils.isNotNull(s)) {
                            log.info("s value >>> " + s.get().get("test"));
                        } else {
                            log.info("async fail");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
        log.info("f");
    }

    @Test
    void objectExposureTest() {
    }

    @Test
    void eventWinningTest() {
//        LinkedList<ArEventWinningEntity> winningLinkedList = arEventService.findArEventWinningListByArEventIdAndSubscriptionYn(738, false, true);
//        List<CacheJsonDataListResDto>resDtoList = new ArrayList<>();
//
//        for (ArEventWinningEntity winning : winningLinkedList) {
//            String jsonStr = GsonUtils.getJsonStringAsObject(winning);
//            String endcodeStr = StringCompression.base64Encode(jsonStr);
//            CacheJsonDataListResDto resDto = new CacheJsonDataListResDto().builder()
//                    .eventId("000691").jsonType("WINNING").fieldId(String.valueOf(winning.getArEventWinningId())).jsonValue(jsonStr)
//                    .build();
//            resDtoList.add(resDto);
//        }
//        cacheJsonService.saveCacheJsonDataList(resDtoList);
          //LinkedList<ArEventWinningEntity> list = cacheJsonService.findArEventWinningFailLinkedListByEventId("000691");
          //log.info(">>" + list);

    }

    private CompletableFuture<Map<String, Object>> asyncTestReturn()  {
        log.info("1234");
        Map<String, Object>resultMap = new HashMap<>();
        resultMap.put("test", 12345);
        CompletableFuture<Map<String, Object>> future = CompletableFuture.completedFuture(resultMap);
        try {
            log.info("66666");
        } catch (Exception e) {
            e.getMessage();
        }
        return future;
    }

    private List<ArPhotoContentsEntity> convertPhotoContentsReqDtoToArPhotoContentsEntity(List<PhotoContentsReqDto>photoContentsReqDtoList) {
        if (!PredicateUtils.isNullList(photoContentsReqDtoList)) {
            return photoContentsReqDtoList
                    .stream()
                    .map(dto -> modelMapper.map(dto, ArPhotoContentsEntity.class))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<PhotoContentsReqDto> convertArPhotoContentsEntityToPhotoContentsReqDto(List<ArPhotoContentsEntity>arPhotoContentsEntityList) {
        if (!PredicateUtils.isNullList(arPhotoContentsEntityList)) {
            return arPhotoContentsEntityList
                    .stream()
                    .map(dto -> modelMapper.map(dto, PhotoContentsReqDto.class))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Test
    void saveAllArEventWinningButtonAdd() {
        List<ArEventWinningButtonAddEntity>entityList = new ArrayList<>();
        ArEventWinningButtonAddEntity entity = new ArEventWinningButtonAddEntity();
        entity.setId(185l);
        entity.setFieldName("영수증번호2");
        entity.setFieldType("INT");
        entityList.add(entity);

        ArEventWinningButtonAddEntity entity2 = new ArEventWinningButtonAddEntity();
        entity2.setId(186l);
        entity2.setFieldName("영수증번호3");
        entity2.setFieldType("VARCHAR");
        entityList.add(entity2);
        arEventService.saveAllArEventWinningButtonAdd(2424, entityList);
    }

    void Test1() {
//        EventLogAttendButtonEntity entity = new E
//        logService.saveEventLogAttendButtonCompletableFuture();
    }

    @Test
    void saveBulkArEventNftCouponTemp() {
        int seq = arEventService.findWebEventSequence("coupon_info_temp_seq");
        List<String> couponList = Arrays.asList("AA", "BB", "CC", "DD", "EE", "FF");
        batchService.saveBulkArEventNftCouponTemp((long)seq, couponList);
    }

    @Test
    void testDeleteArEventCouponRepositoryByEventId() {
        String eventId = "001046";
        try {
            arEventService.deleteArEventCouponRepositoryByEventId(eventId);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }

    @Test
    void testDeleteArEventNftCouponInfoByEventIdx() {
        int eventIdx = 23;
        try {
            arEventService.deleteArEventNftCouponInfoByEventIdx("STAMP", eventIdx);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }
}
