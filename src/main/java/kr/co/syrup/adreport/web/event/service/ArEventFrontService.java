package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.SmsAuthReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventCouponDetailInfoResDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventNftCouponInfoResDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventNftRepositoryResDto;
import kr.co.syrup.adreport.web.event.dto.response.UserWinningInfoResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.entity.repository.*;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.CouponDetailInfoMapVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.CouponSaveReqMapperVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventBaseJoinArEventJoinEventButtonVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventLawInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class ArEventFrontService {

    /**************************************************** Autowired START ****************************************************/
    @Autowired
    private AES256Utils aes256Utils;

    @Autowired
    private ArEventMapper arEventMapper;

    @Autowired
    private BatchService batchService;

    @Autowired
    private EventGiveAwayDeliveryEntityRepository eventGiveAwayDeliveryEntityRepository;

    @Autowired
    private ArEventNftRepositoryRepository arEventNftRepositoryRepository;

    @Autowired
    private ArEventNftWalletRepository arEventNftWalletRepository;

    @Autowired
    private ArEventNftTokenInfoRepository arEventNftTokenInfoRepository;

    @Autowired
    private ArEventNftCouponRepositoryEntityRepository arEventNftCouponRepositoryEntityRepository;

    @Autowired
    private WebEventSmsAuthEntityRepository webEventSmsAuthEntityRepository;

    @Autowired
    private EventGiveAwayDeliveryButtonAddEntityRepository eventGiveAwayDeliveryButtonAddEntityRepository;

    @Autowired
    private ArEventNftCouponInfoEntityRepository arEventNftCouponInfoEntityRepository;

    /****************************************************   Autowired END   ****************************************************/

    /**************************************************** SAVE,UPDATE START ****************************************************/

    /**
     * EVENT_GIVE_AWAY_DELIVERY 정보 저장
     * @param eventGiveAwayDeliveryEntity
     * @return
     */
    @Transactional
    public int saveGiveAwayDelivery(EventGiveAwayDeliveryEntity eventGiveAwayDeliveryEntity) {
        int giveAwayId = 0;
        log.info("=================== eventGiveAwayDeliveryEntity :: " + eventGiveAwayDeliveryEntity.toString());
        try {
            //eventGiveAwayDeliveryEntityRepository.save(eventGiveAwayDeliveryEntity);
            arEventMapper.insertEventGiveAwayDelivery(eventGiveAwayDeliveryEntity);
            giveAwayId = eventGiveAwayDeliveryEntityRepository.findFirstByOrderByGiveAwayIdDesc().getGiveAwayId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return giveAwayId;
        }
    }

    @Transactional
    public void updateEventGiveAwayDeliveryIsReceiveByGiveAwayId(int giveAwayId, boolean isReceive) {
        if (giveAwayId > 0) {
            try {
                arEventMapper.updateEventGiveAwayDeliveryIsReceiveByGiveAwayId(giveAwayId, isReceive);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * EVENT_GIVE_AWAY_DELIVERY 패스워드 변경
     * @param eventId
     * @param newPassword
     */
    @Transactional
    public void updatePasswordEventGiveAwayDelivery(String eventId, String phoneNumber, String newPassword) {
        if (PredicateUtils.isNotNull(eventId)
                || PredicateUtils.isNotNull(phoneNumber)
                || PredicateUtils.isNotNull(newPassword)) {
            try {
                arEventMapper.updatePasswordEventGiveAwayDelivery(eventId, phoneNumber, SecurityUtils.encryptSHA256(newPassword.trim()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveArEventNftWallet(ArEventNftWalletEntity arEventNftWalletEntity) {
        try {
            arEventNftWalletRepository.saveAndFlush(arEventNftWalletEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateArEventNftWalletIdFromNftRepositoryByGiveAwayIds(long arEventWalletId, List<Integer>giveAwayIds) {
        if (arEventWalletId > 0L && !PredicateUtils.isNullList(giveAwayIds)) {
            try {
                arEventMapper.updateWalletIdFromArEventNftRepository(arEventWalletId, giveAwayIds);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateEncryptEventGiveAwayDelivery(boolean isEncrypt) {
        EventGiveAwayDeliveryEntity lastEntity = eventGiveAwayDeliveryEntityRepository.findFirstByOrderByGiveAwayIdDesc();
        Optional<List<EventGiveAwayDeliveryEntity>> optional = eventGiveAwayDeliveryEntityRepository.findAllByGiveAwayIdBetween(0, lastEntity.getGiveAwayId());
        if (optional.isPresent()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (EventGiveAwayDeliveryEntity entity : optional.get()) {
                Map<String, Object> map = new HashMap<>();

                map.put("giveAwayId", entity.getGiveAwayId());

                if (StringUtils.isNotEmpty(entity.getName()) && PredicateUtils.isNotNull(entity.getName())) {
                    if (isEncrypt) {
                        map.put("name", aes256Utils.encrypt(entity.getName()));
                    } else {
                        map.put("name", aes256Utils.decrypt(entity.getName()));
                    }
                }
                if (StringUtils.isNotEmpty(entity.getPhoneNumber()) && PredicateUtils.isNotNull(entity.getPhoneNumber())) {
                    if (isEncrypt) {
                        map.put("phoneNumber", aes256Utils.encrypt(entity.getPhoneNumber()));
                    } else {
                        map.put("phoneNumber", aes256Utils.decrypt(entity.getPhoneNumber()));
                    }
                }
                if (StringUtils.isNotEmpty(entity.getAddress()) && PredicateUtils.isNotNull(entity.getAddress())) {
                    if (isEncrypt) {
                        map.put("address", aes256Utils.encrypt(entity.getAddress()));
                    } else {
                        map.put("address", aes256Utils.decrypt(entity.getAddress()));
                    }
                }
                if (StringUtils.isNotEmpty(entity.getAddressDetail()) && PredicateUtils.isNotNull(entity.getAddressDetail())) {
                    if (isEncrypt) {
                        map.put("addressDetail", aes256Utils.encrypt(entity.getAddressDetail()));
                    } else {
                        map.put("addressDetail", aes256Utils.decrypt(entity.getAddressDetail()));
                    }
                }

                log.info("> " + map);
                list.add(map);
            }

            batchService.batchUpdateEncryptEventGiveAwayDelivery(list);
        }
    }

    @Transactional
    public void saveArEventNftCouponRepository(ArEventNftCouponRepositoryEntity couponRepositoryEntity) {
        try {
            arEventNftCouponRepositoryEntityRepository.save(couponRepositoryEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateCouponIsPayedById(long id, boolean isPayed) {
        try {
            arEventMapper.updateNftCouponIsPayed(id, isPayed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateNftTokenIsPayedById(long id) {
        try {
            arEventMapper.updateNftTokenIsPayed(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateNftRepositoryAtGiveAwayDelivery(long eventWinningLogId, long giveAwayId) {
        try {
            arEventMapper.updateNftRepositoryAtGiveAwayDelivery(eventWinningLogId, giveAwayId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateCouponRepositoryAtGiveAwayDelivery(long eventWinningLogId, long giveAwayId) {
        try {
            arEventMapper.updateCouponRepositoryAtGiveAwayDelivery(eventWinningLogId, giveAwayId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public Long saveSelectAvailableArEventCouponByArEventIdAndArEventWinningId(int arEventId, int arEventWinningId, long eventWinningLogId, boolean isStamp) {
        CouponSaveReqMapperVO reqVO = new CouponSaveReqMapperVO().builder()
                .arEventWinningId(arEventWinningId)
                .build();

        Long nftCouponInfoId = null;
        try {
            //스탬프가 아닐때
            if (!isStamp) {
                reqVO.setArEventId(arEventId);
                reqVO.setEventWinningLogId(eventWinningLogId);
                arEventMapper.saveSelectAvailableArEventCouponByArEventIdAndArEventWinningId(reqVO);
            } else {
            //스탬프 일때
                reqVO.setStpId(arEventId);
                reqVO.setStampEventWinningLogId(eventWinningLogId);
                arEventMapper.saveSelectAvailableArEventCouponByStpIdAndArEventWinningId(reqVO);
            }
            nftCouponInfoId = arEventMapper.selectCouponIdFromArEventNftCouponRepositoryById(reqVO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return nftCouponInfoId;
        }
    }

    @Transactional
    public Long saveSelectAvailableArEventNftTokenByArEventIdAndArEventWinningId(int arEventId, int arEventWinningId, long eventWinningLogId, boolean isStamp) {
        CouponSaveReqMapperVO reqVO = new CouponSaveReqMapperVO();
        if (isStamp) {
            reqVO = new CouponSaveReqMapperVO().builder()
                    .stpId(arEventId)
                    .arEventWinningId(arEventWinningId)
                    .eventWinningLogId(eventWinningLogId)
                    .build();
        } else {
            reqVO = new CouponSaveReqMapperVO().builder()
                    .arEventId(arEventId)
                    .arEventWinningId(arEventWinningId)
                    .eventWinningLogId(eventWinningLogId)
                    .build();
        }


        Long arEventNftTokenInfoId = null;
        try {
            arEventMapper.saveSelectAvailableArEventNftTokenByArEventIdAndArEventWinningId(reqVO);
            arEventNftTokenInfoId = arEventMapper.selectNftTokenIdFromArEventNftTokenRepositoryById(reqVO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return arEventNftTokenInfoId;
        }
    }

    @Transactional
    public void saveArEventNftCouponRepositoryByOcbCoupon(long eventWinningLogId, String ocbCouponId, int giveAwayId) {
        CouponSaveReqMapperVO reqVO = new CouponSaveReqMapperVO().builder()
                .eventWinningLogId(eventWinningLogId)
                .ocbCouponId(ocbCouponId)
                .giveAwayId(giveAwayId)
                .build();

        try {
            arEventMapper.saveArEventNftCouponRepository(reqVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public WebEventSmsAuthEntity saveWebEventSmsAuth(String eventId, String phoneNumber, String authMenuType) {
        if (PredicateUtils.isNotNull(eventId) && PredicateUtils.isNotNull(phoneNumber)) {

            WebEventSmsAuthEntity saveEntity = WebEventSmsAuthEntity.saveOf(eventId, phoneNumber, authMenuType);
            try {
                webEventSmsAuthEntityRepository.save(saveEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return saveEntity;
        }
        return null;
    }

    @Transactional
    public void deleteWebEventSmsAuth(WebEventSmsAuthEntity smsAuthEntity) {
        if (PredicateUtils.isNotNull(smsAuthEntity)) {
            try {
                arEventMapper.deleteWebEventSmsAuth(smsAuthEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void deleteWebEventSmsAuthByEventId(String eventId) {
        if (PredicateUtils.isNotNull(eventId)) {
            try {
                arEventMapper.deleteWebEventSmsAuthByEventId(eventId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    @Transactional
    public void saveAllEventGiveAwayDeliveryButtonAdd(List<EventGiveAwayDeliveryButtonAddEntity> entityList, int giveAwayId) {
        if (PredicateUtils.isNotNullList(entityList)) {
            for (EventGiveAwayDeliveryButtonAddEntity entity : entityList) {
                entity.setGiveAwayId(giveAwayId);
                entity.setCreatedDate(DateUtils.returnNowDate());
            }
            try {
                arEventMapper.saveEventGiveAwayDeliveryButtonAddList(entityList);
                //eventGiveAwayDeliveryButtonAddEntityRepository.saveAll(entityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void deleteEventGiveAwayByGiveAwayId(int giveAwayId) {
        try {
            eventGiveAwayDeliveryEntityRepository.deleteById(giveAwayId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**************************************************** SAVE, UPDATE, DELETE END ****************************************************/


    /**************************************************** SELECT START ****************************************************/

    /**
     * EVENT_GIVE_AWAY_DELIVERY 리트스 가져오기 :: UserWinningInfoResDto 변환
     * @param eventId
     * @param phoneNumber
     * @return
     */
    public List<UserWinningInfoResDto> findAllGiveAwayDeliveryByEventIdAndPhoneNumber(String eventId, String phoneNumber) {
        List<EventGiveAwayDeliveryEntity> entityList = eventGiveAwayDeliveryEntityRepository.findAllByEventIdAndPhoneNumberOrderByCreatedDateAsc(eventId, phoneNumber);
        if (!entityList.isEmpty()) {
            return ModelMapperUtils.convertModelInList(entityList, UserWinningInfoResDto.class);
        }
        return null;
    }

    public List<UserWinningInfoResDto> findAllGiveAwayDeliveryByEventIdAndAttendCode(String eventId, String attendCode) {
        return arEventMapper.selectGiveAwayDeliveryByEventIdAndAttendCode(eventId, attendCode);
    }

    /**
     * EVENT_GIVE_AWAY_DELIVERY 갸져오기 (인덱스 기준)
     * @param giveAwayId
     * @return
     */
    public EventGiveAwayDeliveryEntity findEventGiveAwayById(int giveAwayId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return eventGiveAwayDeliveryEntityRepository.getById(giveAwayId);
    }

    /**
     * EVENT_GIVE_AWAY_DELIVERY 개수 가져오기
     * @param eventId
     * @param phoneNumber
     * @return
     */
    public int countGiveAwayDeliveryByEventIdAndPhoneNumberPasswordIsNotNull(String eventId, String phoneNumber) {
        return (int)eventGiveAwayDeliveryEntityRepository.countByEventIdAndPhoneNumberAndGiveAwayPasswordIsNotNull(eventId, phoneNumber);
    }

    /**
     * EVENT_GIVE_AWAY_DELIVERY 개수 가져오기
     * @param eventId
     * @param phoneNumber
     * @return
     */
    public int countGiveAwayDeliveryByEventIdAndPhoneNumber(String eventId, String phoneNumber) {
        return (int)eventGiveAwayDeliveryEntityRepository.countByEventIdAndPhoneNumber(eventId, phoneNumber);
    }

    /**
     * EVENT_GIVE_AWAY_DELIVERY 개수 가져오기
     * @param eventId
     * @param phoneNumber
     * @return
     */
    public int countGiveAwayDeliveryByEventIdAndPhoneNumberAndToday(String eventId, String phoneNumber) {
        return arEventMapper.selectCountEventGiveAwayDeliveryAndEventIdAndPhoneNumberAndCreatedDate(eventId, phoneNumber, DateUtils.getNow("yyyy-MM-dd"));
    }

    public List<ArEventNftRepositoryEntity> findArEventNftRepositoryByGiveAwayIdIn(List<Integer>giveAwayIdList) {
        return arEventNftRepositoryRepository.findAllByGiveAwayIdInOrderByIdAsc(giveAwayIdList).orElseGet(ArrayList::new);
    }

    public ArEventNftRepositoryEntity findArEventNftRepositoryById(long arNftRepositoryId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventNftRepositoryRepository.getById(arNftRepositoryId);
    }

    public ArEventNftWalletEntity findArEventNftWalletByPhoneNumber(String phoneNumber) {
        return arEventNftWalletRepository.findByUserPhoneNumber(phoneNumber).orElseGet(ArEventNftWalletEntity::new);
    }

    public ArEventNftWalletEntity findArEventNftWalletById(long arEventNftWalletId) {
        return arEventNftWalletRepository.findById(arEventNftWalletId).orElseGet(ArEventNftWalletEntity::new);
    }

    public ArEventNftTokenInfoEntity findArEventNftTokenInfoEntityById(long arEventNftTokenInfoId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventNftTokenInfoRepository.getById(arEventNftTokenInfoId);
    }

    public int countNftWalletByArEventIdAndUserPhoneNumber(int arEventId, String userPhoneNumber, String walletType) {
        return (int)arEventNftWalletRepository.countByArEventIdAndUserPhoneNumberAndNftWalletType(arEventId, userPhoneNumber, walletType);
    }

    public ArEventNftWalletEntity findArEventNftWalletEntityByWalletAddressAndWalletTypeAndArEventId(String walletAddress, String walletType, int arEventId) {
        return arEventNftWalletRepository.findByNftWalletAddressAndNftWalletTypeAndArEventId(walletAddress, walletType, arEventId).orElseGet(ArEventNftWalletEntity::new);
    }

    /**
     * 이벤트가 수정가능한 상태인지 확인
     * @param contractStatus
     * @return
     */
    public boolean isPossibleUpdateEvent(String contractStatus) {
        if (StringUtils.isNotEmpty(contractStatus)) {
            if (StringUtils.equals("00", contractStatus) || StringUtils.equals("01", contractStatus)
                    || StringUtils.equals("02", contractStatus) || StringUtils.equals("0250", contractStatus)
                    || StringUtils.equals("03", contractStatus)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public ArEventNftCouponRepositoryEntity findArEventNftCouponRepositoryEntityById(long repositoryId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        Optional<ArEventNftCouponRepositoryEntity> optional = arEventNftCouponRepositoryEntityRepository.findById(repositoryId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public ArEventCouponDetailInfoResDto findArEventNftCouponRepositoryEntityJoinArEventWInningById(long repositoryId) {
        // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
        return arEventMapper.selectArEventNftCouponRepositoryEntityJoinArEventWInningById(repositoryId);
    }

    public List<ArEventNftCouponRepositoryEntity> findArEventNftCouponRepositoryByGiveAwayIdIn(List<Integer>giveAwayIdList) {
        return arEventNftCouponRepositoryEntityRepository.findAllByGiveAwayIdInOrderByIdAsc(giveAwayIdList);
    }

    public List<ArEventNftCouponRepositoryEntity> findArEventNftCouponRepositoryByStpGiveAwayIdIn(List<Long>stpGiveAwayIdList) {
        return arEventNftCouponRepositoryEntityRepository.findAllByStpGiveAwayIdInOrderByIdAsc(stpGiveAwayIdList);
    }

    public int countWebEventSmsAuth(WebEventSmsAuthEntity smsAuthEntity) {
        if (PredicateUtils.isNotNull(smsAuthEntity)) {
            return arEventMapper.countWebEventSmsAuth(smsAuthEntity);
        }
        return 0;
    }

    public int countSendWebEventSmsAuthByToday(String phoneNumber) {
        if (PredicateUtils.isNotNull(phoneNumber)) {
            return arEventMapper.countSendWebEventSmsAuthByToday(phoneNumber);
        }
        return 0;
    }

    public WebEventSmsAuthEntity findWebEventSmsAuthBySmsAuthCode(String smsAuthCode) {
        return arEventMapper.findWebEventSmsAuthBySmsAuthCode(smsAuthCode);
    }

    /**
     * 시퀀스 넘버 가져오기 (없으면 'sequenceName' 명으로 새로 생성)
     * @param sequenceName
     * @return
     */
    public long findSequenceByName(String sequenceName) {
        if (PredicateUtils.isNotNull(sequenceName) || StringUtils.isNotEmpty(sequenceName)) {
            Long sequence = arEventMapper.selectWebEventSequenceNextvalBySequence(sequenceName);
            if (PredicateUtils.isNull(sequence)) {
                arEventMapper.saveSequences(sequenceName, 0L);
                return 1L;
            }
            if (PredicateUtils.isNotNull(sequence)) {
                return sequence;
            }
        }
        return 0L;
    }

    public String findEventLawContentsByEventTypeAndLawType(String eventType, String lawType) {
        if (PredicateUtils.isNotNull(eventType)) {
            return arEventMapper.selectEventLawContentsByEventTypeAndLawTypeBetweenStartDateEndDate(eventType, lawType, DateUtils.getNowDay());
        }
        return "";
    }

    public List<HashMap<String, Object>> findEventLawHyperLinkList(String eventType, String lawType) {
        return arEventMapper.selectEventLawHyperLinkList(eventType, lawType);
    }

    public EventLawInfoVO findEventLawContentsByIdx(Integer idx) {
        return arEventMapper.selectEventLawContentsByIdx(idx);
    }


    /**
     * EVENT_GIVE_AWAY_DELIVERY 개수 가져오기
     * @param eventId
     * @param phoneNumber
     * @return
     */
    @Deprecated
    public int countGiveAwayDeliveryByEventIdAndPhoneNumberPasswordIsNull(String eventId, String phoneNumber) {
        return (int)eventGiveAwayDeliveryEntityRepository.countByEventIdAndPhoneNumberAndGiveAwayPasswordOrGiveAwayPasswordIsNull(eventId, phoneNumber, "");
    }
    /**************************************************** SELECT END ****************************************************/

    @Cacheable(cacheNames = "findArEventGagePageInfo", keyGenerator = "customKeyGenerator")
    public EventBaseJoinArEventJoinEventButtonVO findArEventGagePageInfo(String eventId) {
        if (PredicateUtils.isNotNull(eventId) || StringUtils.isNotEmpty(eventId)) {
            EventBaseJoinArEventJoinEventButtonVO vo = arEventMapper.selectEventBaseJoinArEventJoinEventButton(eventId);
            if (PredicateUtils.isNull(vo.getStpConnectYn())) {
                vo.setStpConnectYn("N");
            }
            return vo;
        }
        return null;
    }

    @Cacheable(cacheNames = "findArEventBaseInfoByEventId", keyGenerator = "customKeyGenerator")
    public EventBaseJoinArEventJoinEventButtonVO findArEventBaseInfoByEventId(String eventId) {
        if (PredicateUtils.isNotNull(eventId)) {
            return arEventMapper.selectEventBaseJoinArEvent(eventId);
        }
        return null;
    }

    public List<EventGiveAwayDeliveryEntity> findEventGiveAwayDeliveryListAtNftWinning(String eventId, String phoneNumber) {
        return arEventMapper.selectEventGiveAwayDeliveryListAtNftWinning(eventId, phoneNumber);
    }

    public List<EventGiveAwayDeliveryEntity> findEventGiveAwayDeliveryListAtNftWinningByAttendCode(String eventId, String attendCode) {
        return arEventMapper.selectEventGiveAwayDeliveryListAtNftWinningByAttendCode(eventId, attendCode);
    }

    @Cacheable(cacheNames = "findArEventNftCouponInfoEntityById", keyGenerator = "customKeyGenerator")
    public ArEventNftCouponInfoEntity findArEventNftCouponInfoEntityById(Long id) {
        return arEventNftCouponInfoEntityRepository.getById(id);
    }

    public ArEventNftCouponInfoResDto findArEventNftCouponInfJoinArEventWinningById(Long id) {
        return arEventMapper.selectArEventNftCouponInfJoinArEventWinningById(id);
    }

    public CouponDetailInfoMapVO findCouponDetailInfo(Long id) {
        return arEventMapper.selectCouponDetailInfo(id);
    }

    public Map<String, Object> findEventIdArEventWinningIdByCouponRepositoryId(Long id) {
        return arEventMapper.selectEventIdArEventWinningIdByCouponRepositoryId(id);
    }

    public Map<String, Object> findEventIdArEventWinningIdByCouponRepositoryIdAtStamp(Long id) {
        return arEventMapper.selectEventIdArEventWinningIdByCouponRepositoryIdAtStamp(id);
    }

    public String findEventIdByArEventWinningId(int arEventWinningId) {
        return arEventMapper.selectEventIdByArEventWinningId(arEventWinningId);
    }

    @Cacheable(cacheNames = "findArEventWinningListByEventIdxAndMappingNumber", keyGenerator = "customKeyGenerator")
    public LinkedList<ArEventWinningEntity> findArEventWinningListByEventIdxAndMappingNumber(int eventIdx, int mappingNumber, String eventType) {
        if (PredicateUtils.isEqualsStr(eventType, EventTypeDefine.STAMP.name())) {
            return arEventMapper.selectArEventWinningListByStpIdAndMappingNumber(eventIdx, mappingNumber);
        } else {
            return arEventMapper.selectArEventWinningListByArEventIdAndMappingNumber(eventIdx, mappingNumber);
        }
    }

    public LinkedList<ArEventWinningEntity> findArEventWinningListByStpIdAndStampAttendSortLogCount(int stpId, int winningAttemptOrder, String attendValue, String attendType) {
        return arEventMapper.selectArEventWinningListByStpIdAndStampAttendSortLogCount(stpId, winningAttemptOrder, attendValue, attendType);
    }

    @Transactional
    public void updateArEventNftCouponRepositoryStpGiveAwayIdByCouponId(Long couponId, Long stpGiveAwayId) {
        arEventMapper.updateArEventNftCouponRepositoryStpGiveAwayIdByCouponId(couponId, stpGiveAwayId);
    }

}
