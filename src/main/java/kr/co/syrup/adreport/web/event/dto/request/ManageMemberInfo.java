package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ManageMemberInfo implements Serializable {

    private static final long serialVersionUID = -3512494868205252522L;

    private String createdBy;

    private String lastModifiedBy;
}
