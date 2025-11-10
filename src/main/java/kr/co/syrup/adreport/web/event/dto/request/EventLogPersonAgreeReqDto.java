package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventLogPersonAgreeReqDto {

    @NotNull(message = "이벤트아이디가 없습니다.")
    private String eventId;
    
    @NotNull(message = "동의 아이디가 없습니다.")
    private String agreeId = "";

    private String phoneNumber = "";
}
