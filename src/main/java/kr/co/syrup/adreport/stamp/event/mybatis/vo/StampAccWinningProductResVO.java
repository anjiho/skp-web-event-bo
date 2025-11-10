package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StampAccWinningProductResVO implements Serializable {

    private static final long serialVersionUID = -1113568123299029333L;

    private String productName;

    private Integer cnt;
}
