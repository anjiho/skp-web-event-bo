package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import kr.co.syrup.adreport.web.event.entity.XtransReceiverEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.CommonLogPvMapperVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.EventGiveAwayDeliveryButtonAddMapperVO;
import kr.co.syrup.adreport.web.event.mybatis.vo.GiveAwayDeliveryListMapperVO;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.StaticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class XtransLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Value("${xtrans.sftp.event-id}")
    private String xtransSftpEventId;

    @Value("${xtrans.sftp.host}")
    private String xtransSftpHost;

    @Value("${xtrans.sftp.user}")
    private String xtransSftpUser;

    @Value("${xtrans.sftp.pass}")
    private String xtransSftpPass;

    @Value("${xtrans.sftp.port}")
    private int xtransSftpPort;

    @Value("${xtrans.sftp.default-path}")
    private String xtransDefaultPath;

    @Autowired
    private StaticsService staticsService;

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private AES256Utils aes256Utils;

    /**
     * x-trans 경품 당첨 정보 전달 로직
     * @param eventId
     * @return
     */
    public CompletableFuture<Map<String, Object>> sendSftpGiveAwayExcelLogic(String eventId, int limitCount, String commonSettingsKey) {
        Map<String, Object> resultMap = new HashMap<>();

        //이벤트 계약 정보 가져오기
        WebEventBaseEntity webEventBaseEntity = arEventService.findEventBase(eventId);
        //총개수
        int totalCount = staticsService.countGiveAwayDelivery(eventId);

        if (totalCount == 0) {
            resultMap.put("status", "result is 0");
            return CompletableFuture.completedFuture(resultMap);
        }

        String commonValue = "1";
        arEventService.updateCommonSettings(commonSettingsKey, commonValue);

        //루프 개수
        int loopCount = totalCount / limitCount;
        if (loopCount == 0) {
            if (totalCount > 0) {
                loopCount = 1;
            }
        }

        //엑셀표 작성 시작
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;
        // Header 필드
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("상품명");
        cell = row.createCell(1);
        cell.setCellValue("당첨자 이름");
        cell = row.createCell(2);
        cell.setCellValue("핸드폰 번호");
        cell = row.createCell(3);
        cell.setCellValue("우편번호");
        cell = row.createCell(4);
        cell.setCellValue("주소");
        cell = row.createCell(5);
        cell.setCellValue("주소상세");
        cell = row.createCell(6);
        cell.setCellValue("생년월일");
        cell = row.createCell(7);
        cell.setCellValue("당첨일");
        cell = row.createCell(8);
        cell.setCellValue("쿠폰번호");
        cell = row.createCell(9);
        cell.setCellValue("쿠폰사용");
        cell = row.createCell(10);
        cell.setCellValue("쿠폰사용일자");

        //추가 항목 제목 리스트
        List<EventGiveAwayDeliveryButtonAddMapperVO> buttonAddTitleList = staticsService.findWinningButtonAddFileNameListByEventId(eventId);
        if (PredicateUtils.isNotNullList(buttonAddTitleList)) {
            for (int i=0 ; i< buttonAddTitleList.size(); i++) {
                EventGiveAwayDeliveryButtonAddMapperVO buttonAddTitle = buttonAddTitleList.get(i);
                cell = row.createCell(11 + (i + 1));
                cell.setCellValue(buttonAddTitle.getFieldName());
            }
        }

        for (int i=0; i<=loopCount; i++) {
            Integer start = EventUtils.getPagingStartNumber(i, limitCount);
            if (start < 0) {
                start = 0;
            } else {
                start = (i * limitCount);
            }

            List<GiveAwayDeliveryListMapperVO> giveAwayDeliveryListMapperVoList = staticsService.selectGiveAwayDeliveryLimitList(
                    eventId,
                    DateUtils.convertDateToString(webEventBaseEntity.getEventStartDate(), DateUtils.PATTERN_YYYY_MMD_DD),
                    DateUtils.getNow(DateUtils.PATTERN_YYYY_MMD_DD),
                    start,
                    limitCount
            );

            for (GiveAwayDeliveryListMapperVO vo : giveAwayDeliveryListMapperVoList) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(vo.getProductName());

                cell = row.createCell(1);
                if (PredicateUtils.isNotNull(vo.getName()) || StringUtils.isNotEmpty(vo.getName())) {
                    cell.setCellValue(aes256Utils.decrypt(vo.getName()));
                } else {
                    cell.setCellValue(vo.getName());
                }

                cell = row.createCell(2);
                if (PredicateUtils.isNotNull(vo.getPhoneNumber()) || StringUtils.isNotEmpty(vo.getPhoneNumber())) {
                    cell.setCellValue(aes256Utils.decrypt(vo.getPhoneNumber()));
                } else {
                    cell.setCellValue(vo.getPhoneNumber());
                }


                cell = row.createCell(3);
                cell.setCellValue(vo.getZipCode());

                cell = row.createCell(4);
                if (PredicateUtils.isNotNull(vo.getAddress()) || StringUtils.isNotEmpty(vo.getAddress())) {
                    cell.setCellValue(aes256Utils.decrypt(vo.getAddress()));
                } else {
                    cell.setCellValue(vo.getAddress());
                }

                cell = row.createCell(5);
                if (PredicateUtils.isNotNull(vo.getAddressDetail()) || StringUtils.isNotEmpty(vo.getAddressDetail())) {
                    cell.setCellValue(aes256Utils.decrypt(vo.getAddressDetail()));
                } else {
                    cell.setCellValue(vo.getAddressDetail());
                }

                cell = row.createCell(6);
                cell.setCellValue(vo.getMemberBirth());

                cell = row.createCell(7);
                cell.setCellValue(vo.getCreatedDate());

                cell = row.createCell(8);
                cell.setCellValue(vo.getNftCouponId());

                cell = row.createCell(9);
                cell.setCellValue(vo.getIsUse());

                cell = row.createCell(10);
                cell.setCellValue(vo.getUseDate());


                //추가 항목 값 리스트
                List<EventGiveAwayDeliveryButtonAddMapperVO> deliveryAddValueList = new ArrayList<>();
                if (StringTools.containsIgnoreCase(eventId, "S")) {
                    deliveryAddValueList = staticsService.findStampEventGiveAwayDeliveryButtonAddByStpGiveAwayId(vo.getStpGiveAwayId());
                } else {
                    deliveryAddValueList = staticsService.findEventGiveAwayDeliveryButtonAddByGiveAwayId(vo.getGiveAwayId());
                }

                if (PredicateUtils.isNotNullList(deliveryAddValueList)) {
                    for (int j=0; j<deliveryAddValueList.size(); j++) {
                        EventGiveAwayDeliveryButtonAddMapperVO value = deliveryAddValueList.get(j);
                        cell = row.createCell(11 + (j + 1));
                        cell.setCellValue(value.getFieldValue());
                    }
                }
            }
        }

        //DEV, ALP, PROD
        if (!ProfileProperties.isLocal()) {

            //엑셀파일을 받을 담당자 목록 가져오기
            List<XtransReceiverEntity> receiverList = arEventService.findAllXtransReceiver();

            //담당자 목록이 정상적으로 있으면
            if (!PredicateUtils.isNull(receiverList)) {
                List<File> fileList = new ArrayList<>();
                for (XtransReceiverEntity entity : receiverList) {

                    try {
                        String developType = "(개발)";
                        if (ProfileProperties.isAlp()) developType = "(알파)";
                        else if (ProfileProperties.isProd()) developType = "(상용)";

                        String fileName = StringTools.joinStringsNoSeparator("당첨정보", developType);

                        //파일 생성 형식 : "서비스ID_사번_파일명"
                        File outputFile = File.createTempFile(xtransSftpEventId + "_" + entity.getEmployeeNumber() + "_" + eventId + fileName + DateUtils.returnNowDateByYyyymmddhhmmss(), ".xlsx");

                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            wb.write(fos);
                        } catch (FileNotFoundException fne) {
                            log.error(fne.getMessage(), fne);
                            break;
                        } catch (IOException ioe) {
                            log.error(ioe.getMessage(), ioe);
                            break;
                        }
                        fileList.add(outputFile);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        break;
                    }
                }
                //파일전송
                Map<String, Boolean> sftpResultMap = this.sendXtranFileList(fileList);
                //파일전송 시 에러 발생하면 에러처리
                if (PredicateUtils.isNotNull(sftpResultMap)) {
                    Boolean isSftpError = sftpResultMap.get("error");
                    if (PredicateUtils.isNotNull(isSftpError)) {
                        log.error("sftpError {}", isSftpError);
                    }
                }
            }
        } else {
            //로컬 테스트용
            File excelFile = null;
            try {
                //파일 생성 형식 : "서비스ID_사번_파일명"
                File outputFile = File.createTempFile(xtransSftpEventId + "_" + "1234" + "_" + eventId + "당첨정보" + DateUtils.returnNowDateByYyyymmddhhmmss(), ".xlsx");
                excelFile = outputFile;
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    wb.write(fos);
                } catch (FileNotFoundException fne) {
                    log.error(fne.getMessage(), fne);
                } catch (IOException ioe) {
                    log.error(ioe.getMessage(), ioe);
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            FileUtils.fileMove(excelFile.toString(), "/Users/anjiho/Downloads/12345.xlsx");
        }

        //제한테이블 상태 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, "2");

        resultMap.put("eventId", eventId);
        return CompletableFuture.completedFuture(resultMap);
    }

    public ApiResultObjectDto sendSftpCommonPvLogExcelLogic(@RequestBody String jsonStr) {
        int resultCode = httpSuccessCode;

        String eventName = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "eventName"));
        String startDate = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "startDate"));
        String endDate = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "endDate"));
        String logKeys = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "logKeys"));

        String[] splitLogKey = logKeys.split(",");
        List<String> logKeyList = Arrays.asList(splitLogKey);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header 필드
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("구분");

        // Header 필드에 로그키값 표현
        for (int k = 0; k < logKeyList.size(); k++) {
            cell = row.createCell((k + 1));
            cell.setCellValue(logKeyList.get(k));
        }
        //시작일 ~ 종료일 사이가 몇일인지 계산
        int diffDayCount = DateUtils.differenceTwoDay(startDate, endDate);
        //시작일 ~ 종료일 만큼 일자 리스트 만들기
        List<String> dateList = this.makeDateList(startDate, diffDayCount);

        LinkedList<Map<String, Object>>excelDataLinkedList = new LinkedList<>();

        Map<String, Object> totalMap = new HashMap<>();

        totalMap.put("date", "누적 합계");

        for (int j = 0; j < dateList.size(); j++) {
            String date = dateList.get(j);
            Map<String, Object> map = new HashMap<>();

            map.put("date", date);

            for (int i = 0; i < logKeyList.size(); i++) {
                String logKey = logKeyList.get(i);

                List<CommonLogPvMapperVO> commonLogPvList = staticsService.selectCommonLogPvList(eventName, logKey, startDate, endDate);

                Optional<CommonLogPvMapperVO> optional = commonLogPvList.stream().filter(log -> PredicateUtils.isEqualsStr(log.getDate(), date)).findAny();

                if (optional.isPresent()) {
                    map.put("count_" + (i+1), optional.get().getCount());
                } else {
                    map.put("count_" + (i+1), 0);
                }

                if ((j + 1) == dateList.size()) {
                    Optional<CommonLogPvMapperVO> optional2 = commonLogPvList.stream().filter(log -> PredicateUtils.isEqualsStr(log.getDate(), "total")).findFirst();

                    if (optional2.isPresent()) {
                        totalMap.put("total_count_" + (i + 1), optional2.get().getCount());
                    } else {
                        totalMap.put("total_count_" + (i + 1), 0);
                    }
                }
            }
            excelDataLinkedList.add(map);
        }
        excelDataLinkedList.add(totalMap);
        log.debug("list >>" + excelDataLinkedList.toString());

        int p = 0;
        for (Map<String, Object> paramMap : excelDataLinkedList) {

            row = sheet.createRow(rowNum++);

            for (int i = 0; i <= logKeyList.size(); i++) {
                cell = row.createCell(i);
                if (i == 0) {
                    cell.setCellValue(paramMap.get("date").toString());
                } else {
                    if (p == (dateList.size())) {
                        cell.setCellValue(Integer.parseInt(excelDataLinkedList.get((p)).get("total_count_" + i).toString()));
                    } else {
                        cell.setCellValue(Integer.parseInt(paramMap.get("count_" + i).toString()));
                    }
                }
            }
            p++;
        }

        if (!ProfileProperties.isLocal()) {
            //엑셀파일을 받을 담당자 목록 가져오기
            List<XtransReceiverEntity> receiverList = arEventService.findAllXtransReceiver();

            //담당자 목록이 정상적으로 있으면
            if (!PredicateUtils.isNull(receiverList)) {
                List<File> fileList = new ArrayList<>();
                for (XtransReceiverEntity entity : receiverList) {

                    try {
                        //파일 생성 형식 : "서비스ID_사번_파일명"
                        File outputFile = File.createTempFile(xtransSftpEventId + "_" + entity.getEmployeeNumber() + "_" + "PV로그" + DateUtils.returnNowDateByYyyymmddhhmmss(), ".xlsx");

                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            wb.write(fos);
                        } catch (FileNotFoundException fne) {
                            resultCode = ErrorCodeDefine.FILE_NOT_FOUND.code();
                            log.error(fne.getMessage(), fne);
                            break;
                        } catch (IOException ioe) {
                            resultCode = ErrorCodeDefine.IOE_ERROR.code();
                            log.error(ioe.getMessage(), ioe);
                            break;
                        }
                        fileList.add(outputFile);
                    } catch (IOException e) {
                        resultCode = ErrorCodeDefine.IOE_ERROR.code();
                        log.error(e.getMessage(), e);
                        break;
                    }
                }
                //파일전송
                Map<String, Boolean> sftpResultMap = this.sendXtranFileList(fileList);
                //파일전송 시 에러 발생하면 에러처리
                if (PredicateUtils.isNotNull(sftpResultMap)) {
                    Boolean isSftpError = sftpResultMap.get("error");
                    if (PredicateUtils.isNotNull(isSftpError)) {
                        resultCode = ErrorCodeDefine.CONNECTION_TIMEOUT.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(null)
                .build();
    }

    /**
     * X-TRAN 여러개 파일 전송하기
     * @param fileList
     * @return
     */
    public Map<String, Boolean> sendXtranFileList(List<File> fileList) {
        if (!ProfileProperties.isLocal()) {
            //x-trans sftp 접속정보
            SFtpSenderUtils sFtpSenderUtils = new SFtpSenderUtils().builder()
                    .host(xtransSftpHost)
                    .port(xtransSftpPort)
                    .username(xtransSftpUser)
                    .password(xtransSftpPass)
                    .defaultPath(xtransDefaultPath)
                    .build();

            //x-trans sftp 접속
            sFtpSenderUtils.connect();

            //파일전송
            Map<String, Boolean> sftpResultMap = sFtpSenderUtils.fileSendAllList(fileList);

            //파일전송 시 에러 발생하면 에러처리
            return sftpResultMap;
        } else {
//            for (File file : fileList) {
//                FileUtils.fileMove(file.toString(), "/Users/anjiho/Downloads/" + file.getName());
//            }
        }
        return null;
    }

    private List<String> makeDateList(String startDate, int dateCount) {
        List<String> makeDateList = new ArrayList<>();
        for (int i = 0; i <= dateCount; i++) {
            String date = DateUtils.plusDay(startDate, "YYYY.MM.DD", i);
            makeDateList.add(date);
        }
        return makeDateList;
    }

}
