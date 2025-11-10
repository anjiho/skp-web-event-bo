package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class OcbPointApiResDto implements Serializable {

    private static final long serialVersionUID = 4396898899693983756L;

    /**
     * code 별 응답 결과
     * "00": 요청 성공(save_type을 "queue", "point_inquery"로 요청할 경우, 실제 적립은 파라메터에 따라 별도 시점에 처리됨)
     * "10": Token값 유효성 검증 실패
     * "20": 요청 파라메터 값 오류
     * "30": Nxmile 적립 실패 (save_type="real_time"로 요청한 경우, 적립 요청 실패 시)
     * "90": 기타 요청 실패 시
     */
    private String code;

    // 기본 상태 메세지
    private String message;

    // 후적립(save_type="queue", "point_inquery")에 대한 적립 추적번호
    private String trackingId;

    // 메세지 상세 내용
    private String detailMessage;

    private Integer requestPoint;

    private String requestId;
}
