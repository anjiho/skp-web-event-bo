package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class FutureSenseApiResDto {

    private Boolean success;

    private List<Object> response;

    private Object error;

}
