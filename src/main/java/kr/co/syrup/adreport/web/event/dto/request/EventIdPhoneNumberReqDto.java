package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventIdPhoneNumberReqDto implements Serializable {

    private static final long serialVersionUID = 8036599619188512400L;

    private String eventId;

    private String receiverPhoneNumber;

    private String userPhoneNumber;

    private String attendCode;

    private String winningType;

    private String stampEventIds;
}
