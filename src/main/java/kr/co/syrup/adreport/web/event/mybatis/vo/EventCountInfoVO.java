package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventCountInfoVO implements Serializable {

    private static final long serialVersionUID = 584329334170641886L;

    private String countName;

    private Long count;
}
