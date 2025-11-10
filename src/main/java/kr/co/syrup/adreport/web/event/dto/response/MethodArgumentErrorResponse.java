package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodArgumentErrorResponse {

    private String resultCode;

    private String description;

    private String resultMessage;

    public MethodArgumentErrorResponse(String resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    public MethodArgumentErrorResponse(String resultCode, String description, String resultMessage) {
        this.resultCode = resultCode;
        this.description = description;
        this.resultMessage = resultMessage;
    }
}
