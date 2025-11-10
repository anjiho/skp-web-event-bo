package kr.co.syrup.adreport.stamp.event.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StampEventLogAttendSortModel implements Serializable {

    private static final long serialVersionUID = 4173547774365414993L;

    private String eventId;

    private Integer stpId;

    private Integer stpPanId;

    private Long stpPanTrId;

    private Integer stpTrSort;

    private String attendType;

    private String attendValue;

    private Date createdDate;
}
