package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StampEventLogTrVO implements Serializable {

    private static final long serialVersionUID = -7347757739452735714L;

    private String stpEventLogTrId;

    private Long id;

    private int stpId;

    private String eventId;

    private String stpTrPid;

    private int stpPanId;

    private Long stpPanTrId;    //2024.04.16 int > Long 변경 작성자 : 안지호

    private int stpTrSort;

    private String stpAttendAuthCondition;

    private int arEventWinningId;

    private String attendValue;

    private int isClick;
}
