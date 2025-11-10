package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HourlyMapperVO implements Serializable {

    private static final long serialVersionUID = -8619618932644517624L;

    private String hour;

    private int time;

    private int count;
}
