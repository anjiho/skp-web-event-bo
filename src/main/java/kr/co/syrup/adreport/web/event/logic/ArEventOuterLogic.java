package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.AttendCodeUseVO;
import kr.co.syrup.adreport.stamp.event.service.StampFrontService;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.CommonLogPersonAgreeReqDto;
import kr.co.syrup.adreport.web.event.dto.request.CommonLogPvReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.mybatis.vo.AttendCodeHistorySearchMapperVO;
import kr.co.syrup.adreport.web.event.service.ArEventOuterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class ArEventOuterLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private ArEventOuterService arEventOuterService;

    @Autowired
    private StampFrontService stampFrontService;

    @Autowired
    private AES256Utils aes256Utils;

    /**
     * 서버 라이브 체크
     * @return
     */
    public Object getPRTGMonitoringLogic() {
        String resultStr = "";

        Date now = arEventOuterService.getNowFromDB();

        if (PredicateUtils.isNull(now)) {
            resultStr = "FAIL";
        }
        if (PredicateUtils.isNotNull(now)) {
            resultStr = "OK";
        }
        return  resultStr;
    }

    /**
     * 공통 PV 로그 저장하기 로직 (외부 특정 이벤트만 사용)
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto saveCommonLogPvLogic(CommonLogPvReqDto reqDto) {
        if (PredicateUtils.isNotNull(reqDto.getEventName()) && PredicateUtils.isNotNull(reqDto.getLogKey())) {
            arEventOuterService.saveCommonLogPv(reqDto.getEventName(), reqDto.getLogKey());
        }
        return new ApiResultObjectDto().builder()
                .result(reqDto.getLogKey())
                .resultCode(httpSuccessCode)
                .build();

    }

    /**
     * 공통 개인정보 활용 동의 로그 저장하기 로직 (외부 특정 이벤트만 사용)
     * @param reqDto
     * @return
     */
    public ApiResultObjectDto saveCommonLogPersonAgreeLogic(CommonLogPersonAgreeReqDto reqDto) {
        int resultCode = httpSuccessCode;

        if (StringUtils.isEmpty(reqDto.getAgreeId()) || PredicateUtils.isNull(reqDto.getAgreeId())) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
        } else {
            //아이디 중복 여부 확인
            boolean isDuplicate = arEventOuterService.isDuplicateCommonLogPersonAgree(reqDto.getEventName(), reqDto.getAgreeId(), reqDto.getPhoneNumber());
            //아이디 중복일때 에러코드 처리
            if (isDuplicate) {
                resultCode = ErrorCodeDefine.CUSTOM_ERROR_COMMON_LOG_PERSON_AGREE_ID_DUPLICATE.code();
            } else {
                try {
                    arEventOuterService.saveCommonLogPersonAgree(reqDto.getEventName(), reqDto.getAgreeId(), reqDto.getPhoneNumber());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    resultCode = ErrorCodeDefine.IOE_ERROR.code();
                }
            }
        }

        return new ApiResultObjectDto().builder()
                .result(reqDto.getAgreeId())
                .resultCode(resultCode)
                .build();

    }

    /**
     * AR 이벤트 참여코드 사용 기록 정보 가져오기
     * @param eventId
     * @param attendCode
     * @return
     */
    public ApiResultObjectDto getAttendCodeHistoryLogic(String eventId, String attendCode) {
        int resultCode = httpSuccessCode;

        List<AttendCodeHistorySearchMapperVO> searchList = new ArrayList<>();
        if (PredicateUtils.isNull(eventId) || PredicateUtils.isNull(attendCode)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        } else {
            searchList = arEventOuterService.selectAttendCodeHistoryByEventIdAndAttendCode(eventId, attendCode);

            if (!PredicateUtils.isNullList(searchList)) {

                searchList.forEach(data -> {
                    //이름 암복화 여부 확인 후 암호화가 되어있으면 복호화작업
                    if (PredicateUtils.isNotNull(data.getName())) {
                        //암호화가 되었을때
                        if (data.getName().length() > 20) {
                            data.setName(aes256Utils.decrypt(data.getName()));
                        }
                    }
                    //전화번호 암복화 여부 확인 후 암호화가 되어있으면 복호화작업
                    if (PredicateUtils.isNotNull(data.getPhoneNumber())) {
                        //암호화가 되었을때
                        if (data.getPhoneNumber().length() > 15) {
                            String decryptPhoneNumber = aes256Utils.decrypt(data.getPhoneNumber());
                            int strSize = decryptPhoneNumber.length();
                            int cutLen = 4;
                            //뒤에서 4자리 찾아서 보여준다
                            data.setPhoneNumber(decryptPhoneNumber.substring(strSize - cutLen));
                        }
                    }
                    //당첨인데 경품정보 입력 여부 확인
                    if (!PredicateUtils.isEqualsStr(WinningTypeDefine.꽝.code(), data.getWinningType())) {
                        //당첨인데 경품정보 입력 정보가 없으면 false
                        if (PredicateUtils.isNull(data.getGiveAwayId()) || PredicateUtils.isEqualZero(data.getGiveAwayId())) {
                            data.setStatus("입력안함");
                        } else {
                            data.setStatus("입력");
                        }
                    } else if (PredicateUtils.isEqualsStr(WinningTypeDefine.꽝.code(), data.getWinningType())) {
                        data.setStatus("입력안함");
                    }
                });
            }
        }
        return new ApiResultObjectDto().builder()
                .result(searchList)
                .resultCode(resultCode)
                .build();
    }

    /**
     * 스탬프 이벤트 참여코드 사용 기록 정보 가져오기
     * @param eventId
     * @param attendCode
     * @return
     */
    public ApiResultObjectDto getStampAttendCodeHistoryLogic(String eventId, String attendCode) {
        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(eventId) || PredicateUtils.isNull(attendCode)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
        }
        List<AttendCodeUseVO> useList = stampFrontService.findAttendCodeUseListByEventIdAndAttendCode(eventId, attendCode);
        return new ApiResultObjectDto().builder()
                .result(useList)
                .resultCode(resultCode)
                .build();
    }
}
