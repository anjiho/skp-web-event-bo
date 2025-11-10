package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidateAttendCodeReqDto implements Serializable {

    private static final long serialVersionUID = 1756161159605864724L;

    private String eventId;

    private String attendCode;

    private String phoneNumber;
}
