package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WinningHourlyMapperVO implements Serializable {

    private static final long serialVersionUID = -7776149115191063568L;

    private Integer hour;

    private String cnt;

    private Integer totalCnt;

}
