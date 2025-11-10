package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OcbLogPointSaveVO implements Serializable {
    private static final long serialVersionUID = -1984625582358696770L;

    // 포토 추가건 이벤트 아이디
    private String eventId;

    //포토 추가건 OCB 포인트 적립정보 인덱스
    private Integer ocbPointSaveId;

    // 포토 추가건 핸드폰번호(암호화)
    private String phoneNumber;

    // 포토 추가건 OCB MBR_ID
    private String ocbMbrId;

    // 포토 추가건 OCB 포인트 적립 타입( 참여 전적립: PREV, 당첨 : WIN)
    private String pointSaveType;

    // 포토 추가건 적립 포인트
    private Integer point;

    // 포토 추가건 성공/실패 여부
    private Boolean isSuccess;

    // 포토 추가건 실패시 OCB 응답 로그
    private String pointSaveResult;

    private Integer giveAwayId;

    private String requestId;

}
