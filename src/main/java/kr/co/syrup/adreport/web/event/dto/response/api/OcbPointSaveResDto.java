package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OcbPointSaveResDto implements Serializable {

    private static final long serialVersionUID = -399374281462937610L;

    //인덱스
    private Integer id;

    // 포토 추가건 AR 이벤트 당첨 아이디
    private Integer arEventWinningId;

    // 포토 추가건 OCB 포인트 적립코드
    private String ocbPointSaveCode;

    // 포토 추가건 사업자번호
    private String businessNumber;

    // 포토 추가건 적립 기간 (이벤트 기간내 / 1일)
    private Integer saveTermType;

    // 포토 추가건 최대적립고객수
    private Integer saveMaxCustomerCount;

    //포토 추가건 기타 선택 여부 (YN)
    private Boolean isEtc;

    // 포토 추가건 적립금액
    private Integer savePoint;
}
