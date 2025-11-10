package kr.co.syrup.adreport.controller.rest.web.event;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.EventUtils;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.stamp.event.define.StampWinningAttendTypeDefine;
import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampAccWinningProductResVO;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StampTrAccYnResVO;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.stamp.event.service.StampLogService;
import kr.co.syrup.adreport.survey.go.define.ExampleChoiceTypeDefine;
import kr.co.syrup.adreport.survey.go.define.ExampleTypeDefine;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyTableStaticsResDto;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableCategoryStaticsResVO;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableExampleStaticsResVO;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyTableSubjectStaticsResVO;
import kr.co.syrup.adreport.survey.go.service.SurveyGoStaticsService;
import kr.co.syrup.adreport.web.event.define.CommonSettingsDefine;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.EventLogicalTypeDefine;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import kr.co.syrup.adreport.web.event.entity.XtransReceiverEntity;
import kr.co.syrup.adreport.web.event.logic.XtransLogic;
import kr.co.syrup.adreport.web.event.mybatis.mapper.StaticsMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.*;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.BatchService;
import kr.co.syrup.adreport.web.event.service.StaticsService;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@CrossOrigin(value = "*")
@Api(value="ar-event", description="AR 이벤트 소다 파일 다운로드 컨트롤러")
@RequestMapping(value = "/api/v1/web-event-file-download")
public class WebEventFileDownloadController {

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private StaticsService staticsService;

    @Autowired
    private SurveyGoStaticsService surveyGoStaticsService;

    @Autowired
    private StampLogService stampLogService;

    @Autowired
    private XtransLogic xtransLogic;

    @Autowired
    private AES256Utils aes256Utils;

    @Value("${xtrans.sftp.event-id}")
    private String xtransSftpEventId;

    @Value("${spring.profiles}")
    private String profile;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private StaticsMapper staticsMapper;



    @SetSodarMemberSession
    @PostMapping(value = "/excel/attend-code")
    @ApiOperation("엑셀파일 참여코드 다운로드(참여번호 사용현황)")
    public void downloadExcelAttendCode(HttpServletResponse response,
                                        @RequestParam(value = "eventId") String eventId,
                                        @RequestParam(value = "traceNo", required = false) String traceNo) {
        log.info("traceNo {} ", EventUtils.traceNo(traceNo));
        List<EventGateCodeAtUsedMapperVO> arEventGateCodeEntityList = arEventService.findArEventGateCodeByEventIdAtUsed(eventId);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;
        // Header 필드
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("이벤트 아이디");
        cell = row.createCell(1);
        cell.setCellValue("참여코드");
        cell = row.createCell(2);
        cell.setCellValue("참여코드 사용여부");

        if (!StringTools.containsIgnoreCase(eventId, "S")) {
            cell = row.createCell(3);
            cell.setCellValue("참여코드 사용개수");
            cell = row.createCell(4);
            cell.setCellValue("참여코드 참여로그 개수");
        }

        if (!arEventGateCodeEntityList.isEmpty()) {

            for (EventGateCodeAtUsedMapperVO codeEntity : arEventGateCodeEntityList) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(eventId);

                cell = row.createCell(1);
                cell.setCellValue(codeEntity.getAttendCode());

                int usedCount = codeEntity.getUsedCount();
                int logCnt = codeEntity.getCnt();

                String usedStr = "미사용";
                if (StringTools.containsIgnoreCase(eventId, "S")) {
                    if (codeEntity.getIsUse()) {
                        usedStr = "사용";
                    }
                } else {
                    if (usedCount > 0 || logCnt > 0) {
                        usedStr = "사용";
                    }
                    cell = row.createCell(3);
                    cell.setCellValue(codeEntity.getUsedCount());

                    cell = row.createCell(4);
                    cell.setCellValue(codeEntity.getCnt());
                }
                cell = row.createCell(2);
                cell.setCellValue(usedStr);


            }

        }
        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=attend_code_list_" + eventId + "_" + DateUtils.returnNowDateByYyyymmddhhmmss() + ".xlsx");

        try {
            // Excel File Output
            wb.write(response.getOutputStream());
            wb.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("File OutputStream Error");
        }
    }

    @SetSodarMemberSession
    @PostMapping(value = "/excel/give-away")
    @ApiOperation("엑셀파일 당첨정보 다운로드")
    public void downloadExcelGiveAwayDeliveryList(HttpServletResponse response,
                                                  @RequestBody String jsonStr) {
        String eventId = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "eventId"));
        String startDate = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "startDate"));
        String endDate = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "endDate"));

        List<GiveAwayDeliveryListMapperVO> giveAwayDeliveryListMapperVoList = staticsService.selectGiveAwayDeliveryList(eventId, startDate, endDate);

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

        if (!CollectionUtils.isEmpty(giveAwayDeliveryListMapperVoList)) {

            for (GiveAwayDeliveryListMapperVO vo : giveAwayDeliveryListMapperVoList) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(vo.getProductName());

                cell = row.createCell(1);
                cell.setCellValue(aes256Utils.decrypt(vo.getName()));

                cell = row.createCell(2);
                cell.setCellValue(aes256Utils.decrypt(vo.getPhoneNumber()));

                cell = row.createCell(3);
                cell.setCellValue(vo.getZipCode());

                cell = row.createCell(4);
                cell.setCellValue(aes256Utils.decrypt(vo.getAddress()));

                cell = row.createCell(5);
                cell.setCellValue(aes256Utils.decrypt(vo.getAddressDetail()));

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
            }
        }
        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=give_away_" + eventId + "_" + DateUtils.returnNowDateByYyyymmddhhmmss() + ".xlsx");

        try {
            // Excel File Output
            wb.write(response.getOutputStream());
            wb.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("File OutputStream Error");
        }
    }

    @PostMapping(value = "/excel/common-log-pv")
    @ApiOperation("공통 pv 로그 다운로드")
    public void downloadCommonLogPvList(HttpServletResponse response,
                                        @RequestBody String jsonStr) {
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
        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
        //response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + eventName +  "_pv_log_statics_" + DateUtils.returnNowDateByYyyymmddhhmmss() + ".xlsx");

        try {
            // Excel File Output
            wb.write(response.getOutputStream());
            wb.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("File OutputStream Error");
        }
    }

    @PostMapping(value = "/excel/common-log-person-agree")
    @ApiOperation("공통 개인정보 수집 로그 다운로드")
    public void downloadCommonLogPersonAgreeList(HttpServletResponse response,
                                                @RequestBody String jsonStr) {
        String eventName = StringTools.convertNullToEmptyString(GsonUtils.parseStringJsonStr(jsonStr, "eventName"));

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header 필드
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("ID");

        cell = row.createCell(1);
        cell.setCellValue("동의여부");

        cell = row.createCell(2);
        cell.setCellValue("생성일");

        cell = row.createCell(3);
        cell.setCellValue("핸드폰번호");

        List<CommonLogPersonAgreeMapperVO> personAgreeMapperVOList = staticsService.findCommonLogPersonAgreeListByEventName(eventName);

        if (!PredicateUtils.isNull(personAgreeMapperVOList)) {
            for (CommonLogPersonAgreeMapperVO vo : personAgreeMapperVOList) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(PredicateUtils.isNotNull(vo.getAgreeId()) ? aes256Utils.decrypt(vo.getAgreeId()) : "");

                cell = row.createCell(1);
                cell.setCellValue("동의");

                cell = row.createCell(2);
                cell.setCellValue(vo.getCreatedDate());

                cell = row.createCell(3);
                cell.setCellValue(PredicateUtils.isNotNull(vo.getPhoneNumber()) ? aes256Utils.decrypt(vo.getPhoneNumber()) : "");

            }
        }

        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
        //response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + eventName +  "_pv_log_person_agree_" + DateUtils.returnNowDateByYyyymmddhhmmss() + ".xlsx");

        try {
            // Excel File Output
            wb.write(response.getOutputStream());
            wb.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            log.error("File OutputStream Error");
        }
    }

    @SetSodarMemberSession
    @PostMapping(value = "/excel/survey-go/table-statics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eventId", required = true, dataType = "string", paramType = "query", value = "이벤트 아이디"),
            @ApiImplicitParam(name = "limitCount", required = false, dataType = "int", paramType = "query", value = "페이징 개수"),
            @ApiImplicitParam(name = "sheetType", required = true, dataType = "string", paramType = "query", value = "다운로드 종류(ANSWER : 응답통계, ROW : 로우데이타)")
    })
    @ApiOperation("서베이고 통계 XTRAN 전송")
    public ResponseEntity<ApiResultObjectDto> downloadSurveyGoTableStatic2(HttpServletResponse response,
                                                       @RequestParam(value = "eventId") String eventId,
                                                       @RequestParam(value = "limitCount") int limitCount,
                                                       @RequestParam(value = "sheetType") String sheetType) {
        int resultCode = HttpStatus.OK.value();

        Map<String, String>resultMap = new HashMap<>();

        CommonSettingsEntity settingsEntity = new CommonSettingsEntity();
        if (PredicateUtils.isEqualsStr(sheetType, "ANSWER")) {
            settingsEntity = arEventService.findCommonSettingsBySettingKey(CommonSettingsDefine.SURVEY_EXCEL_ANSWER_LIMIT.name());
        }
        if (PredicateUtils.isEqualsStr(sheetType, "ROW")) {
            settingsEntity = arEventService.findCommonSettingsBySettingKey(CommonSettingsDefine.SURVEY_EXCEL_ROW_LIMIT.name());
        }

        if (PredicateUtils.isNotNull(settingsEntity.getId())) {
            if (PredicateUtils.isNotEqualsStr(settingsEntity.getValue(), "1")) {
                CommonSettingsEntity finalSettingsEntity = settingsEntity;
                CompletableFuture.supplyAsync(() ->this.completableFutureSurveyGoTableStatic(eventId, limitCount, sheetType, finalSettingsEntity.getSettingKey()));
            } else {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_EXCEL_DOWNLOAD.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                resultMap.put("commonValue", settingsEntity.getValue());
                resultMap.put("commonDesc", settingsEntity.getCommonSettingsDesc());
            }
        }
        ApiResultObjectDto resultObjectDto = new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();

        return ResponseEntity.ok(resultObjectDto);
    }

    private CompletableFuture<Map<String, Object>> completableFutureSurveyGoTableStatic(String eventId, int limitCount, String sheetType, String commonSettingsKey) {
        String commonValue = "1";
        //제한테이블 진행중으로 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, commonValue);

        //제한 개수가 없을때 예외처리
        if (PredicateUtils.isEqualZero(limitCount)) limitCount = 100;

        int resultCode = HttpStatus.OK.value();

        SXSSFWorkbook wb = new SXSSFWorkbook();
        Row row = null, row2 = null, row3 = null;
        Cell cell = null, cell2 = null, cell3 = null;
        int rowNum = 1, rowNum2 = 1, rowNum3 = 0;

        //제목 cell 스타일
        CellStyle headerCellStyle = ExcelCellStyle.getCellStyle(
                wb, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, IndexedColors.GREY_50_PERCENT.getIndex(), IndexedColors.WHITE.getIndex(), true
        );
        //문항 cell 스타일
        CellStyle subjectCellStyle = ExcelCellStyle.getCellStyle(
                wb, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex(), (short) 0, false
        );
        //내용 cell 스타일
        CellStyle exampleCellStyle = ExcelCellStyle.getCellStyle(
                wb, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, (short) 0, (short) 0, false
        );

        ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);

        if (PredicateUtils.isEqualsStr(sheetType, "ANSWER")) {
            //=============================================== 문항 응답 결과 시작 ================================================== //
            List<SurveyTableStaticsResDto> resultList = new ArrayList<>();
            List<SurveyTableSubjectStaticsResVO> subjectList = surveyGoStaticsService.findSurveyTableSubjectStaticsList(eventId);

            if (PredicateUtils.isNotNullList(subjectList)) {
                for (SurveyTableSubjectStaticsResVO vo : subjectList) {
                    SurveyTableStaticsResDto dto = new SurveyTableStaticsResDto();
                    dto.setSubjectInfo(vo);

                    List<SurveyTableExampleStaticsResVO> exampleList = new ArrayList<SurveyTableExampleStaticsResVO>();

                    if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.퀴즈형.value())) {
                        if (PredicateUtils.isEqualsStr(vo.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                            exampleList = surveyGoStaticsService.findSurveyExampleQuestionAnswerStaticsByQuiz(vo.getSurveySubjectId());

                            int wrongCnt = 0;
                            if (PredicateUtils.isNotNullList(exampleList)) {
                                int answerSum = exampleList.stream().mapToInt(SurveyTableExampleStaticsResVO::getExampleTotalCount).sum();
                                //오답개수
                                wrongCnt = vo.getTotalCount() - answerSum;
                            }
                            //총 오답 선택수, 점유율
                            //exampleList.add(exampleList.size(), surveyGoStaticsService.findSurveyQuizQuestionWrong(vo.getSurveySubjectId()));
                            exampleList.add(exampleList.size(), surveyGoStaticsService.makeSurveyQuizQuestionWrong(wrongCnt));
                            dto.setExampleInfo(exampleList);
                        } else {
                            exampleList = surveyGoStaticsService.findSurveyTableExampleStaticsList(vo.getSurveySubjectId(), vo.getEtcOpinionReceiveYn());
                            dto.setExampleInfo(exampleList);
                        }
                    } else {
                        exampleList = surveyGoStaticsService.findSurveyTableExampleStaticsList(vo.getSurveySubjectId(), vo.getEtcOpinionReceiveYn());
                        dto.setExampleInfo(exampleList);
                    }

                    resultList.add(dto);
                }
            }

            Sheet sheet = wb.createSheet("survey_table");

            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue("■ 문항 응답 결과");

            // Header 필드
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("구분");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("총 보기 선택수");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("보기 점유율");
            cell.setCellStyle(headerCellStyle);

            if (PredicateUtils.isNotNullList(resultList)) {

                //문항
                for (SurveyTableStaticsResDto dto : resultList) {
                    row = sheet.createRow(rowNum++);
                    //구분
                    cell = row.createCell(0);
                    cell.setCellValue("문항" + dto.getSubjectInfo().getSort());
                    cell.setCellStyle(subjectCellStyle);

                    //총 보기 선택수
                    cell = row.createCell(1);
                    cell.setCellValue(dto.getSubjectInfo().getTotalCount());
                    cell.setCellStyle(subjectCellStyle);

                    //보기 점유율
                    cell = row.createCell(2);
                    //객관식
                    if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                        cell.setCellValue(PredicateUtils.isEqualZero(dto.getSubjectInfo().getTotalCount()) ? "0%" : "100%");
                    }
                    //주관식
                    if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                        cell.setCellValue("-");
                    }
                    cell.setCellStyle(subjectCellStyle);

                    List<SurveyTableExampleStaticsResVO> exampleInfo = dto.getExampleInfo();

                    if (PredicateUtils.isNotNullList(exampleInfo)) {

                        int j = 1;
                        //보기
                        for (SurveyTableExampleStaticsResVO example : exampleInfo) {
                            //row = sheet.createRow(rowNum++);
                            //구분 시작
                            //cell = row.createCell(0);
                            //서베이고_기본형 or 분석형
                            if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.서베이고_기본형.value())
                                    || PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.분석형.value())) {

                                //기본형, 분석형 > 객관식
                                if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                                    row = sheet.createRow(rowNum++);
                                    cell = row.createCell(0);

                                    //OX 형
                                    if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getExampleChoiceType(), ExampleChoiceTypeDefine.OX.name())) {
                                        if (example.getSort() == 1) {
                                            cell.setCellValue("보기" + example.getSort() + "(O)");
                                        }
                                        if (example.getSort() == 2) {
                                            cell.setCellValue("보기" + example.getSort() + "(X)");
                                        }
                                    } else if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getExampleChoiceType(), ExampleChoiceTypeDefine.IMG.name())) {
                                        //이미지 형
                                        cell.setCellValue("보기" + example.getSort() + "(이미지형)");

                                    } else if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getExampleChoiceType(), ExampleChoiceTypeDefine.SCALE.name())) {
                                        //척도형
                                        cell.setCellValue("보기" + example.getSort() + "(척도형)");

                                    } else {
                                        if (example.getSort() == -1) {
                                            //구분 - 기타값 주입
                                            cell.setCellValue(example.getExampleTitle());
                                        } else {
                                            cell.setCellValue("보기" + example.getSort() + "(" + example.getExampleTitle() + ")");
                                        }
                                    }
                                    cell.setCellStyle(exampleCellStyle);
                                }

                            } else if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.퀴즈형.value())) {
                                row = sheet.createRow(rowNum++);
                                cell = row.createCell(0);

                                //퀴즈형 > 객관식
                                if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                                    // OX 형
                                    if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getExampleChoiceType(), ExampleChoiceTypeDefine.OX.name())) {
                                        if (example.getSort() == 1) {
                                            cell.setCellValue("보기" + example.getSort() + "(O)_정답");
                                        }
                                        if (example.getSort() == 2) {
                                            cell.setCellValue("보기" + example.getSort() + "(X)_오답");
                                        }
                                    } else if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getExampleChoiceType(), ExampleChoiceTypeDefine.IMG.name())) {
                                        String answerStr = "오답";
                                        if (dto.getSubjectInfo().getQuizAnswerSort() == example.getSort())
                                            answerStr = "정답";

                                        cell.setCellValue("보기" + example.getSort() + "(이미지형)_" + answerStr);
                                    } else {
                                        String answerStr = "오답";
                                        if (dto.getSubjectInfo().getQuizAnswerSort() == example.getSort())
                                            answerStr = "정답";

                                        cell.setCellValue("보기" + example.getSort() + "(" + example.getExampleTitle() + ")_" + answerStr);
                                    }
                                } else {
                                    //퀴즈형 > 주관식
                                    if (j == exampleInfo.size()) {
                                        cell.setCellValue(example.getExampleTitle());
                                    } else {
                                        cell.setCellValue("정답" + example.getSort() + "(" + example.getExampleTitle() + ")");
                                    }
                                }
                                cell.setCellStyle(exampleCellStyle);
                            }

                            //구분 끝

                            //객관식일때만 총보기 선택수, 보기점유율 주입
                            if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getSubjectExampleType(), ExampleTypeDefine.CHOICE.name())) {
                                //총 보기 선택수
                                cell = row.createCell(1);
                                cell.setCellValue(example.getExampleTotalCount());
                                cell.setCellStyle(exampleCellStyle);

                                //보기 점유율
                                cell = row.createCell(2);
                                if (example.getSort() == -1) {
                                    //보기 점유율 - 기타일때
                                    cell.setCellValue("-");
                                } else {
                                    if (PredicateUtils.isEqualsStr(dto.getSubjectInfo().getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                        cell.setCellValue("");
                                    } else {
                                        cell.setCellValue(PredicateUtils.isNull(example.getPercent()) ? "0" : String.valueOf(example.getPercent()));
                                    }
                                }
                                cell.setCellStyle(exampleCellStyle);
                            } else {
                                if (PredicateUtils.isNotEqualsStr(dto.getSubjectInfo().getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                    //총 보기 선택수
                                    cell = row.createCell(1);
                                    cell.setCellValue(example.getExampleTotalCount());
                                    cell.setCellStyle(exampleCellStyle);

                                    //보기 점유율
                                    cell = row.createCell(2);
                                    //퀴즈형 > 주관식
                                    if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.퀴즈형.value())) {
                                        cell.setCellValue("");
                                    } else {
                                        cell.setCellValue(PredicateUtils.isNull(example.getPercent()) ? "0" : String.valueOf(example.getPercent()));
                                    }
                                    cell.setCellStyle(exampleCellStyle);
                                } else {
                                    if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.퀴즈형.value())) {
                                        //총 보기 선택수
                                        cell = row.createCell(1);
                                        cell.setCellValue(example.getExampleTotalCount());
                                        cell.setCellStyle(exampleCellStyle);

                                        //보기 점유율
                                        cell = row.createCell(2);
                                        cell.setCellValue("-");
                                    }
                                    cell.setCellStyle(exampleCellStyle);
                                }
                            }
                            j++;
                        }
                    }
                }
            }
            //=============================================== 문항 응답 결과 끝 ================================================== //

            //=============================================== 유형별응답결과(퀴즈형) 시작 ================================================== //
            if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.퀴즈형.value())
                    || PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.분석형.value())) {
                Sheet sheet2 = wb.createSheet("category_table");

                row2 = sheet2.createRow(0);
                cell2 = row2.createCell(0);

                if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.퀴즈형.value())) {
                    cell2.setCellValue("■ 유형별응답결과(퀴즈형)");
                }
                if (PredicateUtils.isEqualsStr(arEvent.getEventLogicalType(), EventLogicalTypeDefine.분석형.value())) {
                    cell2.setCellValue("■ 유형별응답결과(분석형)");
                }

                row2 = sheet2.createRow(rowNum2++);

                cell2 = row2.createCell(0);
                cell2.setCellValue("");
                cell2.setCellStyle(subjectCellStyle);


                cell2 = row2.createCell(1);
                cell2.setCellValue("조건");
                cell2.setCellStyle(subjectCellStyle);

                cell2 = row2.createCell(2);
                cell2.setCellValue("달성수");
                cell2.setCellStyle(subjectCellStyle);

                List<SurveyTableCategoryStaticsResVO> categoryStaticsList = surveyGoStaticsService.findSurveySubjectCategoryStatics(arEvent.getArEventId(), arEvent.getEventLogicalType());

                if (PredicateUtils.isNotNullList(categoryStaticsList)) {
                    for (SurveyTableCategoryStaticsResVO resVO : categoryStaticsList) {
                        row2 = sheet2.createRow(rowNum2++);
                        //구분
                        cell2 = row2.createCell(0);
                        cell2.setCellValue(resVO.getTitle());
                        cell2.setCellStyle(exampleCellStyle);

                        //총 보기 선택수
                        cell2 = row2.createCell(1);
                        cell2.setCellValue(resVO.getWeight());
                        cell2.setCellStyle(exampleCellStyle);

                        //보기 점유율
                        cell2 = row2.createCell(2);
                        cell2.setCellValue(resVO.getAchieveCount());
                        cell2.setCellStyle(exampleCellStyle);
                    }
                }
            }
            //=============================================== 유형별응답결과(퀴즈형) 끝 ================================================== //
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (PredicateUtils.isEqualsStr(sheetType, "ROW")) {
            //=============================================== raw_table 시작 ================================================== //
            Sheet sheet3 = wb.createSheet("raw_table");
            /**
             * 개서전 코드 시작
             */
//            List<String> headerTitleList = surveyGoStaticsService.makeSurveyRawTableTitle(arEvent.getArEventId());
//
//            row3 = sheet3.createRow(rowNum3++);
//            for (int i=0; i<headerTitleList.size(); i++) {
//                String title = headerTitleList.get(i);
//                cell3 = row3.createCell(i);
//                cell3.setCellValue(title);
//                cell3.setCellStyle(subjectCellStyle);
//            }
//
//            List<List<String>> answerList = new ArrayList<>();
//            try {
//                 answerList = surveyGoStaticsService.makeSurveyRawTableValue2(eventId, limitCount, arEvent.getArEventId());//서베이고를 완료한 survey_log_attend_id 로 참여한 서베이고 로그 결과 리스트 가져오기
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                arEventService.updateCommonSettings(commonSettingsKey, "-1");
//            }
//
//            if (PredicateUtils.isNotNullList(answerList)) {
//                for (int j=0; j< answerList.size(); j++) {
//                    row3 = sheet3.createRow(rowNum3++);
//
//                    List<String> answers = answerList.get(j);
//
//                    for (int k=0; k< answers.size(); k++) {
//                        String answer = answers.get(k);
//
//                        cell3 = row3.createCell(k);
//                        cell3.setCellValue(StringTools.convertNullToEmptyString(answer));
//                        cell3.setCellStyle(exampleCellStyle);
//                    }
//                }
//            }
            /**
             * 개서전 코드 끝
             */

            //설문 제목 리스트
            List<String> headerTitleList = surveyGoStaticsService.makeSurveyRawTableTitle(arEvent.getArEventId());

            row = sheet3.createRow(rowNum++);

            //설문 제목 리스트 만큼 엑셀 주입
            for (int i=0; i<headerTitleList.size(); i++) {
                String title = headerTitleList.get(i);
                cell = row.createCell(i);
                cell.setCellValue(title);
                cell.setCellStyle(subjectCellStyle);
            }

            //설문한 결과 내용 row 가져오기
            List<Map<String,String>>list = surveyGoStaticsService.getSurveyAnswerStaticsListByEventId(eventId);

            //설문한 결과 내용 만큼 엑셀 주입
            if (PredicateUtils.isNotNullList(list)) {
                for (int j = 0; j < list.size(); j++) {
                    row = sheet3.createRow(rowNum++);

                    Map<String,String> answer = list.get(j);

                    for (int k = 0; k < headerTitleList.size(); k++) {
                        cell = row.createCell(k);
                        if (k == 0) {
                            cell.setCellValue(StringTools.convertNullToEmptyString(answer.get("survey_log_attend_id")));
                        } else {
                            cell.setCellValue(StringTools.convertNullToEmptyString(String.valueOf(answer.get("answer_" + (k)))));
                        }
                        cell.setCellStyle(exampleCellStyle);
                    }
                }
            }
            //=============================================== raw_table 끝 ================================================== //
        }

        // ================================================ 원본 파일 생성 시작 ================================================ //
        File originFile = null;
        try {
            //원본파일 생성
            originFile = File.createTempFile("originaFile", ".xlsx");

            //원본 엑셀 파일 FileOutputStream
            try (FileOutputStream fos = new FileOutputStream(originFile)) {
                wb.write(fos);
            } catch (FileNotFoundException fne) {
                resultCode = ErrorCodeDefine.FILE_NOT_FOUND.code();
                log.error(fne.getMessage(), fne);
            } catch (IOException ioe) {
                resultCode = ErrorCodeDefine.IOE_ERROR.code();
                log.error(ioe.getMessage(), ioe);
            } finally {
                wb.dispose();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // ================================================ 원본 파일 생성 끝 ================================================ //

        //=============================================== x-tran 전송 시작 ================================================== //
        //엑셀파일을 받을 담당자 목록 가져오기
        List<XtransReceiverEntity> receiverList = arEventService.findAllXtransReceiver();

        //담당자 목록이 정상적으로 있으면
        if (PredicateUtils.isNotNullList(receiverList)) {
            List<File> fileList = new ArrayList<>();
            for (XtransReceiverEntity entity : receiverList) {

                try {
                    String developType;
                    if (ProfileProperties.isDev()) {
                        developType = "(개발)";
                    } else if (ProfileProperties.isAlp()) {
                        developType = "(알파)";
                    } else if (ProfileProperties.isProd()) {
                        developType = "(상용)";
                    } else {
                        developType = "(로컬)";
                    }

                    String fileName;
                    if (PredicateUtils.isEqualsStr(sheetType, "ROW")) {
                        fileName = StringTools.joinStringsNoSeparator("서베이고로우데이타통계", developType);
                    } else {
                        fileName = StringTools.joinStringsNoSeparator("서베이고응답통계", developType);
                    }

                    //파일 생성 형식 : "서비스ID_사번_파일명"
                    String outputFileName = StringTools.joinStringsNoSeparator(xtransSftpEventId, "_", entity.getEmployeeNumber(), "_", eventId, fileName, DateUtils.returnNowDateByYyyymmddhhmmss());
                    File outputFile = File.createTempFile(outputFileName, ".xlsx");

                    //원본 엑셀 파일 경로
                    Path originFilePath = Paths.get(originFile.toString());

                    //복사할 파일명
                    Path targetFile = Paths.get(outputFile.toString());

                    //수신자 만큼 엑셀파일 복사
                    FileUtils.copyFileReplaceExisting(originFilePath, targetFile);

                    //엑셀 복사파일 발송 리스트에 추가
                    fileList.add(targetFile.toFile());

                } catch (IOException e) {
                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                    log.error(e.getMessage(), e);
                    break;
                }
            }

            //x-trans 전송
            Map<String, Boolean> sftpResultMap = xtransLogic.sendXtranFileList(fileList);

            //파일전송 시 에러 발생하면 에러처리
            if (PredicateUtils.isNotNull(sftpResultMap)) {
                if (sftpResultMap.get("error") != null) {
                    Boolean isSftpError = sftpResultMap.get("error");
                    if (PredicateUtils.isNotNull(isSftpError)) {
                        resultCode = ErrorCodeDefine.CONNECTION_TIMEOUT.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    }
                }
            }
        }
        //=============================================== x-tran 전송 끝  ================================================== //

        if (resultCode == 200) {
            commonValue = "2";  //성공
        } else {
            commonValue = "-1"; //실패
        }
        //제한테이블 상태 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, commonValue);

        Map<String, Object>resultMap = new HashMap<>();
        resultMap.put("success", true);
        resultMap.put("resultCode", resultCode);

        return CompletableFuture.completedFuture(resultMap);
    }

    //@SetSodarMemberSession
    @PostMapping(value = "/excel/stamp/acc-statics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eventId", required = true, dataType = "string", paramType = "query", value = "이벤트 아이디"),
    })
    @ApiOperation("스탬프 적립 통계")
    public ResponseEntity<ApiResultObjectDto> stampAccStatic(HttpServletResponse response,
                                                             @RequestParam(value = "eventId") String eventId) {
        int resultCode = HttpStatus.OK.value();

        Map<String, String>resultMap = new HashMap<>();

        CommonSettingsEntity settingsEntity = arEventService.findCommonSettingsBySettingKey(CommonSettingsDefine.STAMP_ACC_EXCEL_LIMIT.name());

        if (PredicateUtils.isNotNull(settingsEntity.getId())) {
            if (PredicateUtils.isNotEqualsStr(settingsEntity.getValue(), "1")) {
                CompletableFuture.supplyAsync(() ->this.completableFutureStampAccStatic(eventId, settingsEntity.getSettingKey()));
            } else {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_LIMIT_EXCEL_DOWNLOAD.code();
                log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));

                resultMap.put("commonValue", settingsEntity.getValue());
                resultMap.put("commonDesc", settingsEntity.getCommonSettingsDesc());
            }
        }
        ApiResultObjectDto resultObjectDto = new ApiResultObjectDto().builder()
                .resultCode(resultCode)
                .result(resultMap)
                .build();

        return ResponseEntity.ok(resultObjectDto);
    }

    public CompletableFuture<Map<String, Object>> completableFutureStampAccStatic(String eventId, String commonSettingsKey) {
        int resultCode = HttpStatus.OK.value();

        String commonValue = "1";
        //제한테이블 진행중으로 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, commonValue);

        StampEventMainModel stampMain = stampFrontService.findStampEventMainByEventId(eventId);

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;
        int valueRowNum = 1;
        // Header 필드
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("NO");
        cell = row.createCell(1);
        if (PredicateUtils.isEqualsStr(stampMain.getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name())) {
            cell.setCellValue("휴대폰번호");
        } else {
            cell.setCellValue("참여코드");
        }
        cell = row.createCell(2);
        cell.setCellValue("총 스탬프 개수");

        //TR 제목 리스트
        List<StampTrAccYnResVO> stampTrAccYnTitleList = stampLogService.getStampTrAccYnByAttendValue(stampMain.getStpId(), null);
        //당첨 제목 리스트
        List<StampAccWinningProductResVO> stmapAccWinningTitleList = stampLogService.getCountStampWinningProduct(stampMain.getStpId(), null, null);

        int trTitleStartNum = 3;
        int productTitleStartNum = 0;
        //TR 제목 셀 값 주입
        for (int i = 0; i < stampTrAccYnTitleList.size(); i++) {
            StampTrAccYnResVO vo = stampTrAccYnTitleList.get(i);
            cell = row.createCell(trTitleStartNum);
            cell.setCellValue(StringTools.joinStringsNoSeparator("TR", String.valueOf(vo.getStpTrSort()), "/", vo.getStpTrTxt()));

            trTitleStartNum++;
            productTitleStartNum = trTitleStartNum;
        }
        //당첨 제목 셀 값 주입
        for (int j = 0; j < stmapAccWinningTitleList.size() ; j++) {
            StampAccWinningProductResVO vo = stmapAccWinningTitleList.get(j);
            cell = row.createCell(productTitleStartNum);
            cell.setCellValue(StringTools.joinStringsNoSeparator(vo.getProductName()));

            productTitleStartNum++;
        }

        //스탬프 참여한 사용자 리스트
        List<String>stampAccUserList = stampLogService.getStampAccUserListByEventId(eventId);

        //핸드폰 또는 참여번호별로 값 리스트 주입
        if (PredicateUtils.isNotNullList(stampAccUserList)) {
            for (int i = 0 ; i < stampAccUserList.size(); i++) {
                int trStampTotalStartNum = 2;
                int trValueStartNum = 3;
                String attendValue = stampAccUserList.get(i);

                row = sheet.createRow(valueRowNum++);

                cell = row.createCell(0);
                cell.setCellValue((i + 1)); //순번

                cell = row.createCell(1);
                cell.setCellValue(PredicateUtils.isEqualsStr(stampMain.getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name()) ? aes256Utils.decrypt(attendValue) : attendValue);    //핸드폰번호 또는 참여번호

                List<StampTrAccYnResVO> stampTrAccYnValueList = stampLogService.getUnionAllStampTrAndWinningDelivery(stampMain.getStpId(), stampMain.getStpAttendAuthCondition(), attendValue);

                int stampTotalCnt = 0;
                for (int j = 0; j < stampTrAccYnValueList.size(); j++) {
                    StampTrAccYnResVO vo = stampTrAccYnValueList.get(j);

                    //스탬프가 적립된 개수만 증가
                    if (PredicateUtils.isEqualsStr(vo.getTrStatus(), "O")) {
                        stampTotalCnt++;
                    }
                    if (j == (stampTrAccYnValueList.size() - 1)) {
                        cell = row.createCell(trStampTotalStartNum);
                        cell.setCellValue(stampTotalCnt);   //총 스탬프 개수
                    }

                    cell = row.createCell(trValueStartNum++);
                    cell.setCellValue(vo.getTrStatus());    //TR, 당첨 상품 개수
                }
            }
        }

        //=============================================== x-tran 전송 시작 ================================================== //

        //엑셀파일을 받을 담당자 목록 가져오기
        List<XtransReceiverEntity> receiverList = arEventService.findAllXtransReceiver();

        //담당자 목록이 정상적으로 있으면
        if (PredicateUtils.isNotNullList(receiverList)) {
            List<File> fileList = new ArrayList<>();
            for (XtransReceiverEntity entity : receiverList) {

                try {
                    String developType = "(개발)";
                    if (ProfileProperties.isAlp()) {
                        developType = "(알파)";
                    } else if (ProfileProperties.isProd()) {
                        developType = "(상용)";
                    }

                    String fileName = "스탬프 적립 통계" + developType;

                    //파일 생성 형식 : "서비스ID_사번_파일명"
                    File outputFile = File.createTempFile(xtransSftpEventId + "_" + entity.getEmployeeNumber() + "_" + eventId + fileName + DateUtils.returnNowDateByYyyymmddhhmmss(), ".xlsx");

                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        wb.write(fos);
                    } catch (FileNotFoundException fne) {
                        resultCode = ErrorCodeDefine.FILE_NOT_FOUND.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        break;
                    } catch (IOException ioe) {
                        resultCode = ErrorCodeDefine.IOE_ERROR.code();
                        log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                        break;
                    }
                    fileList.add(outputFile);
                } catch (IOException e) {
                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                    break;
                }
            }

            //x-tran 파일 전송하기
            Map<String, Boolean> sftpResultMap = xtransLogic.sendXtranFileList(fileList);

            //파일전송 시 에러 발생하면 에러처리
            if (PredicateUtils.isNotNull(sftpResultMap)) {
                Boolean isSftpError = sftpResultMap.get("error");
                if (PredicateUtils.isNotNull(isSftpError)) {
                    resultCode = ErrorCodeDefine.CONNECTION_TIMEOUT.code();
                    log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
                }
            }
        }
        //=============================================== x-tran 전송 끝  ================================================== //

        if (resultCode == 200) {
            commonValue = "2";  //성공
        } else {
            commonValue = "-1"; //실패
        }
        //제한테이블 상태 업데이트
        arEventService.updateCommonSettings(commonSettingsKey, commonValue);

        Map<String, Object>resultMap = new HashMap<>();
        resultMap.put("success", true);
        resultMap.put("resultCode", resultCode);
        return CompletableFuture.completedFuture(resultMap);
    }

    private List<String> makeDateList(String startDate, int dateCount) {
        List<String> makeDateList = new ArrayList<String>();
        for (int i = 0; i <= dateCount; i++) {
            String date = DateUtils.plusDay(startDate, "YYYY.MM.DD", i);
            makeDateList.add(date);
        }
        return makeDateList;
    }

    @GetMapping(value = "/test2/{eventId}/{count}")
    public void test2(@PathVariable("eventId") String eventId, @PathVariable("count") Integer count) throws Exception {
        SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("raw_table");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        //제목 cell 스타일
        CellStyle headerCellStyle = ExcelCellStyle.getCellStyle(
                wb, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, IndexedColors.GREY_50_PERCENT.getIndex(), IndexedColors.WHITE.getIndex(), true
        );
        //문항 cell 스타일
        CellStyle subjectCellStyle = ExcelCellStyle.getCellStyle(
                wb, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex(), (short) 0, false
        );
        //내용 cell 스타일
        CellStyle exampleCellStyle = ExcelCellStyle.getCellStyle(
                wb, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, (short) 0, (short) 0, false
        );

        ArEventEntity arEvent = arEventService.findArEventByEventId(eventId);
        List<String> headerTitleList = surveyGoStaticsService.makeSurveyRawTableTitle(arEvent.getArEventId());

        row = sheet.createRow(rowNum++);
        for (int i=0; i<headerTitleList.size(); i++) {
            String title = headerTitleList.get(i);
            cell = row.createCell(i);
            cell.setCellValue(title);
            cell.setCellStyle(subjectCellStyle);
        }

        List<Map<String,String>>list = surveyGoStaticsService.getSurveyAnswerStaticsListByEventId(eventId);

        if (PredicateUtils.isNotNullList(list)) {
            for (int j = 0; j < list.size(); j++) {
                row = sheet.createRow(rowNum++);

                Map<String,String> answer = list.get(j);

                for (int k = 0; k < headerTitleList.size(); k++) {
                    cell = row.createCell(k);
                    if (k == 0) {
                        cell.setCellValue(StringTools.convertNullToEmptyString(answer.get("survey_log_attend_id")));
                    } else {
                        cell.setCellValue(StringTools.convertNullToEmptyString(String.valueOf(answer.get("answer_" + (k)))));
                    }
                    cell.setCellStyle(exampleCellStyle);
                }
            }
        }
        File outputFile = File.createTempFile("test1234", ".xlsx");

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            wb.write(fos);
        } catch (FileNotFoundException fne) {
            log.error(fne.getMessage());
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
        FileUtils.fileMove(outputFile.toString(), "/Users/anjiho/Downloads/test1234.xlsx");

    }
}
