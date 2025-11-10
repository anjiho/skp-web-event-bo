package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContractModifyReqDto implements Serializable {

    private static final long serialVersionUID = 9221907689376104486L;

    private String marketingId;

    private String serviceSolutionId;

    private String contractStatus;

    private String realServiceEndDate;

}
