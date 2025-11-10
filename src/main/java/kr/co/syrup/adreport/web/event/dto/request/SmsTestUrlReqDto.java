package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SmsTestUrlReqDto implements Serializable {

    private String eventId;

    private String phoneNumber;
}
