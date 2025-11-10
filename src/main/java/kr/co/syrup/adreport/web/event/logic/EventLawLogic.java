package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import kr.co.syrup.adreport.web.event.service.EventLawService;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Repository
public class EventLawLogic {

    private final int httpSuccessCode = HttpStatus.OK.value();

    @Autowired
    private EventLawService eventLawService;

    /**
     * 법률정보 가져오기
     * @param lawType
     * @return
     */
    public ApiResultObjectDto getEventLawInfoListLogic(String lawType) {
        return new ApiResultObjectDto().builder()
                .result(eventLawService.getEventLawInfoListByLawType(lawType))
                .resultCode(httpSuccessCode)
                .build();
    }

    /**
     * 법률정보 저장 (실제로 사용은 안함 - 에디터 테스트용)
     * @param lawType
     * @param contents
     * @param startDate
     * @param endDate
     * @return
     */
    @Transactional
    public ApiResultObjectDto saveEventLawInfoLogic(String lawType, String contents, Date startDate, Date endDate) {
        if (PredicateUtils.isNull(lawType) || PredicateUtils.isNull(contents) || PredicateUtils.isNull(startDate) || PredicateUtils.isNull(endDate)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }

        Integer insertedIdx = eventLawService.saveEventLawInfo(lawType, contents, startDate, endDate);

        HashMap<String, Integer>resultMap = new HashMap<>();
        resultMap.put("idx", insertedIdx);

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(httpSuccessCode)
                .build();
    }

    /**
     * 법률정보 업데이트 (실제로 사용은 안함 - 에디터 테스트용)
     * @param idx
     * @param lawType
     * @param contents
     * @param startDate
     * @param endDate
     * @return
     */
    @Transactional
    public ApiResultObjectDto updateEventLawInfoLogic(Integer idx, String lawType, String contents, Date startDate, Date endDate) {
        if (PredicateUtils.isNull(idx)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        eventLawService.updateEventLawInfo(idx, lawType, contents, startDate, endDate);

        HashMap<String, Integer>resultMap = new HashMap<>();
        resultMap.put("idx", idx);

        return new ApiResultObjectDto().builder()
                .result(resultMap)
                .resultCode(httpSuccessCode)
                .build();
    }

    /**
     * 법률정보 삭제 (실제로 사용은 안함 - 에디터 테스트용)
     * @param idx
     * @return
     */
    @Transactional
    public ApiResultObjectDto deleteEventLawInfoLogic(Integer idx) {
        if (PredicateUtils.isNull(idx)) {
            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR.getDesc(), ResultCodeEnum.CUSTOM_ERROR_PARAM_ERROR);
        }
        eventLawService.deleteEventLawInfo(idx);
        return new ApiResultObjectDto().builder()
                .result(null)
                .resultCode(httpSuccessCode)
                .build();
    }
}
