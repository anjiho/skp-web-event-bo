package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.InjectCreatedModifyName;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.model.StampEventGateCodeModel;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.EventHtmlDto;
import kr.co.syrup.adreport.web.event.dto.request.EventRepositoryButtonReqDto;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningDto;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningTextReqDto;
import kr.co.syrup.adreport.web.event.dto.request.api.OcbPointSaveReqDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.BatchService;
import kr.co.syrup.adreport.web.event.service.ExcelService;
import kr.co.syrup.adreport.web.event.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SodarCommonLogic {

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private LogService logService;

    @Autowired
    private StampLogService stampLogService;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private BatchService batchService;

    /**
     * ar_event_winning 업데이트 공통 로직 (AR, STAMP)
     * @param eventWinningDtoList
     * @param eventIdx
     * @param isStamp
     */
    @Transactional
    public void updateArEventWinning(List<EventWinningDto> eventWinningDtoList, int eventIdx, boolean isStamp) {
        if (PredicateUtils.isNotNullList(eventWinningDtoList))  {
            int i = 0;
            List<ArEventWinningEntity> arEventWinningEntityList = ModelMapperUtils.convertModelInList(eventWinningDtoList, ArEventWinningEntity.class);
            if (PredicateUtils.isNotNullList(arEventWinningEntityList)) {
                //저장되어있는 당첨리스트 가져오기
                List<ArEventWinningEntity> exitsArEventWinningList = new ArrayList<>();
                if (isStamp) {
                    exitsArEventWinningList = arEventService.findArEventWinningListNoCacheByStpId(eventIdx);
                } else {
                    exitsArEventWinningList = arEventService.findArEventWinningListByArEventId(eventIdx);
                }

                if (PredicateUtils.isNotNullList(exitsArEventWinningList)) {
                    if ( PredicateUtils.isGreaterThan(exitsArEventWinningList.size(), eventWinningDtoList.size()) ) {
                        for (EventWinningDto newArEventWinning : eventWinningDtoList) {
                            if (PredicateUtils.isNotNull(newArEventWinning.getArEventWinningId()) || PredicateUtils.isGreaterThanZero(newArEventWinning.getArEventWinningId())) {
                                ArEventWinningEntity arEventWinning = arEventService.findByArEventWinningByIdNoCache(newArEventWinning.getArEventWinningId());
                                if (PredicateUtils.isNotNull(arEventWinning)) {
                                    //중복되는 항목 삭제
                                    exitsArEventWinningList.removeIf(data -> Objects.equals(data.getArEventWinningId(), arEventWinning.getArEventWinningId()));
                                }
                            }
                        }
                        //기존 데이터중 삭제해야 할 항목 삭제
                        if (PredicateUtils.isNotNullList(exitsArEventWinningList)) {
                            try {
                                exitsArEventWinningList.forEach(element -> {
                                    //당첨정보 삭제
                                    arEventService.deleteArEventWinningById(element.getArEventWinningId());
                                    //당첨정보의 버튼정보 삭제
                                    arEventService.deleteArEventWinningButtonByArEventWinningId(element.getArEventWinningId());
                                    //삭제 되어야 할 항목의 당첨 종류가 쿠폰이면 쿠폰 정보 테이블 삭제
                                    //쿠폰형일때
                                    if (PredicateUtils.isEqualsStr(element.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                                        arEventService.deleteArEventNftCouponInfoByArEventWinningId(element.getArEventWinningId());
                                    }
                                });
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            } else {
                log.debug("당첨정보가 있는상태에서 당첨정보를 없는 상태로 업데이트 할때 저장되어 있는 당첨정보, 당첨정보 버튼 데이터를 삭제처리");
                //당첨정보가 있는상태에서 당첨정보를 없는 상태로 업데이트 할때 저장되어 있는 당첨정보, 당첨정보 버튼 데이터를 삭제처리
                List<ArEventWinningEntity> exitsArEventWinningList = new ArrayList<>();
                if (isStamp) {
                    exitsArEventWinningList = arEventService.findArEventWinningListNoCacheByStpId(eventIdx);
                } else {
                    exitsArEventWinningList = arEventService.findArEventWinningListByArEventId(eventIdx);
                }
                if (PredicateUtils.isNotNullList(exitsArEventWinningList)) {
                    for (ArEventWinningEntity winningEntity : exitsArEventWinningList) {
                        try {
                            arEventService.deleteArEventWinningById(winningEntity.getArEventWinningId());
                            arEventService.deleteArEventWinningButtonByArEventWinningId(winningEntity.getArEventWinningId());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }

            for (ArEventWinningEntity arEventWinningEntity : arEventWinningEntityList) {
                if (isStamp) {
                    arEventWinningEntity.setStpId(eventIdx);
                } else {
                    arEventWinningEntity.setArEventId(eventIdx);
                }
                arEventWinningEntity.setCreatedDate(DateUtils.returnNowDate());

                if (PredicateUtils.isNull(arEventWinningEntity.getWinningTimeType()) || StringUtils.isEmpty(arEventWinningEntity.getWinningTimeType())) {
                    arEventWinningEntity.setWinningTimeType(StringDefine.N.name());
                }
                if (PredicateUtils.isNull(arEventWinningEntity.getAttendCodeWinningType()) || StringUtils.isEmpty(arEventWinningEntity.getAttendCodeWinningType())) {
                    arEventWinningEntity.setAttendCodeWinningType(StringDefine.N.name());
                }
                if (PredicateUtils.isNull(arEventWinningEntity.getAttendCodeLimitType())) {
                    arEventWinningEntity.setAttendCodeLimitType(0);
                }

                //ar_event_winning_limit 삭제 작업 시작
                //기존의 당첨정보 가져오기
                if (PredicateUtils.isNotNull(arEventWinningEntity.getArEventWinningId())) {
                    ArEventWinningEntity selectedWinningEntity = arEventService.findByArEventWinningByIdNoCache(arEventWinningEntity.getArEventWinningId());
                    if (PredicateUtils.isNotNull(selectedWinningEntity)) {

                        if (PredicateUtils.isNull(arEventWinningEntity.getTotalWinningNumber()))
                            arEventWinningEntity.setTotalWinningNumber(0);
                        if (PredicateUtils.isNull(selectedWinningEntity.getTotalWinningNumber()))
                            selectedWinningEntity.setTotalWinningNumber(0);
                        if (PredicateUtils.isGreaterThanZero(arEventWinningEntity.getTotalWinningNumber())) {
                            //기존 당첨정보의 전체당첨 수량보다 수정할 전체담청수량이 크면 제한테이블 삭제처리
                            if (PredicateUtils.isGreaterThan(arEventWinningEntity.getTotalWinningNumber(), selectedWinningEntity.getTotalWinningNumber())) {
                                if (isStamp) {
                                    stampLogService.deleteStampEventLogWinningLimitByStpIdAndCode(eventIdx, StringTools.joinStringsNoSeparator(String.valueOf(eventIdx), String.valueOf(arEventWinningEntity.getEventWinningSort())), EventLogWinningLimitDefine.ID_SORT.name());
                                } else {
                                    logService.deleteEventLogWinningLimitByArEventIdAndCodeAndDesc(eventIdx, StringTools.joinStringsNoSeparator(String.valueOf(eventIdx), String.valueOf(arEventWinningEntity.getEventWinningSort())), EventLogWinningLimitDefine.ID_SORT.name());
                                }
                            }
                        }

                        if (PredicateUtils.isNull(arEventWinningEntity.getDayWinningNumber()))
                            arEventWinningEntity.setDayWinningNumber(0);
                        if (PredicateUtils.isNull(selectedWinningEntity.getDayWinningNumber()))
                            selectedWinningEntity.setDayWinningNumber(0);
                        if (PredicateUtils.isGreaterThanZero(arEventWinningEntity.getDayWinningNumber())) {
                            //기존 당첨정보의 일당첨 수량보다 수정할 일담청수량이 크면 제한테이블 삭제처리
                            if (PredicateUtils.isGreaterThan(arEventWinningEntity.getDayWinningNumber(), selectedWinningEntity.getDayWinningNumber())) {
                                if (isStamp) {
                                    stampLogService.deleteStampEventLogWinningLimitByStpIdAndCode(eventIdx, StringTools.joinStringsNoSeparator(String.valueOf(eventIdx), String.valueOf(arEventWinningEntity.getEventWinningSort())), EventLogWinningLimitDefine.ID_SORT_TODAY.name());
                                } else {
                                    logService.deleteEventLogWinningLimitByArEventIdAndCodeAndDesc(eventIdx, StringTools.joinStringsNoSeparator(String.valueOf(eventIdx), String.valueOf(arEventWinningEntity.getEventWinningSort())), EventLogWinningLimitDefine.ID_SORT_TODAY.name());
                                }
                            }
                        }

                        if (PredicateUtils.isNull(arEventWinningEntity.getHourWinningNumber()))
                            arEventWinningEntity.setHourWinningNumber(0);
                        if (PredicateUtils.isNull(selectedWinningEntity.getHourWinningNumber()))
                            selectedWinningEntity.setHourWinningNumber(0);
                        if (PredicateUtils.isGreaterThanZero(arEventWinningEntity.getHourWinningNumber())) {
                            //기존 당첨정보의 시간당첨 수량보다 수정할 시간담청수량이 크면 제한테이블 삭제처리
                            if (PredicateUtils.isGreaterThan(arEventWinningEntity.getHourWinningNumber(), selectedWinningEntity.getHourWinningNumber())) {
                                if (isStamp) {
                                    stampLogService.deleteStampEventLogWinningLimitByStpIdAndCode(eventIdx, StringTools.joinStringsNoSeparator(String.valueOf(eventIdx), String.valueOf(arEventWinningEntity.getEventWinningSort())), EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name());
                                } else {
                                    logService.deleteEventLogWinningLimitByArEventIdAndCodeAndDesc(eventIdx, StringTools.joinStringsNoSeparator(String.valueOf(eventIdx), String.valueOf(arEventWinningEntity.getEventWinningSort())), EventLogWinningLimitDefine.ID_SORT_TODAY_HOUR.name());
                                }
                            }
                        }
                    }
                }
                //ar_event_winning_limit 삭제 작업 끝

                //당첨정보 저장
                try {
                    arEventService.saveEventWinning(arEventWinningEntity);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                //NFT 임시 파일 업로드 파일명이 있을때 AR_EVENT_NFT_TOKEN_INFO 업데이트
                if (PredicateUtils.isNotNull(arEventWinningEntity.getNftExcelUploadFileName())) {
                    //NFT 기본형
                    if (PredicateUtils.isEqualsStr(arEventWinningEntity.getWinningType(), WinningTypeDefine.NFT.code())) {
                        try {
                            if (PredicateUtils.isNotNull(arEventWinningEntity.getUploadFileSeqNum())) {
                                if (isStamp) {
                                    arEventService.saveArEventNftTokenFromTempTableBySeq(0, eventIdx, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                                } else {
                                    arEventService.saveArEventNftTokenFromTempTableBySeq(eventIdx, 0, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //NFT 쿠폰형
                    if (PredicateUtils.isEqualsStr(arEventWinningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                        try {
                            if (PredicateUtils.isNotNull(arEventWinningEntity.getUploadFileSeqNum())) {
                                if (isStamp) {
                                    arEventService.saveArEventNftCouponFromTempTableBySeq(0, eventIdx, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                                } else {
                                    arEventService.saveArEventNftCouponFromTempTableBySeq(eventIdx, 0, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            //파일업로드 temp 테이블 삭제
                            if (PredicateUtils.isNotNull(arEventWinningEntity.getUploadFileSeqNum())) {
                                arEventService.deleteArEventNftCouponInfoTemp(arEventWinningEntity.getUploadFileSeqNum());
                            }
                        }
                    }
                }

                //당첨 타입이 NFT 에서 다른 타입으로 변경되었을때 NFT 토큰 테이블의 기존 NFT 토큰, NFT 쿠폰 정보 삭제
                if (!PredicateUtils.isEqualsStr(arEventWinningEntity.getWinningType(), WinningTypeDefine.NFT.code())
                        && !PredicateUtils.isEqualsStr(arEventWinningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                    arEventService.deleteArEventNftTokenInfoByChaneWinningType(arEventWinningEntity.getArEventWinningId());
                    arEventService.deleteArEventNftCouponInfoByChaneWinningType(arEventWinningEntity.getArEventWinningId());
                }

                //저장할 당첨버튼 정보 목록
                List<ArEventWinningButtonEntity> arEventWinningButtonEntityList = ModelMapperUtils.convertModelInList(eventWinningDtoList.get(i).getArEventWinningButtonInfo(), ArEventWinningButtonEntity.class);
                //저장되어있는 당첨버튼 정보 목록
                List<ArEventWinningButtonEntity> exitsWinningButtonList = arEventService.findAllArEventWinningButtonByArEventWinningId(arEventWinningEntity.getArEventWinningId());

                int j = 0;
                for (ArEventWinningButtonEntity buttonEntity : arEventWinningButtonEntityList) {

                    if (PredicateUtils.isNotNullList(exitsWinningButtonList)) {
                        //저장되어 있는 당첨버튼 목록이 저장할 버튼 목록 보다 클때
                        if ( exitsWinningButtonList.size() > arEventWinningButtonEntityList.size() ) {
                            //저장되어 있는 당첨버튼이 있는지 DB 조회
                            ArEventWinningButtonEntity findButtonEntity = arEventService.findArEventWinningButtonByIdNoCache(buttonEntity.getArEventWinningButtonId());
                            if (PredicateUtils.isNotNull(findButtonEntity)) {
                                //DB 조회 항목이 있으면 삭제
                                exitsWinningButtonList.removeIf(data -> data.getArEventWinningButtonId() == findButtonEntity.getArEventWinningButtonId());
                            }
                            //중복제거된 DB에 남아있는 항목은 삭제되어야 할 항목이기 때문에 삭제 로직 처리
                            if (PredicateUtils.isGreaterThanZero(exitsWinningButtonList.size())) {
                                exitsWinningButtonList.forEach(button -> {
                                    arEventService.deleteArEventWinningButtonById(button.getArEventWinningButtonId());
                                });
                            }
                        }
                    }

                    buttonEntity.setArEventWinningId(arEventWinningEntity.getArEventWinningId());
                    buttonEntity.setCreatedDate(DateUtils.returnNowDate());

                    try {
                        arEventService.saveEventWinningButton(buttonEntity);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    //DTWS-70 당첨정보 추가 버튼 정보가 있을때
                    if (PredicateUtils.isNotNull(eventWinningDtoList.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonAddInfo())) {
                        List<ArEventWinningButtonAddEntity> winningButtonAddEntityList = ModelMapperUtils.convertModelInList(eventWinningDtoList.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonAddInfo(), ArEventWinningButtonAddEntity.class);
                        if (PredicateUtils.isNotNullList(winningButtonAddEntityList)) {
                            try {
                                arEventService.saveAllArEventWinningButtonAdd(buttonEntity.getArEventWinningButtonId(), winningButtonAddEntityList);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        } else {
                            if (PredicateUtils.isNotNull(eventWinningDtoList.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonId())) {
                                try {
                                    arEventService.deleteArEventWinningButtonAddByWinningButtonId(eventWinningDtoList.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonId());
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        }
                    } else {
                        try {
                            arEventService.deleteArEventWinningButtonAddByWinningButtonId(eventWinningDtoList.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonId());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    j++;
                }

                //ar_event_nft_benefit 정보 수정 시작
                //수정할 NFT 혜택정보
                List<ArEventNftBenefitEntity> arEventNftBenefitEntityList = new ArrayList<>();
                if (!PredicateUtils.isNullList(eventWinningDtoList.get(i).getNftBenefitInfo())) {
                    arEventNftBenefitEntityList = ModelMapperUtils.convertModelInList(eventWinningDtoList.get(i).getNftBenefitInfo(), ArEventNftBenefitEntity.class);
                }
                //저장되어있는 NFT 혜택정보
                List<ArEventNftBenefitEntity> exitsArEventNftBenefitList = arEventService.findAllArEventNftBenefitByArEventWinningIdNoCache(arEventWinningEntity.getArEventWinningId());

                if (PredicateUtils.isNotNullList(arEventNftBenefitEntityList)) {
                    for (ArEventNftBenefitEntity nftBenefit : arEventNftBenefitEntityList) {
                        //저장되어있는 NFT 혜택정보가 있으면
                        if (PredicateUtils.isNotNullList(exitsArEventNftBenefitList)) {
                            //저장되어 있는 NFT 혜택이 있는지 DB 조회
                            ArEventNftBenefitEntity findExitsNftBenefit = arEventService.findArEventNftBenefitById(nftBenefit.getArEventNftBenefitId());
                            if (PredicateUtils.isNotNull(findExitsNftBenefit)) {
                                //DB 조회 항목이 있으면 삭제
                                exitsArEventNftBenefitList.removeIf(data -> Objects.equals(data.getArEventNftBenefitId(), findExitsNftBenefit.getArEventNftBenefitId()));
                            }
                        }
                        //ar_event_nft_benefit 업데이트
                        try {
                            arEventService.saveArEventNftBenefit(ArEventNftBenefitEntity.updateOf(nftBenefit, arEventWinningEntity.getArEventWinningId()));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //중복제거된 DB에 남아있는 항목은 삭제되어야 할 항목이기 때문에 삭제 로직 처리
                    if (PredicateUtils.isNotNullList(exitsArEventNftBenefitList)) {
                        try {
                            exitsArEventNftBenefitList.forEach(benefit -> {
                                arEventService.deleteArEventNftBenefitById(benefit.getArEventNftBenefitId());
                            });
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                //ar_event_nft_benefit 정보 수정 끝

                //보관함 버튼 정보 수정 시작 (SS-20260)
                List<EventRepositoryButtonReqDto> repositoryButtonReqList = eventWinningDtoList.get(i).getRepositoryButtonInfo();
                if (PredicateUtils.isNotNullList(repositoryButtonReqList)) {
                    //수정할 당첨 텍스트 정보
                    List<ArEventRepositoryButtonEntity> updateEntityList = ModelMapperUtils.convertModelInList(repositoryButtonReqList, ArEventRepositoryButtonEntity.class);
                    //저장되어있는 당첨 텍스트 정보
                    List<ArEventRepositoryButtonEntity> savedEntityList = arEventService.findAllArEventRepositoryButtonByArWinningId(arEventWinningEntity.getArEventWinningId());
                    //당첨 텍스트 정보 저장, 삭제, 수정
                    try {
                        arEventService.updateArEventRepositoryButtonFromSodar(updateEntityList, savedEntityList, arEventWinningEntity.getArEventWinningId());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                //보관함 버튼 정보 수정 끝

                //DTWS-70 당첨정보의 OCB_POINT_SAVE 정보 수정
                if (!isStamp) {
                    //당첨 텍스트 정보 수정 시작 (SS-20260)
                    List<EventWinningTextReqDto> winningTextReqList = eventWinningDtoList.get(i).getWinningTextInfo();
                    if (PredicateUtils.isNotNullList(winningTextReqList)) {
                        //수정할 당첨 텍스트 정보
                        List<ArEventWinningTextEntity> updateEntityList = ModelMapperUtils.convertModelInList(winningTextReqList, ArEventWinningTextEntity.class);
                        //저장되어있는 당첨 텍스트 정보
                        List<ArEventWinningTextEntity> savedEntityList = arEventService.findAllArEventWinningTextByArWinningId(arEventWinningEntity.getArEventWinningId());
                        //당첨 텍스트 정보 저장, 삭제, 수정
                        try {
                            arEventService.updateArEventWinningTextFromSodar(updateEntityList, savedEntityList, arEventWinningEntity.getArEventWinningId());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //당첨 텍스트 정보 수정 끝

                    if (PredicateUtils.isNotNull(eventWinningDtoList.get(i).getOcbPointSaveInfo())) {
                        OcbPointSaveReqDto ocbPointSaveReqDto = eventWinningDtoList.get(i).getOcbPointSaveInfo();
                        ocbPointSaveReqDto.setArEventWinningId(arEventWinningEntity.getArEventWinningId());
                        try {
                            arEventService.saveOcbPointSave(eventIdx, ModelMapperUtils.convertModel(ocbPointSaveReqDto, OcbPointSaveEntity.class));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                i++;
            }
        }
    }

    /**
     * ar_event_winning 저장 공통로직 (AR, STAMP)
     * @param arEventWinningInfo
     * @param eventIdx
     * @param isStamp
     */
    @Transactional
    public void saveArEventWinning(List<EventWinningDto> arEventWinningInfo, int eventIdx, boolean isStamp) {
        if (PredicateUtils.isNotNull(arEventWinningInfo)) {
            int i = 0;
            List<ArEventWinningEntity> arEventWinningEntityList = ModelMapperUtils.convertModelInList(arEventWinningInfo, ArEventWinningEntity.class);
            for (ArEventWinningEntity arEventWinningEntity : arEventWinningEntityList) {
                if (isStamp) {
                    arEventWinningEntity.setStpId(eventIdx);
                } else {
                    arEventWinningEntity.setArEventId(eventIdx);
                }
                //당첨정보 저장
                try {
                    arEventService.saveEventWinning(arEventWinningEntity);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                /*
                 * NFT 혜택 정보 리스트 저장
                 */
                if (StringUtils.equals(arEventWinningEntity.getNftBenefitRegYn(), StringDefine.Y.name())) {
                    int arEventWinningId = 0;
                    if (isStamp) {
                        arEventWinningId = arEventService.findEventWinningEntityByStpId(eventIdx).getArEventWinningId();
                    } else {
                        arEventWinningId = arEventService.findEventWinningEntityByArEventId(eventIdx).getArEventWinningId();
                    }
                    try {
                        arEventService.saveAllArEventNftBenefit(arEventWinningId, arEventWinningInfo.get(i).getNftBenefitInfo());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                //NFT 임시 파일 업로드 파일명이 있을때 AR_EVENT_NFT_TOKEN_INFO or AR_EVENT_NFT_COUPON_INFO 업데이트
                if (PredicateUtils.isNotNull(arEventWinningEntity.getNftExcelUploadFileName())) {
                    //NFT 기본형일때
                    if (PredicateUtils.isEqualsStr(arEventWinningEntity.getWinningType(), WinningTypeDefine.NFT.code())) {
                        try {
                            //arEventService.updateArEventNftTokenInfo(eventIdx, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), isStamp);
                            if (isStamp) {
                                arEventService.saveArEventNftTokenFromTempTableBySeq(0, eventIdx, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                            } else {
                                arEventService.saveArEventNftTokenFromTempTableBySeq(eventIdx, 0, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            arEventService.deleteArEventNftCouponInfoTemp(arEventWinningEntity.getUploadFileSeqNum());
                        }
                    }

                    //NFT 쿠폰형일때
                    if (PredicateUtils.isEqualsStr(arEventWinningEntity.getWinningType(), WinningTypeDefine.NFT쿠폰.code())) {
                        try {
                            if (isStamp) {
                                arEventService.saveArEventNftCouponFromTempTableBySeq(0, eventIdx, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                            } else {
                                arEventService.saveArEventNftCouponFromTempTableBySeq(eventIdx, 0, arEventWinningEntity.getArEventWinningId(), arEventWinningEntity.getNftExcelUploadFileName(), arEventWinningEntity.getUploadFileSeqNum());
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        } finally {
                            arEventService.deleteArEventNftCouponInfoTemp(arEventWinningEntity.getUploadFileSeqNum());
                        }
                    }
                }

                int j = 0;
                //당첨버튼 정보 저장하기
                List<ArEventWinningButtonEntity> arEventWinningButtonEntityList = ModelMapperUtils.convertModelInList(arEventWinningInfo.get(i).getArEventWinningButtonInfo(), ArEventWinningButtonEntity.class);

                for (ArEventWinningButtonEntity buttonEntity : arEventWinningButtonEntityList) {

                    try {
                        if (PredicateUtils.isNotNull(arEventWinningEntity.getArEventWinningId())) {
                            buttonEntity.setArEventWinningId(arEventWinningEntity.getArEventWinningId());
                        }
                        arEventService.saveEventWinningButton(buttonEntity);

                        //DTWS-70 당첨정보 추가 버튼 정보가 있을때
                        if (PredicateUtils.isNotNullList(arEventWinningInfo.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonAddInfo())) {
                            List<ArEventWinningButtonAddEntity> winningButtonAddEntityList = ModelMapperUtils.convertModelInList(arEventWinningInfo.get(i).getArEventWinningButtonInfo().get(j).getArEventWinningButtonAddInfo(), ArEventWinningButtonAddEntity.class);
                            try {
                                arEventService.saveAllArEventWinningButtonAdd(buttonEntity.getArEventWinningButtonId(), winningButtonAddEntityList);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    j++;

                }

                //당첨정보 텍스트 정보 저장 (SS-20260)
                if (PredicateUtils.isNotNullList(arEventWinningInfo.get(i).getWinningTextInfo())) {
                    try {
                        arEventService.saveAllArEventWinningText(arEventWinningEntity.getArEventWinningId(), arEventWinningInfo.get(i).getWinningTextInfo());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                //보관함 버튼 정보 저장 (SS-20260)
                if (PredicateUtils.isNotNullList(arEventWinningInfo.get(i).getRepositoryButtonInfo())) {
                    try {
                        arEventService.saveAllArEventRepositoryButton(arEventWinningEntity.getArEventWinningId(), arEventWinningInfo.get(i).getRepositoryButtonInfo());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                //DTWS-70 당첨정보의 OCB_POINT_SAVE 정보 저장
                if (!isStamp) {
                    if (PredicateUtils.isNotNull(arEventWinningInfo.get(i).getOcbPointSaveInfo())) {
                        OcbPointSaveReqDto ocbPointSaveReqDto = arEventWinningInfo.get(i).getOcbPointSaveInfo();
                        ocbPointSaveReqDto.setArEventWinningId(arEventWinningEntity.getArEventWinningId());
                        try {
                            arEventService.saveOcbPointSave(eventIdx, ModelMapperUtils.convertModel(ocbPointSaveReqDto, OcbPointSaveEntity.class));
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
                i++;
            }
            //폐기되어야 할 NFT_TOKEN, NFT_COUPON 데이터 삭제
            arEventService.deleteArEventNftTokenInfoByTempToken();
        }
    }

    /**
     * ar_event_html 업데이트 공통로직 ( AR, STAMP )
     * @param arEventHtmlInfo
     * @param eventId
     * @param eventIdx
     * @param stpPanId
     * @param isStamp
     */
    @Transactional
    public void updateArEventHtml(List<EventHtmlDto> arEventHtmlInfo, String eventId, int eventIdx, int stpPanId, boolean isStamp) {
        if (PredicateUtils.isNullList(arEventHtmlInfo)) {
            // DTWS-567 - 소다에서 올라오는 AR_EVENT_HTML 목록이 없는 경우, 전체 삭제된 것이므로 삭제처리해야됨.
            //저장되어있는 AR_EVENT_HTML 목록 가져오기
            List<ArEventHtmlEntity> savedHtmlList = null;

            if (isStamp) {
                //저장되어있는 AR_EVENT_HTML 목록 가져오기
                savedHtmlList = arEventService.findArEventHtmlListByStpIdNoCache(eventIdx);
            } else {
                //저장되어있는 AR_EVENT_HTML 목록 가져오기
                savedHtmlList = arEventService.findArEventHtmlListByArEventId(eventIdx);
            }

            if (PredicateUtils.isNullList(savedHtmlList)) {
                return;
            }

            arEventService.deleteAllArEventHtml(savedHtmlList.stream().map(ArEventHtmlEntity::getEventHtmlId).collect(Collectors.toList()));

            return;
        }

        if (isStamp) {
            //저장되어있는 AR_EVENT_HTML 목록 가져오기
            List<ArEventHtmlEntity> savedHtmlList = arEventService.findArEventHtmlListByStpIdNoCache(eventIdx);
            //AR_EVENT_HTML 수정 시작
            for (EventHtmlDto htmlDto : arEventHtmlInfo) {
                try {
                    ArEventHtmlEntity returnHtmlEntity = arEventService.saveFirstEventHtmlByHtmlId(eventId, eventIdx, stpPanId, htmlDto, isStamp);
                    //저장이 되면 목록에서 삭제
                    savedHtmlList.removeIf(html -> Objects.equals(html.getEventHtmlId(), returnHtmlEntity.getEventHtmlId()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //삭제목록은 삭제
            if (PredicateUtils.isNotNullList(savedHtmlList)) {
                try {
                    arEventService.deleteAllArEventHtml(savedHtmlList.stream().map(ArEventHtmlEntity::getEventHtmlId).collect(Collectors.toList()));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //ar_event_html 수정 끝
        } else {
            //ar_event_html 안의 배너 정보 검증 시작
            List<EventHtmlDto> arEventHtmlInfoStream = arEventHtmlInfo.stream()
                    .filter(htmlInfo -> PredicateUtils.isEqualsStr(htmlInfo.getHtmlType(), "BUTTON"))
                    .filter(htmlInfo -> PredicateUtils.isEqualsStr(htmlInfo.getHtmlButtonType(), HtmlButtonTypeDefine.NFTREPO.name()))
                    .filter(htmlInfo -> PredicateUtils.isEqualsStr(htmlInfo.getHtmlButtonType(), HtmlButtonTypeDefine.CPREPO.name()))
                    .filter(htmlInfo -> PredicateUtils.isEqualsStr(htmlInfo.getHtmlButtonType(), HtmlButtonTypeDefine.PHOTOREPO.name()))
                    .filter(htmlInfo -> PredicateUtils.isGreaterThanZero(htmlInfo.getArEventNftBannerInfo().size()))
                    .collect(Collectors.toList());

            if (PredicateUtils.isNotNullList(arEventHtmlInfoStream)) {
                throw new BaseException(ResultCodeEnum.CUSTOM_EVENT_NFT_BANNER_REG_LIMIT.getDesc(), ResultCodeEnum.CUSTOM_EVENT_NFT_BANNER_REG_LIMIT);
            }
            //ar_event_html 안의 배너 정보 검증 끝

            //저장되어있는 AR_EVENT_HTML 목록 가져오기
            List<ArEventHtmlEntity> savedHtmlList = arEventService.findArEventHtmlListByArEventId(eventIdx);

            //AR_EVENT_HTML 수정 시작
            for (EventHtmlDto htmlDto : arEventHtmlInfo) {

                ArEventHtmlEntity returnHtmlEntity = arEventService.saveFirstEventHtmlByHtmlId(eventId, eventIdx, 0, htmlDto, isStamp);

                //저장이 되면 목록에서 삭제
                savedHtmlList.removeIf(html -> Objects.equals(html.getEventHtmlId(), returnHtmlEntity.getEventHtmlId()));

                if (PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlType(), "BUTTON")) {
                    //버튼 종류가 NFT보관함, 쿠폰보관함, AR포토함 일때만 ar_event_nft_banner 저장
                    if (PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.NFTREPO.name())
                            || PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.CPREPO.name())
                            || PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.PHOTOREPO.name())) {

                        //ar_event_nft_banner 정보 수정 시작
                        if (PredicateUtils.isNotNullList(htmlDto.getArEventNftBannerInfo())) {
                            //수정할 NFT 배너정보
                            List<ArEventNftBannerEntity> arEventNftBannerEntityList = ModelMapperUtils.convertModelInList(htmlDto.getArEventNftBannerInfo(), ArEventNftBannerEntity.class);
                            //저장되어있는 NFT 배너정보
                            List<ArEventNftBannerEntity> exitsArEventNftBannerList = arEventService.findAllArEventBannerByArEventIdNoCache(eventIdx);

                            if (PredicateUtils.isNullList(arEventNftBannerEntityList)) {
                                log.debug("======================= arEventNftBannerEntityList size 0 ================================");
                            }

                            if (PredicateUtils.isNotNullList(arEventNftBannerEntityList)) {

                                for (ArEventNftBannerEntity nftBanner : arEventNftBannerEntityList) {

                                    if (PredicateUtils.isNullList(exitsArEventNftBannerList)) {
                                        log.debug("======================= exitsArEventNftBannerList size 0 ================================");
                                    }
                                    //저장되어있는 NFT 배너 정보가 있으면
                                    if (PredicateUtils.isNotNullList(exitsArEventNftBannerList)) {
                                        if (PredicateUtils.isNotNull(nftBanner.getId())) {
                                            log.info("nftBanner.getId() >>> " + nftBanner.getId());
                                            //저장되어 있는 NFT 배너정보가 있는지 DB 조회
                                            ArEventNftBannerEntity findExitsNftBanner = arEventService.findArEventNftBannerById(nftBanner.getId());
                                            if (PredicateUtils.isNull(findExitsNftBanner)) {
                                                log.info("======================= findExitsNftBanner is null ================================");
                                            }
                                            if (PredicateUtils.isNotNull(findExitsNftBanner)) {
                                                log.info("findExitsNftBanner >> " + findExitsNftBanner.getId());
                                                //DB 조회 항목이 있으면 삭제
                                                exitsArEventNftBannerList.removeIf(
                                                        data -> Objects.equals(data.getId(), findExitsNftBanner.getId())
                                                );
                                                //수정
                                                arEventService.saveArEventNftBanner(ArEventNftBannerEntity.updateOf(findExitsNftBanner.getId(), eventIdx, returnHtmlEntity.getEventHtmlId(), nftBanner.getBannerImgUrl(), nftBanner.getBannerTargetUrl(), nftBanner.getBannerSort(), findExitsNftBanner.getCreatedDate()));
                                            }
                                        }
                                    } else {
                                        arEventService.saveArEventNftBanner(ArEventNftBannerEntity.saveOf(eventIdx, returnHtmlEntity.getEventHtmlId(), nftBanner.getBannerImgUrl(), nftBanner.getBannerTargetUrl(), nftBanner.getBannerSort()));
                                    }
                                    //ar_event_nft_banner 업데이트
                                }

                                //중복제거된 DB에 남아있는 항목은 삭제되어야 할 항목이기 때문에 삭제 로직 처리
                                if (PredicateUtils.isNotNullList(exitsArEventNftBannerList)) {
                                    log.info("exitsArEventNftBannerList size > 0 result delete");
                                    exitsArEventNftBannerList.forEach(banner -> {
                                        arEventService.deleteArEventNftBannerById(banner.getId());
                                    });
                                }
                            }
                        }
                        //ar_event_nft_banner 정보 수정 끝
                    }
                    //DTWS-70 AR_EVENT_DEVICE_GPS 수정
                    if (PredicateUtils.isEqualsStr(returnHtmlEntity.getHtmlButtonType(), HtmlButtonTypeDefine.PHOTOREPO.name())) {
                        if (PredicateUtils.isNotNullList(htmlDto.getArEventDeviceGpsInfo())) {
                            arEventService.saveAllArEventDeviceGps(returnHtmlEntity.getEventHtmlId(), ModelMapperUtils.convertModelInList(htmlDto.getArEventDeviceGpsInfo(), ArEventDeviceGpsEntity.class));
                        }
                    }
                }
            }
            //삭제목록은 삭제
            if (PredicateUtils.isNotNullList(savedHtmlList)) {
                arEventService.deleteAllArEventHtml(savedHtmlList.stream().map(ArEventHtmlEntity::getEventHtmlId).collect(Collectors.toList()));
            }
            //ar_event_html 수정 끝
        }
    }

    /**
     * 참여코드 저장 공통 로직 ( AR, STAMP )
     * @param attendCodeExcelFile
     * @param eventId
     * @param stpId
     * @param attendCodeRegType
     * @param strDigit
     * @param codeCnt
     */
    @Transactional
    public void saveAllAttendCode(MultipartFile attendCodeExcelFile, String eventId, int stpId, String attendCodeRegType, int strDigit, int codeCnt) {
        //스탬프일때
        if (stpId > 0) {
            List<StampEventGateCodeModel> stampEventGateCodeModelList = new ArrayList<>();
            //참여코드 자동생성
            if (PredicateUtils.isEqualsStr("AUTO", attendCodeRegType)) {
                List<String> attendCodeList = EventUtils.getRandomAttendCode(strDigit, codeCnt);
                for (String attendCode : attendCodeList) {
                    StampEventGateCodeModel gateCodeModel = new StampEventGateCodeModel();
                    gateCodeModel.setStpId(stpId);
                    gateCodeModel.setAttendCode(attendCode);

                    stampEventGateCodeModelList.add(gateCodeModel);
                }
            } else {
                //참여코드 엑셀업로드 생성
                if (PredicateUtils.isNotNull(attendCodeExcelFile)) {
                    //엑셀파일에서 참여코드 데이터 추출
                    List<Map<String, Object>> attendCodeList = excelService.extractionAttendCodeByExcelFile(attendCodeExcelFile, "ATTEND");
                    try {
                        stampSodarService.deleteStampEventGateCodeByStpId(stpId);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }

                    //신규 저장
                    attendCodeList.forEach(attendCodeMap -> {
                        StampEventGateCodeModel gateCode = new StampEventGateCodeModel();
                        gateCode.setStpId(stpId);
                        gateCode.setAttendCode(String.valueOf(attendCodeMap.get("A")));

                        stampEventGateCodeModelList.add(gateCode);
                    });
                }
            }
            //폐기되어야 할 스탬프 COUPON 데이터 삭제
            arEventService.deleteStampEventCouponInfoByTempCoupon();

            if (PredicateUtils.isGreaterThanZero(stampEventGateCodeModelList.size())) {
                try {
                    batchService.saveBatchStampEventGateCode(stampEventGateCodeModelList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else {
        //AR일때
            List<ArEventGateCodeEntity> arEventGateCodeEntityList = new ArrayList<>();
            if (PredicateUtils.isEqualsStr("AUTO", attendCodeRegType)) {
                //참여코드 자동생성
                List<String> attendCodeList = EventUtils.getRandomAttendCode(strDigit, codeCnt);
                for (String attendCode : attendCodeList) {
                    ArEventGateCodeEntity gateCodeEntity = new ArEventGateCodeEntity();
                    gateCodeEntity.setEventId(eventId);
                    gateCodeEntity.setAttendCode(attendCode);
                    gateCodeEntity.setUseYn(false);
                    gateCodeEntity.setUsedCount(0);

                    arEventGateCodeEntityList.add(gateCodeEntity);
                }

            } else {
                //참여코드 엑셀파일 추출 후 저장하기 시작
                if (PredicateUtils.isNotNull(attendCodeExcelFile)) {
                    //엑셀파일에서 참여코드 데이터 추출
                    List<Map<String, Object>> attendCodeMapList = excelService.extractionAttendCodeByExcelFile(attendCodeExcelFile, "ATTEND");
                    for (Map<String, Object> map : attendCodeMapList) {
                        ArEventGateCodeEntity gateCodeEntity = new ArEventGateCodeEntity();
                        gateCodeEntity.setEventId(eventId);
                        gateCodeEntity.setAttendCode(String.valueOf(map.get("A")));
                        gateCodeEntity.setUseYn(false);
                        gateCodeEntity.setUsedCount(0);

                        arEventGateCodeEntityList.add(gateCodeEntity);
                    }
                }
            }
            if (PredicateUtils.isGreaterThanZero(arEventGateCodeEntityList.size())) {
                try {
                    batchService.saveBatchArEventGateCode(arEventGateCodeEntityList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
