package kr.co.syrup.adreport.model.adreport;

import io.swagger.annotations.ApiModel;
import kr.co.syrup.adreport.framework.common.BaseWrapResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.RandomStringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Data
@ApiModel(value = "광고 리포팅 response 래핑 DTO")
@EqualsAndHashCode(callSuper = false)
public class AdreportResponseWrapDto extends BaseWrapResponse implements Serializable {
    private static final long serialVersionUID = 7679089930834355342L;

    private String traceNo;

    public AdreportResponseWrapDto() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        traceNo = dateFormat.format(calendar.getTime()) + RandomStringUtils.randomNumeric(3);
    }
}
