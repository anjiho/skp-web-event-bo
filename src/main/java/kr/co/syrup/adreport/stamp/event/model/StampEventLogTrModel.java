package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventLogTrModel implements Serializable {

    private static final long serialVersionUID = -2505098518338907843L;

    private Long id;

    private Integer stpId;

    private String eventId;

    private String stpTrPid;

    private Integer stpPanId;

    private Long stpPanTrId;

    private Integer stpTrSort;

    private String stpAttendAuthCondition;

    private Integer arEventWinningId;

    private String attendValue;

    private Boolean isClick;

    private Integer createdDayImpr;

    private Integer createdHourImpr;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;
}
