package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpsertStampReqDto implements Serializable {

    private static final long serialVersionUID = -2771593233314627238L;

    private Integer id;

    private String key;

    private String value;
}
