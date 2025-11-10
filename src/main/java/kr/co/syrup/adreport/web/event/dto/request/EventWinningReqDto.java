package kr.co.syrup.adreport.web.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class EventWinningReqDto implements Serializable {

    private static final long serialVersionUID = 7499619725009762407L;

    @ApiModelProperty(value = "이벤트 ID - 필수")
    private String eventId;

    @ApiModelProperty(value = "ar 이벤트 오브젝트 ID - ar 구동인 경우 필수")
    private Integer arEventObjectId;

    @ApiModelProperty(value = "이벤트 참여조건이 참여코드인 경우 필수")
    private String attendCode;

    @ApiModelProperty(value = "이벤트 참여조건이 휴대폰경우인 경우 필수")
    private String phoneNumber;

    @ApiModelProperty(value = "서베이고 로그 참여 ID - 서베이고인 경우 필수")
    private String surveyLogAttendId;

    private Long surveySubjectCategoryId;

    @ApiModelProperty(value = "OCB 파트너 토큰 - OCB 인 경우 필수")
    private String partnerToken;

    @ApiModelProperty(value = "성명 - OCB 인 경우 필수")
    private String name;

    @ApiModelProperty(value = "스탬프 판 TR ID - 스탬프 이벤트인 경우 필수")
    private Long stpPanTrId;

    private String traceNo;

    @ApiModelProperty(value = "스탬프 이벤트 로그 TR ID (하위이벤트에서 스탬프찍혔는지 확인하기 위해 필요) - 스탬프 이벤트인 경우 필수")
    private Long stpEventLogTrId;

    @ApiModelProperty(value = "스탬프 참여조건 - 스탬프 이벤트인 경우 필수")
    private String stpAttendAuthCondition; // **

    @Nullable
    private String attendValue;

    @ApiModelProperty(value = "스탬프 당첨 시도 순서")
    private Integer winningAttemptOrder;

}
