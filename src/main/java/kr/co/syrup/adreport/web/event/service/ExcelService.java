package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.*;
import kr.co.syrup.adreport.web.event.dto.response.AttendCodeValidateResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventGateCodeEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftTokenInfoEntity;
import kr.co.syrup.adreport.web.event.entity.repository.ArEventEntityRepository;
import kr.co.syrup.adreport.web.event.entity.repository.ArEventGateCodeEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ExcelService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ArEventService arEventService;

    /**
     * 참여코드 중복여부 체크
     * @param excelFile
     * @return
     */
    public AttendCodeValidateResDto isValidationAttendCodeByExcelFile(final MultipartFile excelFile) {
        boolean isDuplicate = false;
        long attendCodeCount = 0;
        long duplicateCount = 0;

        String path = request.getSession().getServletContext().getRealPath("/");

        try {
            File destFile = new File(path + excelFile.getOriginalFilename());
            excelFile.transferTo(destFile);

            ExcelReadOption excelReadOption = new ExcelReadOption();
            excelReadOption.setFilePath(path + destFile.getName());
            log.info("================ 파일명 ::: " + destFile.getName() + " =====================");
            excelReadOption.setOutputColumns("A");
            excelReadOption.setStartRow(2);

            List<Map<String, Object>> excelContentList = ExcelRead.read(excelReadOption);
            //참여코드 개수 주입
            int excelCodeSize = excelContentList.size();

            log.info("================ 엑셀 파일 추출 끝 =====================");
            //엑셀 파일 중복된 맥 어드레스가 있는지 확인
            boolean isDuplicatedExcelContentList = excelContentList.stream()
                    .map(excel->excel.get("A"))
                    .distinct()
                    .count() != excelContentList.size();
            //중복된 데이터가 있을때
            if (isDuplicatedExcelContentList) {
                log.error("=============== 참여코드 중복 ==================");

                isDuplicate = true;

                attendCodeCount = excelContentList
                        .stream()
                        .map(excel -> excel.get("A"))
                        .distinct()
                        .count();

                duplicateCount = (excelCodeSize - attendCodeCount);
            } else {
            //중복된 데이터가 없을때
                attendCodeCount = excelCodeSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.fileDelete(path + excelFile.getOriginalFilename());
            log.info("================ 엑셀 파일 삭제 =====================");
        }
        return new AttendCodeValidateResDto().builder()
                .duplicateYn(isDuplicate)   //중복여부
                .totalCount(attendCodeCount + duplicateCount)
                .attendCodeCount(attendCodeCount)   //중복되지 않는 참여코드 개수
                .duplicateCodeCount(duplicateCount) //중복된 참여코드 개수
                .build();
    }

    @LoggingTimeFilter
    public List<Map<String, Object>> extractionAttendCodeByExcelFile(final MultipartFile excelFile, String extractionType) {
        List<Map<String, Object>> attendCodeList = new ArrayList<>();
        String path = request.getSession().getServletContext().getRealPath("/");

        try {
            File destFile = new File(path + excelFile.getOriginalFilename());
            excelFile.transferTo(destFile);

            ExcelReadOption excelReadOption = new ExcelReadOption();
            excelReadOption.setFilePath(path + destFile.getName());
            log.info("================ 파일명 ::: " + destFile.getName() + " =====================");

            excelReadOption.setStartRow(2);
            if (PredicateUtils.isEqualsStr(extractionType, "ATTEND")) {
                excelReadOption.setOutputColumns("A");
                attendCodeList = ExcelRead.read(excelReadOption);
            }
            if (PredicateUtils.isEqualsStr(extractionType, "NFT")) {
                excelReadOption.setOutputColumns("B");
                attendCodeList = ExcelRead.read(excelReadOption);
            }

            //excelReadOption.setStartRow(2);

            log.info("================ 엑셀 파일 추출 끝 =====================");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.fileDelete(path + excelFile.getOriginalFilename());
            log.info("================ 엑셀 파일 삭제 =====================");
        }
        return attendCodeList;
    }

    public String extractionNftIdByExcelFile(MultipartHttpServletRequest request) {
        String fileName = "";
        String deleteFileName = "";
        Iterator<String> it = request.getFileNames();
        String path = request.getSession().getServletContext().getRealPath("/");

        while (it.hasNext()) {
            String uploadFileName = it.next();
            if (PredicateUtils.isNotNull(uploadFileName)) {
                try {
                    List<Map<String, Object>> nftIdMapList = new ArrayList<>();
                    MultipartFile multipartFile = request.getFile(uploadFileName);

                    File destFile = new File(path + multipartFile.getOriginalFilename());
                    multipartFile.transferTo(destFile);

                    String originalFileName = multipartFile.getOriginalFilename();
                    deleteFileName = originalFileName;
                    //파일명 변경
                    int filePos = originalFileName.lastIndexOf(".");
                    String fileExtension = originalFileName.substring(filePos+1);
                    //전달해줄 파일명 생성
                    String finalFileName = "token_info" + "_" + DateUtils.returnNowDateByYyyymmddhhmmssMilliSecond() + "." + fileExtension;

                    log.info("finalFileName {} " + finalFileName);
                    log.info("originalFileName {} " + originalFileName);

                    fileName = finalFileName;

                    //엑셀 파일 추출 시작
                    ExcelReadOption excelReadOption = new ExcelReadOption();
                    excelReadOption.setFilePath(path + destFile.getName());

                    log.info("================ 파일명 ::: " + originalFileName + " =====================");

                    excelReadOption.setOutputColumns("A");
                    excelReadOption.setStartRow(2);

                    nftIdMapList = ExcelRead.read(excelReadOption);
                    //엑셀 파일 추출 끝

                    //ar_event_nft_token_info 테이블 저장 시작
                    if (PredicateUtils.isNotNull(nftIdMapList)) {
                        List<ArEventNftTokenInfoEntity> tokenInfoEntityList = new ArrayList<>();
                        for (Map<String, Object> excelMap : nftIdMapList) {
                            tokenInfoEntityList.add(ArEventNftTokenInfoEntity.excelUploadOf(excelMap.get("A").toString(), fileName));
                        }
                        try {
                            arEventService.saveAllArEventNftTokenInfo(tokenInfoEntityList);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    //ar_event_nft_token_info 테이블 저장 끝
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    FileUtils.fileDelete(path + deleteFileName);
                    log.info("================ 엑셀 파일 삭제 =====================");
                }
                log.info("================ 엑셀 파일 추출 끝 =====================");
            }
        }
        return fileName;
    }
}
