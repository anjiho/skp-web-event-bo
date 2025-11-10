package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.UpsertStampReqDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.SpotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Slf4j
@Repository
public class SpotServiceLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private ArEventService arEventService;

    @Autowired
    private SpotService spotService;

    public ApiResultObjectDto getStampEventLogic(String key) {
        int resultCode = httpSuccessCode;

        //조회 키 가 없으면 에러처리
        if (PredicateUtils.isNull(key)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        CommonSettingsEntity commonSettingsEntity = new CommonSettingsEntity();
        if (PredicateUtils.isNotNull(key)) {
            commonSettingsEntity = arEventService.findCommonSettingsBySettingKey(key);
        }
        return new ApiResultObjectDto().builder()
                .result(commonSettingsEntity)
                .resultCode(resultCode)
                .build();
    }

    @Transactional
    public ApiResultObjectDto upsertStampEventKeyLogic(UpsertStampReqDto reqDto) {
        int resultCode = httpSuccessCode;

        if (PredicateUtils.isNull(reqDto)) {
            resultCode = ErrorCodeDefine.CUSTOM_ERROR_PARAM_ERROR.code();
            log.error(ErrorCodeDefine.getLogErrorMessage(resultCode));
        }

        if (PredicateUtils.isNotNull(reqDto)) {
            //업데이트
            if (PredicateUtils.isNotNull(reqDto.getId())) {
                try {
                    arEventService.updateCommonSettings(reqDto.getKey(), reqDto.getValue());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            //저장
            if (PredicateUtils.isNull(reqDto.getId())) {
                try {
                    spotService.saveCommonSettings(reqDto.getKey(), reqDto.getValue());
                } catch (DataIntegrityViolationException dve) {
                    //키값 중복 에러
                    throw new BaseException(ResultCodeEnum.DUPLICATE_UNIQUE_KEY.getDesc(), ResultCodeEnum.DUPLICATE_UNIQUE_KEY);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return new ApiResultObjectDto().builder()
                .result(null)
                .resultCode(resultCode)
                .build();
    }
}
