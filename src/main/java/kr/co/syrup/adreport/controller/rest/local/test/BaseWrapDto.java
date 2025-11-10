package kr.co.syrup.adreport.controller.rest.local.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseWrapDto implements Serializable {

    private static final long serialVersionUID = -1568151441041088866L;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private String createdBy;

    private Date modifiedDate;

    private String modifiedBy;
}
