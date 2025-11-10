package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SmsSendReqDto implements Serializable {

    private static final long serialVersionUID = 185506218007874693L;

    //이름
    private String name;

    //SMS 발송 아이디
    private String smsId;

    //SMS 수신 핸드폰번호
    private String mdn;


}
