package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StampSortAttendSortYnResVO implements Serializable {

    private static final long serialVersionUID = -9066927707171066039L;

    private String stampEventId;

    private String stpMainSettingYn;

    private Integer stpId;

    private Integer stpPanId;

    private Long stpPanTrId;

    private Integer stpTrSort;

    private String attendSortSettingYn;

    private String stpAttendAuthCondition;

    private String firstStampTrEventId;

    private String stampPanUrl;

}
