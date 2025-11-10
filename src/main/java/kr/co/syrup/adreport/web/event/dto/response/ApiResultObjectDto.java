package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.define.ErrorCodeDefine;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiResultObjectDto implements Serializable {

    private static final long serialVersionUID = 3799147531985378033L;

    private int resultCode;

    private Object result;

    private String resultMessage;

    private String traceNo;

    @Builder
    public ApiResultObjectDto(Object result, int resultCode) {
        this.resultCode = resultCode;
        this.result = result == null ? "" : result;
        this.resultMessage = ErrorCodeDefine.getEventErrorMessage(resultCode) == null ? "SUCCESS" : ErrorCodeDefine.getEventErrorMessage(resultCode);
    }

    public ApiResultObjectDto(Object result, int resultCode, String traceNo) {
        this.resultCode = resultCode;
        this.result = result == null ? "" : result;
        this.resultMessage = ErrorCodeDefine.getEventErrorMessage(resultCode) == null ? "SUCCESS" : ErrorCodeDefine.getEventErrorMessage(resultCode);
        this.traceNo = StringUtils.isEmpty(traceNo) ? traceNo() : traceNo;
    }

    private String traceNo() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(calendar.getTime()) + RandomStringUtils.randomNumeric(3);
    }

}
