package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StpPanTrRowNumByWinningVO implements Serializable {

    private static final long serialVersionUID = -806447578583961778L;

    private Long stpPanTrId;

    private Integer rowNum;
}
