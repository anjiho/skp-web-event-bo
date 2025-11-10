package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ContractInfoReqDto implements Serializable {

    private static final long serialVersionUID = 8300145433735932585L;
    
    private String contractNo;

    private String contractDate;

    private String contractStatus;

    private String userId;

}
