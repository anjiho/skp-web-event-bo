package kr.co.syrup.adreport.web.event.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SodaApiResultObjectDto implements Serializable {

    private static final long serialVersionUID = -6925657529458741959L;

    private String result;

    private String message;

    @Builder
    public SodaApiResultObjectDto(int resultCode) {
        this.result = resultCode == 200 ? "success" : "fail";
        this.message = resultCode == 200 ? "등록 성공" : "등록시 오류가 발생하였습니다.";
    }

}
