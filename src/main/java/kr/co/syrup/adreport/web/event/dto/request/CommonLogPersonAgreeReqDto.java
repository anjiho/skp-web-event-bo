package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonLogPersonAgreeReqDto {
    @NotNull(message = "eventName이(가) 없습니다.")
    private String eventName;

    private String agreeId = "";

    private String phoneNumber = "";
}
