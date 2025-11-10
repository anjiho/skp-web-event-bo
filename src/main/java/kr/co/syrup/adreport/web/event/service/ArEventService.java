package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.common.annotation.InjectCreatedModifyName;
import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.define.ContractStatusDefine;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.*;
import kr.co.syrup.adreport.web.event.dto.response.*;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.entity.repository.*;
import kr.co.syrup.adreport.web.event.logic.EventWinning;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArEventService {

    /**************************************************** Autowired START ****************************************************/
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventBaseEntityRepository eventBaseEntityRepository;

    @Autowired
    private ArEventEntityRepository arEventEntityRepository;

    @Autowired
    private ArEventButtonEntityRepository arEventButtonEntityRepository;

    @Autowired
    private ArEventObjectEntityRepository arEventObjectEntityRepository;

    @Autowired
    private ArEventLogicalEntityRepository arEventLogicalEntityRepository;

    @Autowired
    private ArEventCategoryEntityRepository arEventCategoryEntityRepository;

    @Autowired
    private ArEventScanningImageEntityRepository arEventScanningImageEntityRepository;

    @Autowired
    private ArEventWinningEntityRepository arEventWinningEntityRepository;

    @Autowired
    private ArEventWinningButtonEntityRepository arEventWinningButtonEntityRepository;

    @Autowired
    private ArEventAttendTimeEntityRepository arEventAttendTimeEntityRepository;

    @Autowired
    private ArEventHtmlEntityRepository arEventHtmlEntityRepository;

    @Autowired
    private ArEventGateCodeEntityRepository arEventGateCodeEntityRepository;

    @Autowired
    private WebEventIpAccessRepository webEventIpAccessRepository;

    @Autowired
    private XtransReceiverEntityRepository xtransReceiverEntityRepository;

    @Autowired
    private ArEventNftBenefitRepository arEventNftBenefitRepository;

    @Autowired
    private ArEventNftBannerRepository arEventNftBannerRepository;

    @Autowired
    private ArEventNftRepositoryRepository arEventNftRepositoryRepository;

    @Autowired
    private ArEventNftTokenInfoRepository arEventNftTokenInfoRepository;

    @Autowired
    private ArEventNftCouponInfoEntityRepository arEventNftCouponInfoEntityRepository;

    @Autowired
    private ArEventNftCouponRepositoryEntityRepository arEventNftCouponRepositoryEntityRepository;

    @Autowired
    private ArEventWinningTextEntityRepository arEventWinningTextEntityRepository;

    @Autowired
    private ArEventRepositoryButtonEntityRepository arEventRepositoryButtonEntityRepository;

    @Autowired
    private ArEventDeviceGpsEntityRepository arEventDeviceGpsEntityRepository;

    @Autowired
    private ArPhotoContentsEntityRepository arPhotoContentsEntityRepository;

    @Autowired
    private ArPhotoLogicalEntityRepository arPhotoLogicalEntityRepository;

    @Autowired
    private OcbPointSaveEntityRepository ocbPointSaveEntityRepository;

    @Autowired
    private ArEventWinningButtonAddEntityRepository arEventWinningButtonAddEntityRepository;

    @Autowired
    private CommonSettingsEntityRepository commonSettingsEntityRepository;

    @Autowired
    private ArEventMapper arEventMapper;

    @Autowired
    private EventWinning eventWinning;
    /****************************************************   Autowired END   ****************************************************/


    /**************************************************** SAVE,UPDATE START ****************************************************/
    /**
     * WEB_EVENT_BASE 정보 저장
     * @param webEventBaseEntity
     * @return
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public String saveEventBase(WebEventBaseEntity webEventBaseEntity) {
        String eventId = null;
        try {
            eventBaseEntityRepository.save(webEventBaseEntity);
            eventId = eventBaseEntityRepository.findFirstByOrderByIdDesc().getEventId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return eventId;
    }

    /**
     * AR_EVENT 정보 저장
     * @param arEventEntity
     * @return
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public int saveEvent(ArEventEntity arEventEntity) {
        int arEventId = 0;
        try {
            arEventEntityRepository.save(arEventEntity);
            arEventId = arEventEntityRepository.findFirstByOrderByArEventIdDesc().getArEventId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return arEventId;
    }

    @Transactional
    public ArEventEntity updateArEvent(ArEventEntity arEventEntity) {
        try {
            arEventEntityRepository.save(arEventEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (PredicateUtils.isNotNull(arEventEntity.getArEventId())) {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                return arEventEntityRepository.getById(arEventEntity.getArEventId());
            } else {
                return arEventEntityRepository.findFirstByOrderByArEventIdDesc();
            }
        }
    }

    @Transactional
    public void updateArEventByMapper(ArEventEntity arEventEntity) {
        try {
            ArEventUpdateVO vo = ModelMapperUtils.convertModel(arEventEntity, ArEventUpdateVO.class);
            arEventMapper.updateArEvent(vo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * AR_EVENT_BUTTON 정보 저장
     * @param arEventButtonEntity
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveEventButton(ArEventButtonEntity arEventButtonEntity) {
        try {
            arEventButtonEntityRepository.save(arEventButtonEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * AR_EVENT_OBJECT 배열 저장
     * @param arEventObjectEntityList
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveAllArEventObject(List<ArEventObjectEntity> arEventObjectEntityList) {
        if (!arEventObjectEntityList.isEmpty()) {
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                arEventObjectEntityRepository.saveAll(arEventObjectEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * AR_EVENT_LOGICAL 저장
     * @param arEventLogicalEntity
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveEventLogical(ArEventLogicalEntity arEventLogicalEntity) {
        try {
            arEventLogicalEntityRepository.save(arEventLogicalEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * AR_EVENT_SCANNING_IMAGE 배열 저장
     * @param arEventImageScanningEntityList
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveAllEventImageScanning(List<ArEventScanningImageEntity> arEventImageScanningEntityList) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventScanningImageEntityRepository.saveAll(arEventImageScanningEntityList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * AR_EVENT_WINNING 저장
     * @param arEventWinningEntity
     * @return
     */
    @LoggingTimeFilter
    @Transactional
    public int saveEventWinning(ArEventWinningEntity arEventWinningEntity) {
        int arEventWinningId = 0;
        try {
            arEventWinningEntityRepository.save(arEventWinningEntity);
            arEventWinningId = arEventWinningEntity.getArEventWinningId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return arEventWinningId;
    }

    @Deprecated
    @Transactional
    public void saveAllEventWinningButton(List<ArEventWinningButtonEntity> arEventWinningButtonEntityList) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventWinningButtonEntityRepository.saveAll(arEventWinningButtonEntityList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * AR_EVENT_WINNING_BUTTON 저장
     * @param arEventWinningButtonEntity
     */
    @LoggingTimeFilter
    @Transactional
    public int saveEventWinningButton(ArEventWinningButtonEntity arEventWinningButtonEntity) {
        int winningButtonId = 0;
        try {
            arEventWinningButtonEntityRepository.save(arEventWinningButtonEntity);
            winningButtonId = arEventWinningButtonEntity.getArEventWinningButtonId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return winningButtonId;
    }

    /**
     * AR_EVENT_ATTEND_TIME 배열 저장
     * @param arEventId
     * @param arEventAttendTimeEntityList
     */
    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveAllEventAttendTime(int arEventId, List<ArEventAttendTimeEntity>arEventAttendTimeEntityList) {
        if (!arEventAttendTimeEntityList.isEmpty()) {
            arEventAttendTimeEntityList
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(entity -> {
                        entity.setArEventId(arEventId);
                        if (PredicateUtils.isEqualZero(entity.getAttendStartHour()) || PredicateUtils.isNull(entity.getAttendStartHour())) {
                            entity.setAttendStartHour(0);
                        }
                        if (PredicateUtils.isEqualZero(entity.getAttendEndHour()) || PredicateUtils.isNull(entity.getAttendEndHour())) {
                            entity.setAttendEndHour(0);
                        }
                    });
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                arEventAttendTimeEntityRepository.saveAll(arEventAttendTimeEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param eventId
     * @param arEventId
     * @param eventHtmlDtoList
     */
    @Transactional
    public void saveAllEventHtml(String eventId, int arEventId, List<EventHtmlDto>eventHtmlDtoList) {
        if (PredicateUtils.isNotNullList(eventHtmlDtoList)) {
            List<ArEventHtmlEntity> arEventHtmlEntityList = ModelMapperUtils.convertModelInList(eventHtmlDtoList, ArEventHtmlEntity.class);
            arEventHtmlEntityList
                    .stream()
                    .forEach(entity -> {
                        entity.setEventId(eventId);
                        entity.setArEventId(arEventId);
                        entity.setCreatedDate(DateUtils.returnNowDate());
                    });
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                arEventHtmlEntityRepository.saveAll(arEventHtmlEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Deprecated
    public ArEventHtmlEntity saveEventHtmlByReturnEntity(String eventId, int arEventId, EventHtmlDto eventHtmlDto) {
        if (PredicateUtils.isNotNull(eventHtmlDto)) {
            ArEventHtmlEntity entity = ModelMapperUtils.convertModel(eventHtmlDto, ArEventHtmlEntity.class);
            entity.setEventId(eventId);
            entity.setArEventId(arEventId);
            entity.setCreatedDate(DateUtils.returnNowDate());

            arEventHtmlEntityRepository.save(entity);

            return arEventHtmlEntityRepository.findByArEventIdOrderByEventHtmlIdDesc(arEventId).orElseGet(ArEventHtmlEntity::new);
        }
        return null;
    }

    public ArEventHtmlEntity saveFirstEventHtmlByHtmlId(String eventId, int arEventId, int stpPanId, EventHtmlDto eventHtmlDto, boolean isStamp) {
        if (PredicateUtils.isNotNull(eventHtmlDto)) {
            ArEventHtmlEntity entity = ModelMapperUtils.convertModel(eventHtmlDto, ArEventHtmlEntity.class);
            entity.setEventId(eventId);

            if (isStamp) {
                if (arEventId > 0) {
                    entity.setStpId(arEventId);
                }
                if (stpPanId > 0) {
                    entity.setStpPanId(stpPanId);
                }
            } else {
                entity.setArEventId(arEventId);
            }
            entity.setCreatedDate(DateUtils.returnNowDate());
            entity.setDeviceLocationFindSettingYn(PredicateUtils.isNull(eventHtmlDto.getDeviceLocationFindSettingYn()) ? StringDefine.N.name() : eventHtmlDto.getDeviceLocationFindSettingYn());

            try {
                arEventHtmlEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return arEventHtmlEntityRepository.findFirstByEventHtmlId(entity.getEventHtmlId()).orElseGet(ArEventHtmlEntity::new);
        }
        return null;
    }

    /**
     * AR_EVENT_GAGE_CODE 배열 저장
     * @param arEventGateCodeEntityList
     */
    @Transactional
    public void saveAllArEventGateCode(List<ArEventGateCodeEntity>arEventGateCodeEntityList) {
        if (PredicateUtils.isNotNullList(arEventGateCodeEntityList)) {
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                arEventGateCodeEntityRepository.saveAll(arEventGateCodeEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * AR_EVENT_GAGE_CODE 배치 저장
     * @param arEventGateCodeEntityList
     */
    @Deprecated
    public void saveBatchArEventGateCode(List<ArEventGateCodeEntity>arEventGateCodeEntityList) {
        String sql = "";
        if (PredicateUtils.isNotNullList(arEventGateCodeEntityList)) {
            sql = "INSERT INTO ar_event_gate_code " +
                    "( event_id,"
                    + " attend_code,"
                    + " use_yn,"
                    + " created_date )"
                    + " VALUES (?,?,?,now()) ";

            try {
                jdbcTemplate.batchUpdate(
                        sql, arEventGateCodeEntityList, 10000,
                        new ParameterizedPreparedStatementSetter<ArEventGateCodeEntity>() {
                            @Override
                            public void setValues(PreparedStatement ps, ArEventGateCodeEntity argument) throws SQLException {
                                ps.setString(1, argument.getEventId());
                                ps.setString(2, argument.getAttendCode());
                                ps.setBoolean(3, false);

                            }
                        });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * AR_EVENT_NFT_BENEFIT 리스트 저장
     * @param arEventWinningId
     * @param nftBenefitReqDtoList
     */
    @LoggingTimeFilter
    @Transactional
    public void saveAllArEventNftBenefit(int arEventWinningId, List<NftBenefitReqDto> nftBenefitReqDtoList) {
        if (!PredicateUtils.isNullList(nftBenefitReqDtoList)) {
            List<ArEventNftBenefitEntity> saveArEventNftBenefitList = new ArrayList<>();
            for (NftBenefitReqDto reqDto : nftBenefitReqDtoList) {
                saveArEventNftBenefitList.add(
                        ArEventNftBenefitEntity.saveOf(arEventWinningId, reqDto.getNftBenefitName(), reqDto.getNftBenefitDesc())
                );
            }
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                arEventNftBenefitRepository.saveAll(saveArEventNftBenefitList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveAllArEventNftBanner(int arEvent, int eventHtmlId,  List<NftBannerReqDto> nftBannerList, boolean isStamp) {
        if (!PredicateUtils.isNullList(nftBannerList)) {
            List<ArEventNftBannerEntity> saveArEventNftBannerEntityList = new ArrayList<>();
            for (NftBannerReqDto reqDto : nftBannerList) {
                //save
                if (PredicateUtils.isNull(reqDto.getArNftBannerId())) {
                    saveArEventNftBannerEntityList.add(
                            !isStamp ? ArEventNftBannerEntity.saveOf(arEvent, eventHtmlId, reqDto.getBannerImgUrl(), reqDto.getBannerTargetUrl(), reqDto.getBannerSort())
                                        : ArEventNftBannerEntity.saveStampOf(arEvent, eventHtmlId, reqDto.getBannerImgUrl(), reqDto.getBannerTargetUrl(), reqDto.getBannerSort())
                    );
                }
                //update
                if (PredicateUtils.isNotNull(reqDto.getArNftBannerId())) {
                    saveArEventNftBannerEntityList.add(
                            !isStamp ? ArEventNftBannerEntity.updateOf(arEvent, reqDto.getArNftBannerId(), eventHtmlId, reqDto.getBannerImgUrl(), reqDto.getBannerTargetUrl(), reqDto.getBannerSort(), DateUtils.returnNowDate())
                                        : ArEventNftBannerEntity.updateOfStamp(arEvent, reqDto.getArNftBannerId(), eventHtmlId, reqDto.getBannerImgUrl(), reqDto.getBannerTargetUrl(), reqDto.getBannerSort(), DateUtils.returnNowDate())
                    );
                }
            }
            try {
                // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                arEventNftBannerRepository.saveAll(saveArEventNftBannerEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveArEventNftBenefit(ArEventNftBenefitEntity arEventNftBenefitEntity) {
        if (PredicateUtils.isNotNull(arEventNftBenefitEntity)) {
            try {
                arEventNftBenefitRepository.save(arEventNftBenefitEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveArEventNftBanner(ArEventNftBannerEntity arEventNftBannerEntity) {
        if (PredicateUtils.isNotNull(arEventNftBannerEntity)) {
            try {
                arEventNftBannerRepository.save(arEventNftBannerEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * NFT 권한 이전하기
     * @param giveAwayList
     * @param nftTokenInfoList
     */
    @Transactional
    public void transNftToken(List<EventGiveAwayDeliveryEntity> giveAwayList, List<ArEventNftTokenInfoEntity> nftTokenInfoList) {
        log.info("============================ 응모 NFT 토큰 지급 시작 ================================");
        if (PredicateUtils.isNotNullList(giveAwayList) || PredicateUtils.isNotNullList(nftTokenInfoList)) {

            List<ArEventNftRepositoryEntity> saveNftRepositoryList = new ArrayList<>();
            List<ArEventNftTokenInfoEntity> updateNftTokenList = new ArrayList<>();

            if (giveAwayList.size() > nftTokenInfoList.size()) {
                log.error("당첨자 목록과 NFT 토큰 지급 개수가 일치하지 않습니다.");
            }

            if (giveAwayList.size() <= nftTokenInfoList.size()) {
                log.info("============================ 응모와 NFT 개수가 같아서 로직 시작  ================================");
                for (int i = 0; i < giveAwayList.size(); i++) {
                    ArEventNftTokenInfoEntity nftTokenInfo = nftTokenInfoList.get(i);
                    EventGiveAwayDeliveryEntity giveAwayDeliveryInfo = giveAwayList.get(i);

                    if (nftTokenInfo.getIsPayed()) {
                        log.error("transferNftToken Error :: 이미 지급 완료된 NFT 토큰 =====> TokenId >>>> " + nftTokenInfo.getNftTokenId());
                    } else {
                        log.info("============================ giveAwayId :: " + giveAwayDeliveryInfo.getGiveAwayId() + " ================================");
                        log.info("============================ nftTokenInfoId :: " + nftTokenInfo.getId() + " ================================");
                        log.info("============================ nftTokenId:: " + nftTokenInfo.getNftTokenId() + " ================================");
                        //NFT_REPOSITORY 저장 배열 담기
                        saveNftRepositoryList.add(
                                ArEventNftRepositoryEntity.saveOf(giveAwayDeliveryInfo.getGiveAwayId(), nftTokenInfo.getId())
                        );
                        //NFT_TOKEN_INFO 지급완료된 토큰 배열 담기
                        updateNftTokenList.add(
                                ArEventNftTokenInfoEntity.transferOf(nftTokenInfo)
                        );
                        try {
                            arEventMapper.updateNftTokenIsPayed(nftTokenInfo.getId());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }

                if (PredicateUtils.isNotNullList(saveNftRepositoryList)) {
                    try {
                        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                        arEventNftRepositoryRepository.saveAll(saveNftRepositoryList);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                if (PredicateUtils.isNotNullList(updateNftTokenList)) {
                    try {
                        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                        arEventNftTokenInfoRepository.saveAll(updateNftTokenList);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        log.info("============================ 응모 NFT 토큰 지급 끝 ================================");
    }

    @Transactional
    public void transNftCoupon(List<EventGiveAwayDeliveryEntity> giveAwayList, List<ArEventNftCouponInfoEntity> nftCouponInfoList) {
        log.info("============================ NFT 쿠폰 지급 시작 ================================");
        if (PredicateUtils.isNotNullList(giveAwayList) || PredicateUtils.isNotNullList(nftCouponInfoList)) {

            List<ArEventNftCouponRepositoryEntity> saveNftCouponRepositoryList = new ArrayList<>();
            List<ArEventNftCouponInfoEntity> updateNftCouponList = new ArrayList<>();

            if (giveAwayList.size() > nftCouponInfoList.size()) {
                log.error("당첨자 목록과 NFT 쿠폰 지급 개수가 일치하지 않습니다.");
            }

            if (giveAwayList.size() <= nftCouponInfoList.size()) {
                log.info("============================ 응모와 NFT 개수가 같아서 로직 시작  ================================");
                for (int i = 0; i < giveAwayList.size(); i++) {
                    ArEventNftCouponInfoEntity nftCouponInfo = nftCouponInfoList.get(i);
                    EventGiveAwayDeliveryEntity giveAwayDeliveryInfo = giveAwayList.get(i);

                    if (nftCouponInfo.getIsPayed()) {
                        log.error("transferNftToken Error :: 이미 지급 완료된 NFT 쿠폰 =====> TokenId >>>> " + nftCouponInfo.getNftCouponId());
                    } else {
                        log.info("============================ giveAwayId :: " + giveAwayDeliveryInfo.getGiveAwayId() + " ================================");
                        log.info("============================ nftCouponInfoId :: " + nftCouponInfo.getId() + " ================================");
                        log.info("============================ nftCouponId:: " + nftCouponInfo.getNftCouponId() + " ================================");
                        //NFT_COUPON_REPOSITORY 저장 배열 담기
                        saveNftCouponRepositoryList.add(
                                ArEventNftCouponRepositoryEntity.saveOf(giveAwayDeliveryInfo.getGiveAwayId(), nftCouponInfo.getId())
                        );
                        //NFT_COUPON_INFO 지급완료된 토큰 배열 담기
                        updateNftCouponList.add(
                                ArEventNftCouponInfoEntity.transferOf(nftCouponInfo)
                        );

                        try {
                            arEventMapper.updateNftCouponIsPayed(nftCouponInfo.getId(), true);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }

                if (PredicateUtils.isNotNullList(saveNftCouponRepositoryList)) {
                    try {
                        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
                        arEventNftCouponRepositoryEntityRepository.saveAll(saveNftCouponRepositoryList);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        log.info("============================ NFT 쿠폰 지급 끝 ================================");
    }

    /**
     * 쿠폰 보관함에 OCB 쿠폰 지급
     * @param giveAwayList
     */
    @Transactional
    public void transCouponRepositoryByOcbCoupon(List<EventGiveAwayDeliveryEntity> giveAwayList) {
        log.info("============================ OCB 쿠폰 지급 시작 ================================");
        if (PredicateUtils.isNullList(giveAwayList)) {
            log.info("============================ 지급할 당첨자 목록이 없음 ================================");
        } else {
            List<ArEventNftCouponRepositoryEntity> saveNftCouponRepositoryList = new ArrayList<>();

            for (EventGiveAwayDeliveryEntity deliveryEntity : giveAwayList) {
                //AR_EVENT_WINNING 정보 가져오기
                ArEventWinningEntity winningEntity = this.findByArEventWinningById(deliveryEntity.getArEventWinningId());
                if (PredicateUtils.isNotNull(winningEntity.getOcbCouponId())) {
                    saveNftCouponRepositoryList.add(
                            ArEventNftCouponRepositoryEntity.saveOfOcbCoupon(deliveryEntity.getGiveAwayId(), winningEntity.getOcbCouponId())
                    );

                } else {
                    log.info("=================================== 당첨정보의 OCB 쿠폰 정보가 없음 ===================================");
                }
            }

            if (PredicateUtils.isNotNullList(saveNftCouponRepositoryList)) {
                try {
                    arEventNftCouponRepositoryEntityRepository.saveAll(saveNftCouponRepositoryList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Transactional
    public void saveAllArEventNftTokenInfo(List<ArEventNftTokenInfoEntity> arEventNftTokenInfoEntityList) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventNftTokenInfoRepository.saveAll(arEventNftTokenInfoEntityList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveAllArEventNftCouponInfo(List<ArEventNftCouponInfoEntity> arEventNftCouponInfoEntityList) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventNftCouponInfoEntityRepository.saveAll(arEventNftCouponInfoEntityList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateSubscriptionRaffleScheduleDate(int arEventWinningId) {
        try {
            arEventMapper.updateSubscriptionRaffleScheduleDate(arEventWinningId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void updateArEventNftTokenInfo(int arEventId, int arEventWinningId, String fileName, boolean isStamp) {
        try {
            if (isStamp) {
                arEventMapper.updateArEventNftTokenInfo(0, arEventId, arEventWinningId, fileName);
            } else {
                arEventMapper.updateArEventNftTokenInfo(arEventId, 0, arEventWinningId, fileName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void updateArEventNftCouponInfo(int arEventId, int stpId, int arEventWinningId, String fileName) {
        try {
            arEventMapper.updateArEventNftCouponInfo(arEventId, stpId, arEventWinningId, fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void saveAllArEventWinningText(int arEventWinningId, List<EventWinningTextReqDto> reqDtoList) {
        List<ArEventWinningTextEntity> entityList = ModelMapperUtils.convertModelInList(reqDtoList, ArEventWinningTextEntity.class);
        if (PredicateUtils.isNotNullList(entityList)) {
            for (ArEventWinningTextEntity entity : entityList) {
                entity.setArEventWinningId(arEventWinningId);
            }
            try {
                arEventWinningTextEntityRepository.saveAll(entityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveArEventWinningText(ArEventWinningTextEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                arEventWinningTextEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void saveAllArEventRepositoryButton(int arEventWinningId, List<EventRepositoryButtonReqDto> reqDtoList) {
        List<ArEventRepositoryButtonEntity> entityList = ModelMapperUtils.convertModelInList(reqDtoList, ArEventRepositoryButtonEntity.class);
        if (!PredicateUtils.isNullList(entityList)) {
            for (ArEventRepositoryButtonEntity entity : entityList) {
                entity.setArEventWinningId(arEventWinningId);
            }
            try {
                arEventRepositoryButtonEntityRepository.saveAll(entityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveArEventRepositoryButton(ArEventRepositoryButtonEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                arEventRepositoryButtonEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveSequences(String name, long val) {
        try {
            arEventMapper.saveSequences(name.trim(), val);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateArEventWinningText(ArEventWinningTextEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                arEventMapper.updateArEventWinningText(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateArEventRepositoryButton(ArEventRepositoryButtonEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                arEventMapper.updateArEventRepositoryButton(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateArEventWinningTextFromSodar(List<ArEventWinningTextEntity>updateEntityList, List<ArEventWinningTextEntity>savedEntityList, int arEventWinningId) {
        if (PredicateUtils.isNotNullList(updateEntityList)) {
            for (ArEventWinningTextEntity updateEntity : updateEntityList) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(updateEntity.getArEventWinningTextId())) {
                    updateEntity.setArEventWinningId(arEventWinningId);
                    this.saveArEventWinningText(updateEntity);
                } else {
                    //저장되어 있는 데이터가 있을때
                    if (PredicateUtils.isNotNullList(savedEntityList)) {
                        //저장되어있는 데이터가 있는지 DB조회
                        ArEventWinningTextEntity findEntity = this.findArEventWinningTextById(updateEntity.getArEventWinningTextId());
                        if (PredicateUtils.isNotNull(findEntity)) {
                            //DB 조회 항목이 있으면 저장되어있는 배열 데이터에서 row 데이터 삭제처리
                            savedEntityList.removeIf(entity -> Objects.equals(entity.getArEventWinningTextId(), findEntity.getArEventWinningTextId()));
                        }
                    }
                    //인덱스가 있는 데이터는 수정
                    this.updateArEventWinningText(updateEntity);
                }
            }
            //삭제되어야 할 배열 데이터가 있으면 삭제 로직 처리
            if (PredicateUtils.isNotNullList(savedEntityList)) {
                savedEntityList.forEach(entity -> {
                    this.deleteArEventWinningTextById(entity.getArEventWinningTextId());
                });
            }
        }
    }

    @Transactional
    public void updateArEventRepositoryButtonFromSodar(List<ArEventRepositoryButtonEntity>updateEntityList, List<ArEventRepositoryButtonEntity>savedEntityList, int arEventWinningId) {
        if (!PredicateUtils.isNullList(updateEntityList)) {
            for (ArEventRepositoryButtonEntity updateEntity : updateEntityList) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(updateEntity.getArEventRepositoryButtonId())) {
                    updateEntity.setArEventWinningId(arEventWinningId);
                    this.saveArEventRepositoryButton(updateEntity);
                } else {
                    //저장되어 있는 데이터가 있을때
                    if (!PredicateUtils.isNullList(savedEntityList)) {
                        //저장되어있는 데이터가 있는지 DB조회
                        ArEventRepositoryButtonEntity findEntity = this.findArEventRepositoryButtonById(updateEntity.getArEventRepositoryButtonId());
                        if (PredicateUtils.isNotNull(findEntity)) {
                            //DB 조회 항목이 있으면 저장되어있는 배열 데이터에서 row 데이터 삭제처리
                            savedEntityList.removeIf(entity -> Objects.equals(entity.getArEventRepositoryButtonId(), findEntity.getArEventRepositoryButtonId()));
                        }
                    }
                    //인덱스가 있는 데이터는 수정
                    this.updateArEventRepositoryButton(updateEntity);
                }
            }
            //삭제되어야 할 배열 데이터가 있으면 삭제 로직 처리
            if (!PredicateUtils.isNullList(savedEntityList)) {
                savedEntityList.forEach(entity -> {
                    this.deleteArEventRepositoryButtonById(entity.getArEventRepositoryButtonId());
                });
            }
        }
    }

    @InjectCreatedModifyName
    @LoggingTimeFilter
    //AR_EVENT_DEVICE_GPS 테이블 > 신규저장, 업데이트, 삭제 서비스
    @Transactional
    public void saveAllArEventDeviceGps(int eventHtmlId, List<ArEventDeviceGpsEntity> entityList) {
        if (eventHtmlId > 0 && !PredicateUtils.isNullList(entityList)) {

            List<ArEventDeviceGpsEntity> saveList = new ArrayList<>();
            //신규 저장과 수정건 따로 가공해서 리스트에 담는 loop
            for (ArEventDeviceGpsEntity entity : entityList) {
                //신규저장
                if (PredicateUtils.isNull(entity.getId())) {
                    entity.setEventHtmlId(eventHtmlId);
                    saveList.add(entity);
                }
                //수정
                if (PredicateUtils.isNotNull(entity.getId())) {
                    //저장되어있는 row 조회
                    ArEventDeviceGpsEntity findEntity = arEventDeviceGpsEntityRepository.findById(entity.getId()).orElseGet(ArEventDeviceGpsEntity::new);
                    if (PredicateUtils.isNotNull(findEntity)) {
                        //저장되어있는 row 값이 있으면 수정값 구성
                        saveList.add(ArEventDeviceGpsEntity.ofUpdate(eventHtmlId, findEntity, entity));
                    }
                }
            }

            //저장되어 있는 전체 리스트 가져오기
            Optional<List<ArEventDeviceGpsEntity>> optional = arEventDeviceGpsEntityRepository.findAllByEventHtmlId(eventHtmlId);
            if (optional.isPresent()) {
                //저장되어 있는 전체 리스트
                List<ArEventDeviceGpsEntity> findAllList = optional.get();
                //새로 저장또는 수정할 데이터만큼 loop
                for (ArEventDeviceGpsEntity saveEntity : saveList) {
                    //저장되어 있는 전체 데이터에서 새로 저장또는 수정할 데이터의 ID 값이 같으면 row 삭제
                    findAllList.removeIf(entity -> Objects.equals(entity.getId(), saveEntity.getId()));
                }
                //전체데이터 리스트가 남아있으면 삭제해야 할 데이터이기때문에 삭제한다
                if (!PredicateUtils.isNullList(findAllList)) {
                    //삭제할 아이디 리스트 담기
                    List<Integer> deleteList = findAllList.stream()
                                                                .map(ArEventDeviceGpsEntity::getId)
                                                                .collect(Collectors.toList());
                    try {
                        //데이터 삭제
                        arEventDeviceGpsEntityRepository.deleteAllById(deleteList);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            try {
                //신규저장 또는 수정하기
                arEventDeviceGpsEntityRepository.saveAll(saveList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveAllArPhotoContents(int arEventId, String contentsType, List<ArPhotoContentsEntity>entityList) {
        if (arEventId > 0 && !PredicateUtils.isNullList(entityList)) {
            List<ArPhotoContentsEntity> saveList = new ArrayList<>();

            for (ArPhotoContentsEntity entity : entityList) {
                //신규저장
                if (PredicateUtils.isNull(entity.getId())) {
                    entity.setArEventId(arEventId);
                    entity.setPhotoContentType(contentsType);
                    saveList.add(entity);
                }
                //수정
                if (PredicateUtils.isNotNull(entity.getId())) {
                    //저장되어있는 row 조회
                    ArPhotoContentsEntity findEntity = arPhotoContentsEntityRepository.findById(entity.getId()).orElseGet(ArPhotoContentsEntity::new);
                    if (PredicateUtils.isNotNull(findEntity)) {
                        //저장되어있는 row 값이 있으면 수정값 구성
                        saveList.add(ArPhotoContentsEntity.ofUpdate(findEntity, entity, contentsType));
                    }
                }
            }
            //저장되어 있는 전체 리스트 가져오기
            Optional<List<ArPhotoContentsEntity>> optional = arPhotoContentsEntityRepository.findAllByArEventIdAndPhotoContentType(arEventId, contentsType);
            if (optional.isPresent()) {
                //저장되어 있는 전체 리스트
                List<ArPhotoContentsEntity> findAllList = optional.orElseGet(ArrayList::new);
                //새로 저장또는 수정할 데이터만큼 loop
                for (ArPhotoContentsEntity saveEntity : saveList) {
                    //저장되어 있는 전체 데이터에서 새로 저장또는 수정할 데이터의 ID 값이 같으면 row 삭제
                    findAllList.removeIf(entity -> Objects.equals(entity.getId(), saveEntity.getId()));
                }
                //전체데이터 리스트가 남아있으면 삭제해야 할 데이터이기때문에 삭제한다
                if (!PredicateUtils.isNullList(findAllList)) {
                    //삭제할 아이디 리스트 담기
                    List<Long> deleteList = findAllList.stream()
                                                                .map(ArPhotoContentsEntity::getId)
                                                                .collect(Collectors.toList());
                    try {
                        //데이터 삭제
                        arPhotoContentsEntityRepository.deleteAllById(deleteList);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            try {
                //신규저장 또는 수정하기
                arPhotoContentsEntityRepository.saveAll(saveList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveOcbPointSave(int arEventId, OcbPointSaveEntity entity) {
        //신규저장
        if (PredicateUtils.isNull(entity.getId()) || PredicateUtils.isEqualZero(entity.getId())) {
            entity.setArEventId(arEventId);
        }
        //수정
        if (PredicateUtils.isNotNull(entity.getId()) && PredicateUtils.isGreaterThanZero(entity.getId())) {
            OcbPointSaveEntity findEntity = ocbPointSaveEntityRepository.findById(entity.getId()).orElseGet(OcbPointSaveEntity::new);
            entity = OcbPointSaveEntity.ofUpdate(findEntity, entity);
        }
        try {
            ocbPointSaveEntityRepository.save(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveArPhotoLogical(int arEventId, ArPhotoLogicalEntity entity) {
        //신규저장
        if (PredicateUtils.isNull(entity.getId())) {
            entity.setArEventId(arEventId);
        }
        //수정
        if (PredicateUtils.isNotNull(entity.getId())) {
            ArPhotoLogicalEntity findEntity = arPhotoLogicalEntityRepository.findById(entity.getId()).orElseGet(ArPhotoLogicalEntity::new);
            entity = ArPhotoLogicalEntity.ofUpdate(findEntity, entity);
        }
        try {
            arPhotoLogicalEntityRepository.save(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @InjectCreatedModifyName
    @LoggingTimeFilter
    @Transactional
    public void saveAllArEventWinningButtonAdd(int arEventWinningButtonId, List<ArEventWinningButtonAddEntity>entityList) {
        if (arEventWinningButtonId > 0 && !PredicateUtils.isNullList(entityList)) {
            List<ArEventWinningButtonAddEntity> saveList = new ArrayList<>();

            for (ArEventWinningButtonAddEntity entity : entityList) {
                //신규저장
                if (PredicateUtils.isNull(entity.getId())) {
                    entity.setArEventWinningButtonId(arEventWinningButtonId);
                    saveList.add(entity);
                }
                //수정
                if (PredicateUtils.isNotNull(entity.getId())) {
                    //저장되어있는 row 조회
                    ArEventWinningButtonAddEntity findEntity = arEventWinningButtonAddEntityRepository.findById(entity.getId()).orElseGet(ArEventWinningButtonAddEntity::new);
                    if (PredicateUtils.isNotNull(findEntity)) {
                        //저장되어있는 row 값이 있으면 수정값 구성
                        saveList.add(ArEventWinningButtonAddEntity.ofUpdate(findEntity, entity));
                    }
                }
            }//end for

            //저장되어 있는 전체 리스트 가져오기
            Optional<List<ArEventWinningButtonAddEntity>> optional = arEventWinningButtonAddEntityRepository.findAllByArEventWinningButtonId(arEventWinningButtonId);
            if (optional.isPresent()) {
                //저장되어 있는 전체 리스트
                List<ArEventWinningButtonAddEntity> findAllList = optional.orElseGet(ArrayList::new);
                //새로 저장또는 수정할 데이터만큼 loop
                for (ArEventWinningButtonAddEntity saveEntity : saveList) {
                    //저장되어 있는 전체 데이터에서 새로 저장또는 수정할 데이터의 ID 값이 같으면 row 삭제
                    findAllList.removeIf(entity -> Objects.equals(entity.getId(), saveEntity.getId()));
                }
                //전체데이터 리스트가 남아있으면 삭제해야 할 데이터이기때문에 삭제한다
                if (!PredicateUtils.isNullList(findAllList)) {
                    //삭제할 아이디 리스트 담기
                    List<Long> deleteList = findAllList.stream()
                            .map(ArEventWinningButtonAddEntity::getId)
                            .collect(Collectors.toList());
                    try {
                        arEventWinningButtonAddEntityRepository.deleteAllById(deleteList);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            try {
                arEventWinningButtonAddEntityRepository.saveAll(saveList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateCommonSettings(String settingKey, String value) {
        if (PredicateUtils.isNotNull(settingKey)) {
            CommonSettingsEntity updateEntity = CommonSettingsEntity.ofUpdate(this.findCommonSettingsBySettingKey(settingKey), value);
            try {
                commonSettingsEntityRepository.save(updateEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveCommonSettings(String settingKey, String value) {
        if (PredicateUtils.isNotNull(settingKey)) {
            CommonSettingsEntity entity = new CommonSettingsEntity();
            entity.setSettingKey(settingKey);
            entity.setValue(value);
            try {
                commonSettingsEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveSodarEventUpdateHistory(String eventId, String jsonStr) {
        try {
            arEventMapper.insertSodarEventUpdateHistory(eventId, jsonStr, SodarMemberSession.get().getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveArEventNftCouponFromTempTableBySeq(int arEventId, int stpId, long arEventWinningId, String uploadFileName, long seqName) {
        try {
            arEventMapper.insertArEventNftCouponByTemp(arEventId, stpId, arEventWinningId, uploadFileName, seqName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void saveArEventNftTokenFromTempTableBySeq(int arEventId, int stpId, long arEventWinningId, String uploadFileName, long seqNum) {
        try {
            arEventMapper.insertArEventNftTokenByTemp(arEventId, stpId, arEventWinningId, uploadFileName, seqNum);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**************************************************** SAVE,UPDATE END ****************************************************/



    /**************************************************** SELECT START ****************************************************/

    /**
     * WEB_EVENT_BASE 가져오기
     * @param eventId
     * @return
     */
    @Cacheable(cacheNames = "findEventBase", keyGenerator = "customKeyGenerator")
    public WebEventBaseEntity findEventBase(String eventId) {
        return eventBaseEntityRepository.findByEventId(eventId).orElseGet(WebEventBaseEntity::new);
    }

    public WebEventBaseEntity findEventBaseNoCache(String eventId) {
        return eventBaseEntityRepository.findByEventId(eventId).orElseGet(WebEventBaseEntity::new);
    }

    /**
     * AR_EVENT_WINNING 정보 가져오기
     * @param arEventId
     * @return
     */
    public ArEventWinningEntity findEventWinningEntityByArEventId(int arEventId) {
        return arEventWinningEntityRepository.findFirstByArEventIdOrderByArEventWinningIdDesc(arEventId);
    }

    public ArEventWinningEntity findEventWinningEntityByStpId(int stpId) {
        return arEventWinningEntityRepository.findFirstByStpIdOrderByArEventWinningIdDesc(stpId);
    }

    @Cacheable(cacheNames = "findArEventByEventIdAtWinningProcess", keyGenerator = "customKeyGenerator")
    public ArEventByIdAtWinningProcessMapperVO findArEventByEventIdAtWinningProcess(String eventId) {
        return arEventMapper.selectArEventByIdAtWinningProcess(eventId);
    }

    @Cacheable(cacheNames = "findArEventByEventIdAtObjectExposure", keyGenerator = "customKeyGenerator")
    public ArEventByEventIdAtObjectExposureVO findArEventByEventIdAtObjectExposure(String eventId) {
        return arEventMapper.selectArEventByEventIdAtObjectExposure(eventId);
    }

    public ArEventEntity findArEventByEventId(String eventId) {
        return arEventEntityRepository.findByEventId(eventId);
    }

    @Cacheable(cacheNames = "findArEventByEventIdAtCache", keyGenerator = "customKeyGenerator")
    public ArEventEntity findArEventByEventIdAtCache(String eventId) {
        return arEventEntityRepository.findByEventId(eventId);
    }

    public ArEventJoinEventBaseVO findArEventJoinEventBaseByEventId(String eventId) {
        return arEventMapper.selectArEventJoinEventBaseByEventId(eventId);
    }

    public ArEventJoinEventBaseVO findArEventJoinEventBaseByArEventId(int arEventId) {
        return arEventMapper.selectArEventJoinEventBaseByArEventId(arEventId);
    }

    /**
     * AR_EVENT 정보 가져오기
     * @param arEventId
     * @return
     */
    @Cacheable(cacheNames = "findArEventById", keyGenerator = "customKeyGenerator")
    public ArEventEntity findArEventById(int arEventId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventEntityRepository.findById(arEventId).orElseGet(ArEventEntity::new);
    }

    /**
     * AR_EVENT 정보 가져오기, ArEventResDto 객체로 변경
     * @param eventId
     * @return ArEventResDto
     */
    public ArEventResDto findArEventByEventIdOfResDto(String eventId) {
        ArEventEntity arEventEntity = arEventEntityRepository.findByEventId(eventId);
        if (PredicateUtils.isNotNull(arEventEntity.getArEventId())) {
            return ModelMapperUtils.convertModel(arEventEntity, ArEventResDto.class);
        }
        return null;
    }

    /**
     * AR_EVENT_ATTEND_TIME 리스트 정보 가져오기
     * @param arEventId
     * @return
     */
    public List<ArEventAttendTimeEntity> findAllArEventAttendTimeByArEventId(int arEventId) {
        return arEventAttendTimeEntityRepository.findByArEventIdOrderByArEventAttendTimeIdAsc(arEventId);
    }

    @Cacheable(cacheNames = "findAllArEventAttendTimeByArEventIdProjection", keyGenerator = "customKeyGenerator")
    public List<ArEventAttendTimeEntity> findAllArEventAttendTimeByArEventIdProjection(int arEventId) {
        return arEventAttendTimeEntityRepository.findAllByArEventIdOrderByArEventAttendTimeIdAsc(arEventId);
    }

    /**
     * AR_EVENT_BUTTON 가져오기
     * @param arEventId
     * @return
     */
    public ArEventButtonEntity findArEventButtonByArEventId(int arEventId) {
        return arEventButtonEntityRepository.findByArEventId(arEventId);
    }

    public ArEventButtonEntity findArEventButtonByStpId(int stpId) {
        return arEventButtonEntityRepository.findByStpId(stpId);
    }

    /**
     * AR_EVENT_WINNING_BUTTON 가져오기
     * @param arEventWinningButtonId
     * @return
     */
    @Cacheable(cacheNames = "findArEventWinningButtonById", keyGenerator = "customKeyGenerator")
    public ArEventWinningButtonEntity findArEventWinningButtonById(int arEventWinningButtonId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventWinningButtonEntityRepository.findById(arEventWinningButtonId).orElseGet(ArEventWinningButtonEntity::new);
    }

    public ArEventWinningButtonEntity findArEventWinningButtonByIdNoCache(int arEventWinningButtonId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventWinningButtonEntityRepository.findById(arEventWinningButtonId).orElseGet(ArEventWinningButtonEntity::new);
    }

    /**
     * AR_EVENT_LOGICAL 가져오기
     * @param arEventId
     * @return
     */
    public ArEventLogicalEntity findArEventLogicalByArEventId(int arEventId) {
        return arEventLogicalEntityRepository.findByArEventId(arEventId);
    }

    @Cacheable(cacheNames = "findArEventLogicalResDtoAtExposureObject", keyGenerator = "customKeyGenerator")
    public ArEventLogicalResDto findArEventLogicalResDtoAtExposureObject(int arEventId) {
        ArEventLogicalEntity arEventLogicalEntity = arEventMapper.selectArEventLogicalAtExposureObject(arEventId);
        if (PredicateUtils.isNotNull(arEventLogicalEntity)) {
            return ModelMapperUtils.convertModel(arEventLogicalEntity, ArEventLogicalResDto.class);
        }
        return new ArEventLogicalResDto();
    }

    public List<ArEventWinningEntity> findArEventWinningListByArEventId(int arEventId) {
        return arEventWinningEntityRepository.findAllByArEventId(arEventId);
    }

    @Cacheable(cacheNames = "findArEventWinningListByStpId", keyGenerator = "customKeyGenerator")
    public List<ArEventWinningEntity> findArEventWinningListByStpId(int stpId) {
        return arEventWinningEntityRepository.findAllByStpId(stpId);
    }

    public List<ArEventWinningEntity> findArEventWinningListNoCacheByStpId(int stpId) {
        return arEventWinningEntityRepository.findAllByStpIdOrderByEventWinningSortAsc(stpId);
    }

    /**
     * AR_EVENT_WINNING 리스트 가져오기
     * @param arEventId
     * @param eventWinningTypeIn
     * @return
     */
    public LinkedList<ArEventWinningEntity> findArEventWinningListByArEventIdAndEventWinningTypeIn(int arEventId, List<String>eventWinningTypeIn, boolean isSubscription) {
        List<ArEventWinningEntity> arEventWinningEntityList = new ArrayList<>();
        //응모형일때
        if (isSubscription) {
            arEventWinningEntityList = arEventWinningEntityRepository.findAllByArEventIdAndSubscriptionYnAndWinningTypeInOrderByEventWinningSortAsc(arEventId, StringDefine.Y.name(), eventWinningTypeIn);
        }
        //응모형이 아닐때
        if (!isSubscription) {
            arEventWinningEntityList = arEventWinningEntityRepository.findAllByArEventIdAndWinningTypeInOrderByEventWinningSortAsc(arEventId, eventWinningTypeIn);
        }

        if (!arEventWinningEntityList.isEmpty()) {
            return EventUtils.convertALtoLL(arEventWinningEntityList);
        }
        return null;
    }

    @Cacheable(cacheNames = "findArEventWinningListByArEventIdAndSubscriptionYn", keyGenerator = "customKeyGenerator")
    public LinkedList<ArEventWinningEntity> findArEventWinningListByArEventIdAndSubscriptionYn(int arEventId, boolean isSubscription, boolean isFail) {
        List<ArEventWinningEntity> arEventWinningEntityList = new ArrayList<>();
        //응모형일때
        if (isSubscription) {
            arEventWinningEntityList = arEventMapper.selectArEventWinningListByArEventIdAndNotFailAtWinningProcess(arEventId, StringDefine.Y.name(), isFail);
        }
        //응모형이 아닐때
        if (!isSubscription) {
            arEventWinningEntityList = arEventMapper.selectArEventWinningListByArEventIdAndNotFailAtWinningProcess(arEventId, null, isFail);
        }

        if (!arEventWinningEntityList.isEmpty()) {
            return EventUtils.convertALtoLL(arEventWinningEntityList);
        }
        return null;
    }

    @Cacheable(cacheNames = "findArEventWinningListByStpId", keyGenerator = "customKeyGenerator")
    public LinkedList<ArEventWinningEntity> findArEventWinningListByStpId(int stpId, boolean isFail) {
        List<ArEventWinningEntity> arEventWinningEntityList = new ArrayList<>();
        arEventWinningEntityList = arEventMapper.selectArEventWinningListByStpId(stpId, isFail);
        return EventUtils.convertALtoLL(arEventWinningEntityList);
    }


    @Cacheable(cacheNames = "findFailArEventWinningListByArEventIdAndSubscriptionYn", keyGenerator = "customKeyGenerator")
    public LinkedList<ArEventWinningEntity>     findFailArEventWinningListByArEventIdAndSubscriptionYn(int arEventId, boolean isSubscription) {
        List<ArEventWinningEntity> arEventWinningEntityList = new ArrayList<>();
        //응모형일때
        if (isSubscription) {
            arEventWinningEntityList = arEventMapper.selectArEventWinningListByArEventIdAndNotFailAtWinningProcess(arEventId, StringDefine.Y.name(), true);
        }
        //응모형이 아닐때
        if (!isSubscription) {
            arEventWinningEntityList = arEventMapper.selectArEventWinningListByArEventIdAndNotFailAtWinningProcess(arEventId, null, true);
        }

        if (!arEventWinningEntityList.isEmpty()) {
            return EventUtils.convertALtoLL(arEventWinningEntityList);
        }
        return null;
    }

    /**
     * AR_EVENT_WINNING 리스트 가져오기 :: ArEventWinningResDto 으로 변환
     * @param arEventId
     * @return
     */
    public List<ArEventWinningResDto> findAllArEventWinningByArEventIdOfResDto(int arEventId) {
        List<ArEventWinningEntity> entityList = arEventWinningEntityRepository.findAllByArEventIdOrderByArEventWinningIdAsc(arEventId);
        if (!PredicateUtils.isNullList(entityList)) {
            return ModelMapperUtils.convertModelInList(entityList, ArEventWinningResDto.class);
        }
        return new ArrayList<>();
    }

    /**
     * AR_EVENT_WINNING 가져오기(인덱스 기준)
     * @param arEventWinningId
     * @return
     */
    @Cacheable(cacheNames = "findByArEventWinningById" , keyGenerator = "customKeyGenerator")
    public ArEventWinningEntity findByArEventWinningById(int arEventWinningId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventWinningEntityRepository.findById(arEventWinningId).orElseGet(ArEventWinningEntity::new);
    }

    public ArEventWinningEntity findByArEventWinningByIdNoCache(int arEventWinningId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventWinningEntityRepository.findById(arEventWinningId).orElseGet(ArEventWinningEntity::new);
    }

    /**
     * AR_EVENT_WINNING_BUTTON 리스트 가져오기
     * @param arEventWinningId
     * @return
     */
    public List<ArEventWinningButtonEntity> findAllArEventWinningButtonByArEventWinningId(int arEventWinningId) {
        return arEventWinningButtonEntityRepository.findAllByArEventWinningIdOrderByButtonSortAsc(arEventWinningId);
    }

    @Cacheable(cacheNames = "findArEventWinningButtonListByArEventWinningIdAtWinningProcess", keyGenerator = "customKeyGenerator")
    public List<ArEventWinningButtonEntity> findArEventWinningButtonListByArEventWinningIdAtWinningProcess(int arEventWinningId) {
        return arEventMapper.selectArEventWinningButtonListByArEventWinningIdAtWinningProcess(arEventWinningId);
    }

    @Cacheable(cacheNames = "findWinningButtonResDtoByArEventWinningIdAtWinningProcess", keyGenerator = "customKeyGenerator")
    public List<WinningButtonResDto> findWinningButtonResDtoByArEventWinningIdAtWinningProcess(int arEventWinningId, String eventId, String stpConnectYn) {
        List<WinningButtonResDto> buttinList = ModelMapperUtils.convertModelInList(this.findArEventWinningButtonListByArEventWinningIdAtWinningProcess(arEventWinningId), WinningButtonResDto.class);
        if (PredicateUtils.isNotNullList(buttinList)) {
            if (PredicateUtils.isNull(stpConnectYn)) {
                stpConnectYn = StringDefine.N.name();
            }
            //스탬프 이벤트일때만
            if (PredicateUtils.isEqualsStr(stpConnectYn, StringDefine.Y.name())) {
                eventWinning.injectStampPanAndStampNextEventUrlPath(eventId, buttinList);
            }
        }
        return buttinList;
    }

    /**
     * AR_EVENT_OBJECT 가져오기
     * @param arEventId
     * @return
     */
    public List<ArEventObjectEntity> findAllArEventObjectByArEventId(int arEventId) {
        Optional<List<ArEventObjectEntity>>optional = arEventObjectEntityRepository.findByArEventIdOrderByArEventObjectIdAsc(arEventId);
        if (optional.isPresent()) {
            for (ArEventObjectEntity arEventObject : optional.get()) {
                if (PredicateUtils.isNull(arEventObject.getBridgeExposureTimeType())) {
                    arEventObject.setBridgeExposureTimeType(StringDefine.N.name());
                    arEventObject.setBridgeExposureTimeSecond(0);
                }
            }
            return optional.get();
        }
        return null;
    }

    @Cacheable(cacheNames = "findAllArEventObjectByArEventIdAtObjectExposure", keyGenerator = "customKeyGenerator")
    public List<ArEventObjectEntity> findAllArEventObjectByArEventIdAtObjectExposure(int arEventId) {
        return arEventMapper.selectArEventObjectByArEventIdAtObjectExposure(arEventId);
    }

    /**
     * AR_EVENT_OBJECT 가져오기(인덱스 기준)
     * @param arEventObjectId
     * @return
     */
    public ArEventObjectEntity findArEventObjectById(int arEventObjectId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventObjectEntityRepository.getById(arEventObjectId);
    }

    public ArEventObjectEntity findArEventObjectByIdAtWinningProcess(int arEventObjectId, int arEventId) {
        return arEventMapper.selectArEventObjectByIdAtWinningProcess(arEventObjectId);
    }

    /**
     * AR_EVENT_SCANNING_IMAGE 가져오기
     * @param arEventId
     * @return
     */
    public List<ArEventScanningImageEntity> findAllArEventScanningImageByEventId(int arEventId) {
        return arEventScanningImageEntityRepository.findAllByArEventIdOrderByArEventScanningImageIdAsc(arEventId).orElseGet(ArrayList::new);
    }

    public List<ArEventScanningImageResDto> findAllArEventScanningImageResDtoAtExposureObject(int arEventId) {
        List<ArEventScanningImageEntity> arEventScanningImageResDtoList = arEventMapper.selectArEventScanningImageListByArEventIdAtExposureObject(arEventId);
        if (!PredicateUtils.isNullList(arEventScanningImageResDtoList)) {
            return ModelMapperUtils.convertModelInList(arEventScanningImageResDtoList,ArEventScanningImageResDto.class);
        }
        return new ArrayList<>();
    }

    /**
     * AR_EVENT_HTML 리스트 가져오기
     * @param arEventId
     * @return
     */
    @Cacheable(cacheNames = "findAllArEventHtmlByArEventId", keyGenerator = "customKeyGenerator")
    public List<ArEventHtmlEntity> findAllArEventHtmlByArEventId(int arEventId) {
        return arEventHtmlEntityRepository.findAllByArEventIdOrderByHtmlTypeSortAsc(arEventId);
    }

    public List<ArEventHtmlResDto> findAllArEventHtmlResDtoByArEventId(int arEventId) {
        List<ArEventHtmlEntity> arEventHtmlEntityList = arEventHtmlEntityRepository.findAllByArEventIdOrderByHtmlTypeSortAsc(arEventId);
        if (!PredicateUtils.isNullList(arEventHtmlEntityList)) {
            return ModelMapperUtils.convertModelInList(arEventHtmlEntityList, ArEventHtmlResDto.class);
        }
        return null;
    }

    public LinkedList<String> findArEventGateCodeListByEventId(String eventId) {
        return arEventMapper.selectArEventGateCodeList(eventId);
    }

    public long countArEventGateCodeListByEventId(String eventId) {
        return arEventMapper.countArEventGateCodeByEventId(eventId);
    }

    public List<EventGateCodeAtUsedMapperVO> findArEventGateCodeByEventIdAtUsed(String eventId) {
        if (StringTools.containsIgnoreCase(eventId, "S")) {
            return arEventMapper.selectStampEventGateCodeAtUsed(eventId);
        } else {
            return arEventMapper.selectEventGateCodeAtUsed(eventId);
        }
    }

    /**
     * AR_EVENT_GATE_CODE 가져오기
     * @param eventId
     * @param attendCode
     * @return
     */
    public ArEventGateCodeEntity findByEventIdAndAttendCode(String eventId, String attendCode) {
        Optional<ArEventGateCodeEntity> optional = arEventGateCodeEntityRepository.findByEventIdAndAttendCode(eventId, attendCode);
        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 카테고리 정보 리스트
     * @param categoryType
     * @param parentCode
     * @return
     */
    public ApiResultObjectDto findAllEventCategory(String categoryType, String parentCode) {
        int resultCode = HttpStatus.OK.value();

        Optional<List<ArEventCategoryEntity>> parentCategoryOptional = null;
        //조건에 따른 부모의 카테고리 리스트 가져오기
        if (StringUtils.isEmpty(categoryType) && StringUtils.isEmpty(parentCode)) {
            parentCategoryOptional = arEventCategoryEntityRepository.findAllByCategoryDepth(1);
        } else if (StringUtils.isEmpty(categoryType) && !StringUtils.isEmpty(parentCode)) {
            parentCategoryOptional = arEventCategoryEntityRepository.findAllByCategoryCodeAndCategoryDepth(parentCode.toUpperCase(), 1);
        } else if (!StringUtils.isEmpty(categoryType) && StringUtils.isEmpty(parentCode)) {
            parentCategoryOptional = arEventCategoryEntityRepository.findAllByCategoryTypeAndCategoryDepth(categoryType, 1);
        } else {
            parentCategoryOptional = arEventCategoryEntityRepository.findAllByCategoryTypeAndCategoryCodeAndCategoryDepth(categoryType, parentCode.toUpperCase(), 1);
        }

        //부모의 값이 있는지 확인
        if (parentCategoryOptional.isPresent()) {
            List<CategoryDto> categoryList = new ArrayList<>();
            List<ArEventCategoryEntity> parentCategoryList = parentCategoryOptional.orElseGet(ArrayList::new);
            //부모 카테고리 리스트 foreach 시작
            parentCategoryList.forEach(parent -> {

                CategoryDto categoryDto = new CategoryDto();
                categoryDto.setCategoryCode(parent.getCategoryCode());
                categoryDto.setCategoryName(parent.getCategoryName());
                categoryDto.setCategoryType(parent.getCategoryType());
                categoryDto.setCategoryDepth(parent.getCategoryDepth());

                //부모 카테고리 코드에 따른 자식 카테고리 코드 리스트 값 가져오기
                List<ArEventCategoryEntity> childCategoryList = arEventCategoryEntityRepository.findAllByParentCodeAndAndCategoryDepth(parent.getCategoryCode(), 2);
                List<Map<String, Object>> childCategoryMapList = new ArrayList<>();

                //자식의 카테고리 리스트 foreach 시작
                childCategoryList.forEach(child -> {
                    Map<String, Object> childCategoryMap = new HashMap<>();
                    childCategoryMap.put("categoryCode", child.getCategoryCode());
                    childCategoryMap.put("categoryName", child.getCategoryName());
                    childCategoryMap.put("categoryValue", child.getCategoryValue());
                    childCategoryMap.put("categoryDepth", child.getCategoryDepth());

                    childCategoryMapList.add(childCategoryMap);

                });
                //자식의 카테고리 리스트 foreach 끝
                categoryDto.setChildCategoryList(childCategoryMapList);

                categoryList.add(categoryDto);
            });
            //부모 카테고리 리스트 foreach 끝
            return new ApiResultObjectDto().builder()
                    .result(categoryList)
                    .resultCode(resultCode)
                    .build();
        }
        return new ApiResultObjectDto();
    }

    public int findWebEventSequence(String seqName) {
        return (int)arEventMapper.selectWebEventSequenceNextval(seqName);
    }

    public List<ArEventObjectEntity> findArEventObjectListByArEventId(int arEventId) {
        return arEventObjectEntityRepository.findByArEventId(arEventId);
    }

    public List<ArEventScanningImageEntity> findArEventScanningImageListByArEventId(int arEventId) {
        return arEventScanningImageEntityRepository.findByArEventId(arEventId);
    }

    public ArEventScanningImageEntity findArEventScanningImageById(int arEventScanningImageId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventScanningImageEntityRepository.getById(arEventScanningImageId);
    }

    public List<WebEventIpAccess> findWebEventIpAccessListByBuildLevelAndUrlPath(String buildLevel, String urlPath) {
        return webEventIpAccessRepository.findAllByBuildLevelAndUrlPath(buildLevel, urlPath).orElse(new ArrayList<>());
    }

    public List<XtransReceiverEntity> findAllXtransReceiver() {
        return xtransReceiverEntityRepository.findAll();
    }

    @Cacheable(cacheNames = "findAllArEventNftBenefitByArEventWinningId", keyGenerator = "customKeyGenerator")
    public List<ArEventNftBenefitEntity> findAllArEventNftBenefitByArEventWinningId(int arEventWinningId) {
        return arEventNftBenefitRepository.findAllByArEventWinningIdOrderByArEventNftBenefitIdAsc(arEventWinningId).orElse(new ArrayList<>());
    }

    public List<ArEventNftBenefitEntity> findAllArEventNftBenefitByArEventWinningIdNoCache(int arEventWinningId) {
        return arEventNftBenefitRepository.findAllByArEventWinningIdOrderByArEventNftBenefitIdAsc(arEventWinningId).orElse(new ArrayList<>());
    }

    @Cacheable(cacheNames = "findAllArEventBannerByArEventId", keyGenerator = "customKeyGenerator")
    public List<ArEventNftBannerEntity> findAllArEventBannerByArEventId(int arEventId) {
        return arEventNftBannerRepository.findAllByArEventIdOrderByIdAsc(arEventId).orElse(new ArrayList<>());
    }

    public List<ArEventNftBannerEntity> findAllArEventBannerByArEventIdNoCache(int arEventId) {
        return arEventNftBannerRepository.findAllByArEventIdOrderByIdAsc(arEventId).orElse(new ArrayList<>());
    }

    @Cacheable(cacheNames = "findAllArEventBannerByStpId", keyGenerator = "customKeyGenerator")
    public List<ArEventNftBannerEntity> findAllArEventBannerByStpId(int stpId) {
        return arEventNftBannerRepository.findAllByStpIdOrderByIdAsc(stpId).orElse(new ArrayList<>());
    }

    public ArEventNftBenefitEntity findArEventNftBenefitById(int arEventNftBenefitId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventNftBenefitRepository.getById(arEventNftBenefitId);
    }

    public ArEventNftBannerEntity findArEventNftBannerById(int arNftBannerId) {
        Optional<ArEventNftBannerEntity> optional = arEventNftBannerRepository.findById(arNftBannerId);
        return optional.orElseGet(ArEventNftBannerEntity::new);
    }


    public List<ArEventNftBannerEntity> findArEventNftBannerByEventHtmlId(int eventHtmlId) {
        return arEventNftBannerRepository.findAllByEventHtmlId(eventHtmlId).orElse(new ArrayList<>());
    }

    public List<ArEventNftTokenInfoEntity> findAllArEventNftTokenInfoLeftJoinArEventWinningByArEventWinningId(int arEventWinningId) {
        return arEventNftTokenInfoRepository.findAllByArEventWinningIdAndIsPayedOrderByIdAsc(arEventWinningId, false).orElse(new ArrayList<>());
    }

    public List<EventGiveAwayDeliveryEntity> findSubscriptionScheduleListByYYYY_MM_DD_HH(int arEventWinningId, String scheduleType) {
        return arEventMapper.selectSubscriptionList(DateUtils.getNow(DateUtils.PATTERN_YYYY_MMD_DD_HH), arEventWinningId, scheduleType);
    }

    public List<ArEventWinningEntity> findSubscriptionWinningInfoListByYYYY_MM_DD_HH(String scheduleType) {
        return arEventMapper.selectSubscriptionWinningInfo(DateUtils.getNow(DateUtils.PATTERN_YYYY_MMD_DD_HH), scheduleType);
    }

    public List<ArEventNftTokenInfoEntity> findAllArEventNftTokenByArEventWinningId(int arEventWinningId) {
        return arEventNftTokenInfoRepository.findAllByArEventWinningId(arEventWinningId).orElse(new ArrayList<>());
    }

    public int countArEventNftTokenByArEventWinningId(int arEventWinningId) {
        return (int) arEventNftTokenInfoRepository.countByArEventWinningId(arEventWinningId);
    }

    public ArEventNftTokenInfoEntity findDistinctNftTokenInfoByArEventWinningId(int arEventWinningId) {
        return arEventNftTokenInfoRepository.findDistinctFirstByArEventWinningId(arEventWinningId).orElseGet(ArEventNftTokenInfoEntity::new);
    }

    public List<ArEventNftCouponInfoEntity> findAllArEventNftCouponByArEventWinningId(int arEventWinningId) {
        Optional<List<ArEventNftCouponInfoEntity>> optional = arEventNftCouponInfoEntityRepository.findAllByArEventWinningId(arEventWinningId);
        return optional.orElseGet(ArrayList::new);
    }

    public int countArEventNftCouponByArEventWinningId(int arEventWinningId) {
        return (int) arEventNftCouponInfoEntityRepository.countByArEventWinningId(arEventWinningId);
    }

    public List<ArEventNftCouponInfoEntity> findAllArEventNftCouponInfoByArEventWinningId(int arEventWinningId) {
        return arEventNftCouponInfoEntityRepository.findAllByArEventWinningIdAndIsPayedOrderByIdAsc(arEventWinningId, false).orElse(new ArrayList<>());
    }

    public ArEventNftCouponInfoEntity findDistinctNftCouponInfoByArEventWinningId(int arEventWinningId) {
        return arEventNftCouponInfoEntityRepository.findDistinctFirstByArEventWinningId(arEventWinningId).orElseGet(ArEventNftCouponInfoEntity::new);
    }

    public List<Map<String, Object>> getArEventIdAtEventEndAfterSixtyDay() {
        return arEventMapper.selectEventIdAndArEventIdAtServiceEndAfterSixtyDay(Arrays.asList(ContractStatusDefine.서비스종료.code(), ContractStatusDefine.계약종료.code()), DateUtils.plusDay(DateUtils.getNowDay(), "YYYYMMDD", -59));
    }

    public List<Map<String, Object>> getStampEventAtEventEndAfterSixtyDay() {
        return arEventMapper.selectStampEventIdAndArEventIdAtServiceEndAfterSixtyDay(Arrays.asList(ContractStatusDefine.서비스종료.code(), ContractStatusDefine.계약종료.code()), DateUtils.plusDay(DateUtils.getNowDay(), "YYYYMMDD", -59));
    }

    public List<ArEventWinningTextEntity> findAllArEventWinningTextByArWinningId(int arEventWinningId) {
        return arEventWinningTextEntityRepository.findAllByArEventWinningId(arEventWinningId).orElseGet(ArrayList::new);
    }

    public ArEventWinningTextEntity findArEventWinningTextById(long arEventWinningTextId) {
        return arEventWinningTextEntityRepository.findById(arEventWinningTextId).orElseGet(ArEventWinningTextEntity::new);
    }

    public ArEventRepositoryButtonEntity findArEventRepositoryButtonById(long index) {
        return arEventRepositoryButtonEntityRepository.findById(index).orElseGet(ArEventRepositoryButtonEntity::new);
    }
    public List<ArEventRepositoryButtonEntity> findAllArEventRepositoryButtonByArWinningId(int arEventWinningId) {
        return arEventRepositoryButtonEntityRepository.findAllByArEventWinningId(arEventWinningId).orElseGet(ArrayList::new);
    }

    public ArPhotoLogicalEntity findArPhotoLogicalByArEventId(int arEventId) {
        return arPhotoLogicalEntityRepository.findByArEventId(arEventId).orElseGet(ArPhotoLogicalEntity::new);
    }

    public List<ArPhotoContentsEntity> findArPhotoContentsByArEventId(int arEventId) {
        return arPhotoContentsEntityRepository.findAllByArEventId(arEventId).orElseGet(ArrayList::new);
    }

    public OcbPointSaveEntity findOcbPointSaveByArEventIdAndArEventWinningId(Integer arEventId, Integer arEventWinningId) {
        if (PredicateUtils.isNotNull(arEventWinningId)) {
            return ocbPointSaveEntityRepository.findTopByArEventIdAndArEventWinningId(arEventId, arEventWinningId).orElseGet(OcbPointSaveEntity::new);
        } else {
            return ocbPointSaveEntityRepository.findTopByArEventId(arEventId).orElseGet(OcbPointSaveEntity::new);
        }
    }

    public List<ArEventDeviceGpsEntity> findAllArEventDeviceGpsByEventHtmlId(int eventHtmlId) {
        return arEventDeviceGpsEntityRepository.findAllByEventHtmlId(eventHtmlId).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findAllArEventWinningButtonAddByArEventWinningButtonId", keyGenerator = "customKeyGenerator")
    public List<ArEventWinningButtonAddEntity> findAllArEventWinningButtonAddByArEventWinningButtonId(int arEventWinningButtonId) {
        return arEventWinningButtonAddEntityRepository.findAllByArEventWinningButtonId(arEventWinningButtonId).orElseGet(ArrayList::new);
    }

    public CommonSettingsEntity findCommonSettingsBySettingKey(String settingKey) {
        return commonSettingsEntityRepository.findBySettingKey(settingKey).orElseGet(CommonSettingsEntity::new);
    }

    public List<ArEventHtmlEntity> findArEventHtmlListByArEventId(int arEventId) {
        return arEventHtmlEntityRepository.findByArEventId(arEventId).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findArEventHtmlListByStpId", keyGenerator = "customKeyGenerator")
    public List<ArEventHtmlEntity> findArEventHtmlListByStpId(int stpId) {
        return arEventHtmlEntityRepository.findByStpIdAndStpPanIdIsNull(stpId).orElseGet(ArrayList::new);
    }

    public List<ArEventHtmlEntity> findArEventHtmlListByStpIdNoCache(int stpId) {
        return arEventHtmlEntityRepository.findByStpIdAndStpPanIdIsNull(stpId).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findByStpIdAndStpPanIdIsNullOrderByHtmlTypeSort", keyGenerator = "customKeyGenerator")
    public List<ArEventHtmlEntity> findByStpIdAndStpPanIdIsNullOrderByHtmlTypeSort(int stpId) {
        return arEventHtmlEntityRepository.findByStpIdAndStpPanIdIsNullOrderByHtmlTypeSort(stpId).orElseGet(ArrayList::new);
    }

    public List<ArEventHtmlEntity> findArEventHtmlListNoCacheByStpId(int stpId) {
        return arEventHtmlEntityRepository.findByStpIdAndStpPanIdIsNull(stpId).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findArEventHtmlListByStpPanId", keyGenerator = "customKeyGenerator")
    public List<ArEventHtmlEntity> findArEventHtmlListByStpPanId(int stpPanId) {
        return arEventHtmlEntityRepository.findByStpPanId(stpPanId).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findArEventHtmlListByStpPanIdOrderByHtmlTypeSort", keyGenerator = "customKeyGenerator")
    public List<ArEventHtmlEntity> findArEventHtmlListByStpPanIdOrderByHtmlTypeSort(int stpPanId) {
        return arEventHtmlEntityRepository.findByStpPanIdOrderByHtmlTypeSort(stpPanId).orElseGet(ArrayList::new);
    }

    public List<ArEventHtmlEntity> findArEventHtmlListNoCacheByStpPanId(int stpPanId) {
        return arEventHtmlEntityRepository.findByStpPanId(stpPanId).orElseGet(ArrayList::new);
    }

    public List<ArEventHtmlEntity> findArEventHtmlListByStpIdAndStpPanId(int stpId, int stpPanId) {
        return arEventHtmlEntityRepository.findByStpIdAndStpPanId(stpId, stpPanId).orElseGet(ArrayList::new);
    }

    /**************************************************** SELECT END ****************************************************/


    /**************************************************** DELETE START ****************************************************/

    @Transactional
    public void deleteArEventAttendTimeByArEventId(int arEventId) {
        try {
            arEventAttendTimeEntityRepository.deleteByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventScanningImageById(int arEventScanningImageId) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventScanningImageEntityRepository.deleteById(arEventScanningImageId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventObjectById(int arEventObjectId) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventObjectEntityRepository.deleteById(arEventObjectId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventWinningById(int arEventWinningId) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventWinningEntityRepository.deleteById(arEventWinningId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventWinningButtonByArEventWinningId(int arEventWinningId) {
        try {
            arEventWinningButtonEntityRepository.deleteByArEventWinningId(arEventWinningId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventWinningButtonById(int arEventWinningButtonId) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventWinningButtonEntityRepository.deleteById(arEventWinningButtonId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteAllArEventHtml(List<Integer> eventHtmlIdList) {
        try {
            arEventHtmlEntityRepository.deleteAllById(eventHtmlIdList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventGateCodeByEventId(String eventId) {
        try {
            arEventGateCodeEntityRepository.deleteByEventId(eventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventNftBenefitById(int arEventNftBenefitId) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventNftBenefitRepository.deleteById(arEventNftBenefitId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventNftBannerById(int arNftBannerId) {
        try {
            // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
            arEventNftBannerRepository.deleteById(arNftBannerId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void deleteArEventNftTokenInfoByTempToken() {
        try {
            arEventNftTokenInfoRepository.deleteByArEventIdIsNullAndArEventWinningIdIsNull();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventNftTokenInfoByArEventWinningId(int arEventWinningId) {
        try {
            arEventNftTokenInfoRepository.deleteByArEventWinningId(arEventWinningId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventNftTokenInfoByChaneWinningType(int arEventWinningId) {
        if (PredicateUtils.isGreaterThanZero(arEventWinningId)) {
            ArEventNftTokenInfoEntity tokenInfo = this.findDistinctNftTokenInfoByArEventWinningId(arEventWinningId);
            if (PredicateUtils.isNotNull(tokenInfo.getId())) {
                this.deleteArEventNftTokenInfoByArEventWinningId(arEventWinningId);
            }
        }
    }

    @Transactional
    public void deleteArEventNftCouponInfoByArEventWinningId(int arEventWinningId) {
        try {
            arEventNftCouponInfoEntityRepository.deleteByArEventWinningId(arEventWinningId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventNftCouponInfoByChaneWinningType(int arEventWinningId) {
        if (PredicateUtils.isGreaterThanZero(arEventWinningId)) {
            Optional<ArEventNftCouponInfoEntity> optional = arEventNftCouponInfoEntityRepository.findDistinctFirstByArEventWinningId(arEventWinningId);
            if (optional.isPresent()) {
                this.deleteArEventNftCouponInfoByArEventWinningId(arEventWinningId);
            }
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void deleteArEventNftCouponInfoByTempCoupon() {
        try {
            arEventNftCouponInfoEntityRepository.deleteByArEventIdIsNullAndArEventWinningIdIsNull();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void deleteStampEventCouponInfoByTempCoupon() {
        try {
            arEventNftCouponInfoEntityRepository.deleteByStpIdIsNullAndArEventWinningIdIsNull();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventGiveAwayByEventId(String eventId) {
        try {
            arEventMapper.deleteEventGiveAwayByEventId(eventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogWinningByEventId(String eventId) {
        try {
            arEventMapper.deleteEventLogWinningByEventId(eventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteEventLogWinningLimitByArEventId(int arEventId) {
        try {
            arEventMapper.deleteEventLogWinningLimitByArEventId(arEventId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventWinningTextById(long index) {
        try {
            arEventWinningTextEntityRepository.deleteById(index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventRepositoryButtonById(long index) {
        try {
            arEventRepositoryButtonEntityRepository.deleteById(index);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteWebEventSmsAuthAtPrevOneDay() {
        try {
            arEventMapper.deleteWebEventSmsAuthAtPrevOneDay();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSequencesByName(String name) {
        try {
            arEventMapper.deleteSequencesByName(name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteArEventNftCouponRepositoryByEventWinningLogId(long eventWinningLogId) {
        arEventNftCouponRepositoryEntityRepository.deleteByEventWinningLogId(eventWinningLogId);
    }

    @Transactional
    public void deleteArEventNftCouponRepositoryByStampEventWinningLogId(long stampEventWinningLogId) {
        arEventNftCouponRepositoryEntityRepository.deleteByStampEventWinningLogId(stampEventWinningLogId);
    }

    @Transactional
    public void deleteEmTran() {
        arEventMapper.deleteEmTran();
    }

    @Transactional
    public void deleteArEventWinningButtonAddByWinningButtonId(int arEventWinningButtonId) {
        arEventWinningButtonAddEntityRepository.deleteAllByArEventWinningButtonId(arEventWinningButtonId);
    }

    @Transactional
    public void deleteArEventNftCouponInfoTemp(long tempSeq) {
        arEventMapper.deleteArEventNftCouponInfoTempBySeq(tempSeq);
    }

    @Transactional
    public void deleteArEventCouponRepositoryByEventId(String eventId) {
        arEventMapper.deleteArEventCouponRepositoryByEventId(eventId);
    }

    @Transactional
    public void deleteArEventNftCouponInfoByLegacy() {
        arEventMapper.deleteArEventNftCouponInfoByLegacy();
    }

    @Transactional
    public void deleteArEventNftCouponInfoTemp() {
        arEventMapper.deleteArEventNftCouponInfoTemp();
    }

    @Transactional
    public void deleteArEventNftCouponInfoByEventIdx(String eventType, int idx) {
        boolean isStampEvent = EventTypeDefine.isStampEvent(eventType);
        arEventMapper.deleteArEventNftCouponInfoByEventIdx(isStampEvent, idx);
    }

    /************************************************** DELETE END ****************************************************/

}
