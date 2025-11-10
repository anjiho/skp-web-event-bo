package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventGateCodeAtUsedMapperVO implements Serializable {

    private static final long serialVersionUID = 3008751530172131026L;

    private String attendCode;

    private int usedCount;

    private int cnt;

    private Boolean isUse;
}
