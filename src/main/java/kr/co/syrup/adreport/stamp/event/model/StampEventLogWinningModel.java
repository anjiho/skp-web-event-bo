package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//스탬프 교환형 당첨 로그
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventLogWinningModel implements Serializable {

    private static final long serialVersionUID = -6089959558952120008L;

    // 인덱스
    private Long id;

    private Integer stpId;

    private Long stpPanTrId;

    // 당첨 정보 인덱스
    private Integer arEventWinningId;

    private Integer eventWinningSort;

    private String winningType;

    // 사용자 참여 종류(핸드폰, 참여코드)
    private String attendType;

    // 사용자 참여 값(핸드폰번호 또는 참여코드 값)
    private String attendValue;

    //스탬프 당첨(당첨없음, 교환형, 추첨형)
    private String stpWinningType;

    //스탬프 당첨 입력 테이블 인덱스
    private Long stpGiveAwayId;

    // 생성일자(yyyy-mm-dd)
    private String createdDay;

    // 생성 시(hh)
    private String createdHour;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // Ex)220901(yymmdd)
    private Integer createdDayImpr;

    // Ex) 22090105(yymmddhh)
    private Integer createdHourImpr;

    @Builder
    public StampEventLogWinningModel(int stpId, long stpPanTrId, int arEventWinningId, int eventWinningSort, String winningType, String attendType, String attendValue, String stpWinningType) {
        this.stpId = stpId;
        this.stpPanTrId = stpPanTrId;
        this.arEventWinningId = arEventWinningId;
        this.eventWinningSort = eventWinningSort;
        this.winningType = winningType;
        this.attendType = attendType;
        this.attendValue = attendValue;
        this.stpWinningType = stpWinningType;
        this.createdDay = DateUtils.getNow("yyyy-MM-dd");
        this.createdHour = DateUtils.getNowHour();
        this.createdDayImpr = Integer.parseInt(DateUtils.getNowYYMMDD());
        this.createdHourImpr = Integer.parseInt(DateUtils.getNowYYMMDDHH());
    }

    public static StampEventLogWinningModel ofAllCount(int stpId, int eventWinningSort) {
        StampEventLogWinningModel model = new StampEventLogWinningModel();
        model.setStpId(stpId);
        model.setEventWinningSort(eventWinningSort);
        return model;
    }

    public static StampEventLogWinningModel ofCount(int stpId, int eventWinningSort, String attendType, String attendValue) {
        StampEventLogWinningModel model = new StampEventLogWinningModel();
        model.setStpId(stpId);
        model.setEventWinningSort(eventWinningSort);
        model.setAttendType(attendType);
        model.setAttendValue(attendValue);
        return model;
    }

    public static StampEventLogWinningModel ofCount(int stpId, int eventWinningSort, String attendType, String attendValue, Integer createdDayImpr) {
        StampEventLogWinningModel model = new StampEventLogWinningModel();
        model.setStpId(stpId);
        model.setEventWinningSort(eventWinningSort);
        model.setAttendType(attendType);
        model.setAttendValue(attendValue);
        if (PredicateUtils.isNotNull(createdDayImpr)) {
            model.setCreatedDayImpr(createdDayImpr);
        }
        return model;
    }

    public static StampEventLogWinningModel ofCount(int stpId, int eventWinningSort, Integer createdDayImpr, Integer createdHourImpr) {
        StampEventLogWinningModel model = new StampEventLogWinningModel();
        model.setStpId(stpId);
        model.setEventWinningSort(eventWinningSort);
        if (PredicateUtils.isNotNull(createdDayImpr)) {
            model.setCreatedDayImpr(createdDayImpr);
        }
        if (PredicateUtils.isNotNull(createdHourImpr)) {
            model.setCreatedHourImpr(createdHourImpr);
        }
        return model;
    }
}
