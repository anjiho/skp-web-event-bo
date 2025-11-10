package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StampTrAccYnResVO implements Serializable {

    private static final long serialVersionUID = -5511385394804891815L;

    private Long stpPanTrId;

    private String stpTrTxt;

    private Integer stpTrSort;

    private String trStatus;
}
