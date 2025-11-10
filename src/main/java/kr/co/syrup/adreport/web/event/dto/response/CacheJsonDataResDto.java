package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheJsonDataResDto implements Serializable {

    private static final long serialVersionUID = -882987767936228466L;

    private Long id;

    private String jsonValue;
}
