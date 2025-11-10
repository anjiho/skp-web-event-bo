package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventGiveAwayDeliveryButtonAddMapperVO implements Serializable {

    private static final long serialVersionUID = 9153703439040834553L;

    private Long arEventWinningButtonAddId;

    private String fieldName;

    private Integer giveAwayId;

    private String fieldValue;
}
