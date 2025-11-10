package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Positive;
import java.io.Serializable;

@Setter
@Getter
public class EventAttendTimeDto implements Serializable {

    private static final long serialVersionUID = -3123730741476276714L;

    // 참여시간 설정(시작)
    private Integer attendStartHour;

    // 참여시간 설정(종료)
    private Integer attendEndHour;
}
