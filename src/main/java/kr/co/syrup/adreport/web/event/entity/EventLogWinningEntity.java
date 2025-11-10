package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "EVENT_LOG_WINNING")
public class EventLogWinningEntity implements Serializable {

    private static final long serialVersionUID = -6566942755108729240L;

    // 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이벤트 아이디
    private String eventId;

    // AR 이벤트 아이디
    private Integer arEventId;

    private Integer arEventObjectId;

    // 이벤트 당첨 정보 아이디
    private Integer arEventWinningId;

    // 당첨자 정보 설정 넘버
    private Integer eventWinningSort;

    // 당첨 타입  값(기프티콘, 기타, 꽝)
    private String winningType;

    // 참여코드
    private String attendCode;

    //경품정보 입력 테이블 인덱스
    private Integer giveAwayId;

    //서베이고 유형 인덱스
    private Long surveySubjectCategoryId;

    //핸드폰번호
    private String phoneNumber;

    // 생성일자(yyyy-mm-dd)
    private String createdDay;

    // 생성 시(hh)
    private String createdHour;

    // 생성일(yyyy-mm-dd hh:mm:ss)
    private Date createdDate;

    private Integer createdDayImpr;

    private Integer createdHourImpr;

    public static EventLogWinningEntity saveOf(EventWinningReqDto reqDto, ArEventWinningEntity winningEntity) {
        return new EventLogWinningEntity().builder()
                .eventId(reqDto.getEventId())
                .arEventId(winningEntity.getArEventId())
                .arEventObjectId(reqDto.getArEventObjectId())
                .arEventWinningId(winningEntity.getArEventWinningId())
                .eventWinningSort(winningEntity.getEventWinningSort())
                .winningType(winningEntity.getWinningType())
                .attendCode(PredicateUtils.isNull(reqDto.getAttendCode()) ? "" : reqDto.getAttendCode() )
                .surveySubjectCategoryId(PredicateUtils.isNull(reqDto.getSurveySubjectCategoryId()) ? null : reqDto.getSurveySubjectCategoryId())
                .phoneNumber(PredicateUtils.isNull(reqDto.getPhoneNumber()) ? "" : reqDto.getPhoneNumber())
                .createdDay(DateUtils.getNow("yyyy-MM-dd"))
                .createdHour(DateUtils.getNowHour())
                .createdDate(DateUtils.returnNowDate())
                .createdDayImpr(Integer.parseInt(DateUtils.getNowYYMMDD()))
                .createdHourImpr(Integer.parseInt(DateUtils.getNowYYMMDDHH()))
                .build();
    }
}
