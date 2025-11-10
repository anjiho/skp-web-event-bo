package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventLogWinningLimitMapperVO implements Serializable {

    private static final long serialVersionUID = -4871542850366259586L;

    private String code;

    private Boolean status;

    private String codeDesc;
}
