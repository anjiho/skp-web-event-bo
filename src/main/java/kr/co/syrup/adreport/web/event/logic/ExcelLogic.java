package kr.co.syrup.adreport.web.event.logic;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.model.StampEventGateCodeModel;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampSodarService;
import kr.co.syrup.adreport.web.event.define.CommonSettingsDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.dto.response.AttendCodeValidateResDto;
import kr.co.syrup.adreport.web.event.dto.response.NftTokenIdValidateResDto;
import kr.co.syrup.adreport.web.event.entity.*;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.mybatis.vo.ArEventJoinEventBaseVO;
import kr.co.syrup.adreport.web.event.service.ArEventFrontService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.BatchService;
import kr.co.syrup.adreport.web.event.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Repository
public class ExcelLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    private static final int ATTEND_CODE_LIMIT_COUNT = 100000;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private ArEventFrontService arEventFrontService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private StampSodarService stampSodarService;

    @Autowired
    private StampFrontService stampFrontService;

    /**
     * 참여코드 엑셀파일 중복여부 검증하기
     * @param excelFile
     * @param traceNo
     * @return
     */
    public ApiResultObjectDto isDuplicateAttendCodeLogic(MultipartFile excelFile, String traceNo) {
        int resultCode = httpSuccessCode;
        //참여코드 중복 확인
        AttendCodeValidateResDto resDto = excelService.isValidationAttendCodeByExcelFile(excelFile);

        return new ApiResultObjectDto(resDto, resultCode, traceNo);
    }

    /**
     * 참여코드 저장하기
     * @param jsonStr
     * @param excelFile
     * @param commonSettingsKey
     * @return
     */
    public ApiResultObjectDto validateAttendCodeByAdditionalSaveLogic(String jsonStr, MultipartFile excelFile, String commonSettingsKey) {
        int resultCode = HttpStatus.OK.value();

        String eventId = GsonUtils.parseStringJsonStr(jsonStr, "eventId");
        boolean isStamp = StringTools.containsIgnoreCase(eventId, "S");
        long addedCnt = 0l;
        //총개수 선언
        int totalCount = 0;
        //새로 입력할 코드 개수 선언
        int newAttendCodeCount = 0;

        boolean isDuplicate = false;

        long duplicateCodeCount = 0;

        int attendCodeCount;

        if (!isStamp) {
            addedCnt = arEventService.countArEventGateCodeListByEventId(eventId);
        } else {
            addedCnt = stampFrontService.countStampEventGateCodeByEventId(eventId);
        }

        //엑셀파일 추가 방식이 아닌 추가
        if (PredicateUtils.isNull(excelFile)) {
            //추가로 입력할 개수
            attendCodeCount = GsonUtils.parseIntJsonStr(jsonStr, "attendCodeCount");
            int strDigit = 0;
            ArEventEntity arEventEntity = new ArEventEntity();
            StampEventMainModel stampEventMain = new StampEventMainModel();
            //이벤트에 등록 되어있는 코드 자릿수 정보 가져오기
            if (!isStamp) {
                arEventEntity = arEventService.findArEventByEventId(eventId);
                //자릿수
                strDigit = arEventEntity.getAttendCodeDigit();
            } else {
                stampEventMain = stampSodarService.findStampEventMainByEventId(eventId);
                //자릿수
                if (PredicateUtils.isNotNull(stampEventMain.getStpAttendCodeDigit())) {
                    strDigit = stampEventMain.getStpAttendCodeDigit();
                }
            }
            //자리수가 0이면 에러처리
            if (PredicateUtils.isEqualZero(strDigit)) {
                throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_GET_DATA_IS_NULL.getDesc(), ResultCodeEnum.CUSTOM_ERROR_GET_DATA_IS_NULL);
            }

            //기존개수 + 신규 입력개수가 10만개가 넘었을때 에러처리
            if ((attendCodeCount + (int)addedCnt) > ATTEND_CODE_LIMIT_COUNT) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_ATTEND_TOTAL_LIMIT_COUNT.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                AttendCodeValidateResDto resDto = new AttendCodeValidateResDto().builder()
                        .duplicateYn(true)   //중복여부
                        .totalCount(totalCount) //총개수
                        .attendCodeCount(newAttendCodeCount)   //새로 등록된 코드 개수
                        .duplicateCodeCount(duplicateCodeCount) //중복된 참여코드 개수
                        .build();

                return ApiResultObjectDto.builder().resultCode(resultCode).result(resDto).build();
            }
            //기존개수 + 신규 입력개수가 10만개가 안넘었을때 정상로직
            if ((attendCodeCount + addedCnt) <= ATTEND_CODE_LIMIT_COUNT) {
                //비동기 로직
                CompletableFuture.supplyAsync(()-> this.completableFutureAttendCodeByAdditionalSaveLogic(eventId,  null, attendCodeCount, isStamp, commonSettingsKey));

                AttendCodeValidateResDto resDto = new AttendCodeValidateResDto().builder()
                        .duplicateYn(false)   //중복여부
                        .totalCount((attendCodeCount + (int)addedCnt)) //총개수
                        .attendCodeCount(attendCodeCount)   //새로 등록된 코드 개수
                        .duplicateCodeCount(duplicateCodeCount) //중복된 참여코드 개수
                        .build();

                return ApiResultObjectDto.builder().resultCode(resultCode).result(resDto).build();
            }
        } else {
            attendCodeCount = 0;
        }

        //엑셀파일 추가 방식
        if (PredicateUtils.isNotNull(excelFile)) {

            List<String> newAttendCodeList = new ArrayList<>();
            //엑셀 참여코드 추출
            List<Map<String, Object>> excelAttendCodeList = excelService.extractionAttendCodeByExcelFile(excelFile, "ATTEND");

            //String 배열로 만들기
            excelAttendCodeList.forEach(excel -> {
                newAttendCodeList.add(excel.get("A").toString());
            });

            //기존개수 + 신규 입력개수가 10만개가 넘었을때 에러처리
            if (PredicateUtils.isGreaterThan((newAttendCodeList.size() + (int)addedCnt), ATTEND_CODE_LIMIT_COUNT)) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_ATTEND_TOTAL_LIMIT_COUNT.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                totalCount = (int)addedCnt;
            } else {
                LinkedList<String>tableAttendCodeList = stampFrontService.findStampEventGateCodeListByEventId(eventId);
                //두개의 String 배열 조인
                Iterable<String> joinedIterable = Iterables.unmodifiableIterable(
                        Iterables.concat(tableAttendCodeList, newAttendCodeList)
                );
                Collection<String> joined = Lists.newArrayList(joinedIterable);

                //중복제거된 개수
                long distinctCount = joined.stream().distinct().count();
                //중복되었는지 조건
                if (PredicateUtils.isGreaterThan(excelAttendCodeList.size() + (int)addedCnt, (int)distinctCount)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_DUPLICATE_ATTEND_CODE.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                    isDuplicate = true;
                    newAttendCodeCount = 0;
                    duplicateCodeCount = joined.size() - distinctCount;
                    totalCount = (int)addedCnt;

                } else {
                    CompletableFuture.supplyAsync(()-> this.completableFutureAttendCodeByAdditionalSaveLogic(eventId, newAttendCodeList, 0, isStamp, commonSettingsKey));

                    totalCount = excelAttendCodeList.size() + (int)addedCnt;
                    newAttendCodeCount = excelAttendCodeList.size();
                }
            }
        }
        //제한테이블 상태 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, "2");

        AttendCodeValidateResDto resDto = new AttendCodeValidateResDto().builder()
                .duplicateYn(isDuplicate)   //중복여부
                .totalCount(totalCount) //총개수
                .attendCodeCount(newAttendCodeCount)   //새로 등록된 코드 개수
                .duplicateCodeCount(duplicateCodeCount) //중복된 참여코드 개수
                .build();

        return new ApiResultObjectDto().builder()
                .resultCode(resultCode).result(resDto).build();
    }


    /**
     * 기존에 있던 참여코드 리스트와 엑셀 파일 참여코드 리스트 중복 여부 검증후 신규 코드 값 입력
     */
    public CompletableFuture<ApiResultObjectDto> completableFutureAttendCodeByAdditionalSaveLogic(String eventId, List<String> newAttendCodeList, int attendCodeCount, boolean isStamp, String commonSettingsKey) {
        //제한테이블 상태 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, "1");
        int strDigit = 0;

        List<ArEventGateCodeEntity>saveArEventGateCodeEntityList = new ArrayList<>();
        List<StampEventGateCodeModel>saveStampEventGateCodeModelList = new ArrayList<>();
        LinkedList<String>tableAttendCodeList = new LinkedList<>();

        ArEventEntity arEventEntity = new ArEventEntity();
        StampEventMainModel stampEventMain = new StampEventMainModel();

        if (!isStamp) {
            arEventEntity = arEventService.findArEventByEventId(eventId);
            //자릿수
            strDigit = arEventEntity.getAttendCodeDigit();
            tableAttendCodeList = arEventService.findArEventGateCodeListByEventId(eventId);
        } else {
            stampEventMain = stampSodarService.findStampEventMainByEventId(eventId);
            //자릿수
            if (PredicateUtils.isNotNull(stampEventMain.getStpAttendCodeDigit())) {
                strDigit = stampEventMain.getStpAttendCodeDigit();
            }
            tableAttendCodeList = stampFrontService.findStampEventGateCodeListByEventId(eventId);
        }

        //기존 저장된 참여코드를 검증하여 중복방지 후 저장할 참여코드 목록 생성
        List<String>attendCodeList = EventUtils.getRandomAttendCode(strDigit, attendCodeCount, tableAttendCodeList);

        //자동생성 일때
        if (PredicateUtils.isNullList(newAttendCodeList)) {
            //AR이벤트일때
            if (!isStamp) {
                for (int i = 0; i < attendCodeList.size(); i++) {
                    ArEventGateCodeEntity gateCodeEntity = new ArEventGateCodeEntity();
                    gateCodeEntity.setEventId(eventId);
                    gateCodeEntity.setAttendCode(attendCodeList.get(i));
                    gateCodeEntity.setUseYn(false);
                    gateCodeEntity.setUsedCount(0);
                    saveArEventGateCodeEntityList.add(gateCodeEntity);
                }
                // 코드 개수 업데이트
                ArEventEntity updateArEventEntity = ArEventEntity.updateOf(arEventEntity, (tableAttendCodeList.size() + attendCodeCount));
                try {
                    arEventService.saveEvent(updateArEventEntity);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
            //스탬프이벤트일때
                int stpId = stampEventMain.getStpId();
                try {
                    // 코드 개수 업데이트
                    stampSodarService.updateStpAttendCodeCountFromStampEventMain(stpId, (tableAttendCodeList.size() + attendCodeCount));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                for (int i = 0; i < attendCodeList.size(); i++) {
                    StampEventGateCodeModel gateCodeModel = new StampEventGateCodeModel();
                    gateCodeModel.setStpId(stpId);
                    gateCodeModel.setAttendCode(attendCodeList.get(i));
                    saveStampEventGateCodeModelList.add(gateCodeModel);
                }
            }
        } else {
            //엑셀파일 추가 방식
            if (!isStamp) {
                for (int i = 0; i < newAttendCodeList.size(); i++) {
                    ArEventGateCodeEntity gateCodeEntity = new ArEventGateCodeEntity();
                    gateCodeEntity.setEventId(eventId);
                    gateCodeEntity.setAttendCode(newAttendCodeList.get(i));
                    gateCodeEntity.setUseYn(false);
                    gateCodeEntity.setUsedCount(0);
                    saveArEventGateCodeEntityList.add(gateCodeEntity);
                }
            } else {
                StampEventMainModel stampEventMainModel = stampSodarService.findStampEventMainByEventId(eventId);
                for (int i = 0; i < newAttendCodeList.size(); i++) {
                    StampEventGateCodeModel gateCodeModel = new StampEventGateCodeModel();
                    gateCodeModel.setStpId(stampEventMainModel.getStpId());
                    gateCodeModel.setAttendCode(newAttendCodeList.get(i));
                    saveStampEventGateCodeModelList.add(gateCodeModel);
                }
            }
        }

        //저장할 참여코드가 있으면 - ar,서베이고 이벤트
        if (PredicateUtils.isGreaterThanZero(saveArEventGateCodeEntityList.size())) {
            //추가 저장하기
            try {
                batchService.saveBatchArEventGateCode(saveArEventGateCodeEntityList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                arEventService.updateCommonSettings(commonSettingsKey, "2");
            }
        }
        //저장할 참여코드가 있으면 - 스탬프 이벤트
        if (PredicateUtils.isGreaterThanZero(saveStampEventGateCodeModelList.size())) {
            try {
                batchService.saveBatchStampEventGateCode(saveStampEventGateCodeModelList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                arEventService.updateCommonSettings(commonSettingsKey, "2");
            }
        }
        return null;
    }

    /**
     * NFT 토큰 정보 엑셀 파일 신규 저장하기
     * @param file
     * @param jsonStr {"winningType":"NFT" or "NFTCP"}
     * @return
     */
    public ApiResultObjectDto saveTemporaryNftIdListByExcelLogic(MultipartFile file, String jsonStr) {
        return this.addNftIdListByExcelLogicNew(file, jsonStr, true);
    }

    /**
     * NFT 토큰 정보 엑셀 파일 추가 저장하기
     * @param excelFile
     * @param jsonStr {"winningType":"NFT" or "NFTCP", "arEventWinningId":680}
     * @return
     */
    public ApiResultObjectDto addNftIdListByExcelLogicNew(MultipartFile excelFile, String jsonStr, Boolean isNew) {
        int resultCode = httpSuccessCode;

        Map<String, Object> resultMap = new HashMap<>();
        //총개수 선언
        int totalCount = 0;
        //새로 입력할 nft 개수 선언
        int newNftTokenCount = 0;

        boolean isDuplicate = false;

        int duplicateNftTokenCount = 0;

        int temporaryCount = 0;

        String uploadNftFileName = "";

        int arEventWinningId = 0;

        String winningType = WinningTypeDefine.NFT.code();

        String firstFileName = "token_info";

        boolean isPossibleUpdateEvent = false;

        boolean isStamp = false;

        Long uploadSequenceNum = 0l;

        if (PredicateUtils.isNotNull(excelFile)) {

            List<String> exitsNftTokenList = new ArrayList<>();
            List<String> exitsNftCouponList = new ArrayList<>();

            //저장되어있는 NFT 토큰 정보 가져오기
            List<ArEventNftTokenInfoEntity> tableNftTokenList = new ArrayList<>();
            List<ArEventNftCouponInfoEntity> tableNftCouponList = new ArrayList<>();

            winningType = GsonUtils.parseStringJsonStr(jsonStr, "winningType");
//            Boolean isStamp = GsonUtils.parseBooleanJsonStr(jsonStr, "isStamp");


            if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT쿠폰.code())) {
                firstFileName = "coupon_info";
            }

            //수정
            if (!isNew) {
                arEventWinningId = GsonUtils.parseIntJsonStr(jsonStr, "arEventWinningId");

                ArEventWinningEntity winningEntity = arEventService.findByArEventWinningById(arEventWinningId);
                //AR_EVENT 정보가 없으면 에러처리
                if (PredicateUtils.isNull(winningEntity)) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }

                if (PredicateUtils.isNotNull(winningEntity.getStpId())) {
                    isStamp = true;
                }

                //AR_EVENT 정보가 있으면 엑셀 NFT 토큰값 저장
                if (PredicateUtils.isNotNull(winningEntity)) {
                    String contractStatus = "00";

                    //스탬프 이벤트가 아닐때
                    if (!isStamp) {
                        ArEventJoinEventBaseVO vo = arEventService.findArEventJoinEventBaseByArEventId(winningEntity.getArEventId());
                        if (PredicateUtils.isNotNull(vo)) {
                            contractStatus = vo.getContractStatus();
                        }
                    } else {
                        //스탬프 이벤트일때
                        WebEventBaseEntity webEventBase = stampSodarService.findWebEventBaseByStpId(winningEntity.getStpId());
                        contractStatus = webEventBase.getContractStatus();
                    }

                    isPossibleUpdateEvent = arEventFrontService.isPossibleUpdateEvent(contractStatus);

                    //승인대기, 서비스 예정일때 winningId 기준 기존 DB 정보 삭제 후 엑셀 내용 등록
                    if (isPossibleUpdateEvent) {
                        //NFT 일때
                        if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT.code())) {
                            //수정할려고 하는 타입이 NFT 토큰이고 DB의 기존 타입이 같으면
                            if (PredicateUtils.isEqualsStr(winningType, winningEntity.getWinningType())) {
                                // winningId 기준 기존 DB 정보 삭제
                                arEventService.deleteArEventNftTokenInfoByArEventWinningId(arEventWinningId);
                            } else {
                                //NFT 쿠폰일때
                                // winningId 기준 기존 DB 정보 삭제
                                arEventService.deleteArEventNftCouponInfoByArEventWinningId(arEventWinningId);
                            }
                        } else {
                            //NFT 쿠폰일때
                            if (PredicateUtils.isEqualsStr(winningType, winningEntity.getWinningType())) {
                                // winningId 기준 기존 DB 정보 삭제
                                arEventService.deleteArEventNftCouponInfoByArEventWinningId(arEventWinningId);
                            } else {
                                arEventService.deleteArEventNftTokenInfoByArEventWinningId(arEventWinningId);
                            }
                        }

                    } else {
                        //이벤트 수정이 불가능한 상태일때 상품 타입이 변경되서 들어오면 "이벤트 수정이 불가능한 상태" 에러를 던진다.
                        if (!PredicateUtils.isEqualsStr(winningType, winningEntity.getWinningType())) {
                            resultCode = Integer.parseInt(ResultCodeEnum.CUSTOM_ERROR_IMPOSSIBLE_EVENT_MODIFY.getCode());
                            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_IMPOSSIBLE_EVENT_MODIFY.getDesc(), ResultCodeEnum.CUSTOM_ERROR_IMPOSSIBLE_EVENT_MODIFY);
                        } else {
                            //NFT 일때
                            if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT.code())) {
                                //저장되어있는 NFT 토큰 정보 가져오기
                                tableNftTokenList = arEventService.findAllArEventNftTokenByArEventWinningId(arEventWinningId);
                            } else {
                                //저장되어있는 NFT 쿠폰 정보 가져오기
                                tableNftCouponList = arEventService.findAllArEventNftCouponByArEventWinningId(arEventWinningId);
                            }
                        }
                    }
                }
            }

            //NFT 일때
            if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT.code())) {
                //String 배열로 만들기
                tableNftTokenList.forEach(nftToken -> {
                    exitsNftTokenList.add(nftToken.getNftTokenId());
                });
            } else {
                //NFT 쿠폰 일때
                tableNftCouponList.forEach(nftCoupon -> {
                    exitsNftTokenList.add(nftCoupon.getNftCouponId());
                });
            }

            List<String> newNftTokenList = new ArrayList<>();
            //엑셀파일 NFT 토큰 추출
            List<Map<String, Object>> excelNftTokenList = excelService.extractionAttendCodeByExcelFile(excelFile, WinningTypeDefine.NFT.code());
            boolean isDuplicatedExcelContentList = excelNftTokenList.stream()
                    .distinct()
                    .count() != excelNftTokenList.size();

            //개수가 0이면 에러처리
            if (excelNftTokenList.size() == 0) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_GET_DATA_IS_NULL.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
            } else {
                //엑셀 데이터가 중복 되었을때
                if (isDuplicatedExcelContentList) {
                    resultCode = ErrorCodeDefine.CUSTOM_ERROR_DUPLICATE_NFT_TOKEN.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                    isDuplicate = true;
                    temporaryCount = excelNftTokenList.size();
                    duplicateNftTokenCount = excelNftTokenList.size() - ((int) excelNftTokenList.stream().distinct().count());

                }

                if (!isDuplicatedExcelContentList) {
                    //String 배열로 만들기
                    excelNftTokenList.forEach(nftToken -> {
                        newNftTokenList.add(nftToken.get("B").toString());
                    });

                    //두개의 String 배열 조인
                    Iterable<String> joinedIterable = Iterables.unmodifiableIterable(
                            Iterables.concat(exitsNftTokenList, newNftTokenList)
                    );

                    Collection<String> joinedCollection = Lists.newArrayList(joinedIterable);
                    //두개의 총개수
                    totalCount = exitsNftTokenList.size() + newNftTokenList.size();
                    //중복제거된 개수
                    long distinctCount = joinedCollection.stream().distinct().count();

                    temporaryCount = newNftTokenList.size();

                    //중복제거된 개수가 0보가 크면 NFT 토큰 중복 에러 처리
                    if (PredicateUtils.isGreaterThan(totalCount, (int) distinctCount)) {
                        resultCode = ErrorCodeDefine.CUSTOM_ERROR_DUPLICATE_NFT_TOKEN.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                        isDuplicate = true;
                        duplicateNftTokenCount = (int) (totalCount - distinctCount);
                        totalCount = (int) (totalCount - distinctCount);

                    } else {
                        //파일명 변경
                        int filePos = excelFile.getOriginalFilename().lastIndexOf(".");
                        String fileExtension = excelFile.getOriginalFilename().substring(filePos + 1);
                        //전달해줄 파일명 생성
                        String finalFileName = firstFileName + "_" + DateUtils.returnNowDateByYyyymmddhhmmssMilliSecond() + "." + fileExtension;
                        uploadNftFileName = finalFileName;

                        //신규
                        if (isNew) {
                            List<String> couponList = new ArrayList<>();
                            for (String nftCoupon : newNftTokenList) {
                                couponList.add(nftCoupon);
                            }
                            int couponTempSeq = arEventService.findWebEventSequence("coupon_info_temp_seq");
                            try {
                                batchService.saveBulkArEventNftCouponTemp(couponTempSeq, couponList);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            } finally {
                                uploadSequenceNum = (long) couponTempSeq;
                            }
                        }

                        //수정
                        if (!isNew) {
                            //중복제거된 개수가 0개면 엑셀파일의 NFT 토큰값을 저장한다
                            ArEventWinningEntity winningEntity = arEventService.findByArEventWinningById(arEventWinningId);
                            //AR_EVENT 정보가 없으면 에러처리
                            if (PredicateUtils.isNull(winningEntity)) {
                                resultCode = ErrorCodeDefine.CUSTOM_ERROR_AR_EVENT_INFO_NULL.code();
                                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                            }

                            //AR_EVENT 정보가 있으면 엑셀 NFT 토큰값 저장
                            if (PredicateUtils.isNotNull(winningEntity)) {
                                newNftTokenCount = newNftTokenList.size();

                                //승인대기, 서비스 예정일때 winningId 기준 기존 DB 정보 삭제 후 엑셀 내용 등록
                                if (isPossibleUpdateEvent) {
                                    //NFT 일때
                                    if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT.code())) {
                                        // winningId 기준 기존 DB 정보 삭제
                                        arEventService.deleteArEventNftTokenInfoByArEventWinningId(arEventWinningId);
                                    } else {
                                        //NFT 쿠폰일때
                                        // winningId 기준 기존 DB 정보 삭제
                                        arEventService.deleteArEventNftCouponInfoByArEventWinningId(arEventWinningId);
                                    }
                                }

                                //NFT 일때
                                if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT.code())) {
                                    List<ArEventNftTokenInfoEntity> addTokenInfoEntityList = new ArrayList<>();
                                    for (String nftToken : newNftTokenList) {
                                        addTokenInfoEntityList.add(ArEventNftTokenInfoEntity.addExcelUploadOf(winningEntity.getArEventId(), arEventWinningId, nftToken, uploadNftFileName));
                                    }
                                    arEventService.saveAllArEventNftTokenInfo(addTokenInfoEntityList);
                                } else {
                                    //쿠폰일때
                                    List<ArEventNftCouponInfoEntity> addCouponInfoEntityList = new ArrayList<>();
                                    for (String nftCoupon : newNftTokenList) {
                                        int winningStandId = 0;
                                        if (!isStamp) {
                                            winningStandId = winningEntity.getArEventId();
                                        } else {
                                            winningStandId = winningEntity.getStpId();
                                        }
                                        addCouponInfoEntityList.add(ArEventNftCouponInfoEntity.addExcelUploadOf(winningStandId, arEventWinningId, nftCoupon, uploadNftFileName, isStamp));
                                    }
                                    arEventService.saveAllArEventNftCouponInfo(addCouponInfoEntityList);
                                }
                            }
                            resultMap.put("arEventWinningId", arEventWinningId);
                        }
                    }
                }
                //수정일때
                if (!isNew) {
                    //엑셀 파일의 신규 값이 있을때
                    if (PredicateUtils.isGreaterThanZero(newNftTokenCount)) {
                        if (PredicateUtils.isEqualsStr(winningType, WinningTypeDefine.NFT.code())) {
                            totalCount = arEventService.countArEventNftTokenByArEventWinningId(arEventWinningId);
                        } else {
                            totalCount = arEventService.countArEventNftCouponByArEventWinningId(arEventWinningId);
                        }
                    } else {
                        totalCount = temporaryCount;
                    }
                }
                //신규일때
                if (isNew) {
                    totalCount = temporaryCount;
                }
            }
        }
        NftTokenIdValidateResDto resDto = new NftTokenIdValidateResDto().builder()
                .duplicateYn(isDuplicate)   //중복여부
                .totalCount(totalCount) //총개수
                .addNftTokenCount(newNftTokenCount)   //새로 등록된 코드 개수
                .duplicateNftTokenCount(duplicateNftTokenCount) //중복된 참여코드 개수
                .uploadFileName(uploadNftFileName)
                .uploadFileSeqNum(uploadSequenceNum)
                .build();

        return new ApiResultObjectDto().builder()
                .result(resDto)
                .resultCode(resultCode)
                .build();
    }

}
