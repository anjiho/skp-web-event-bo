package kr.co.syrup.adreport.web.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GiveAwayDeliverySaveReqDto implements Serializable {

    private static final long serialVersionUID = -611174404332699316L;

    @ApiModelProperty(value = "이벤트ID")
    @NotNull(message = "eventId가 없습니다.")
    private String eventId;

    @ApiModelProperty(value = "당첨 정보 인덱스")
    @NotNull(message = "arEventWinningId가 없습니다.")
    private Integer arEventWinningId;

    @ApiModelProperty(value = "이름")
    private String name;

    @ApiModelProperty(value = "핸드폰번호(01012341234)")
    private String phoneNumber;

    @ApiModelProperty(value = "우편번호")
    private String zipCode;

    @ApiModelProperty(value = "주소")
    private String address;

    @ApiModelProperty(value = "주소상세")
    private String addressDetail;

    @ApiModelProperty(value = "참여코드")
    private String attendCode;

    @ApiModelProperty(value = "생년월일(19990101")
    private String memberBirth;

    @ApiModelProperty(value = "AR,서베이고 당첨 로그 인덱스")
    private Long eventLogWinningId;

    @ApiModelProperty(value = "당첨버튼 인덱스")
    private Integer arEventWinningButtonId;

    @ApiModelProperty(value = "서베이고 참여 로그 인덱스")
    private String surveyLogAttendId;

    @ApiModelProperty(value = "OCB쿠폰 아이디")
    private String ocbCouponId;

    @ApiModelProperty(value = "스탬프 인덱스")
    private Integer stpId;

    @ApiModelProperty(value = "스탬프 당첨 로그 인덱스")
    private Long stpEventLogWinningId;

    @ApiModelProperty(value = "스탬프 당첨 입력 인덱스")
    private Long stpGiveAwayId;

    @ApiModelProperty(value = "당첨 정보 추가 입력 목록")
    private List<GiveAwayDeliveryButtonAddInputDto> giveAwayDeliveryButtonAddInputList;
}
