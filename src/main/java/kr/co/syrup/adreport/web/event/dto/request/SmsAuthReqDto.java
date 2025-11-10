package kr.co.syrup.adreport.web.event.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
public class SmsAuthReqDto implements Serializable {

    private static final long serialVersionUID = -5623225716440839506L;

    private String smsAuthCode;

    private String eventId;

    private String phoneNumber;

    private String authMenuType;

    private String authCode;
}
