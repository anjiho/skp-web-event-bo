package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@NoArgsConstructor
@Data
public class EventWinningButtonAddReqDto implements Serializable {

    private static final long serialVersionUID = 5078193806567310945L;

    //인덱스
    private Long id;

    // 포토 추가건 필드 이름
    private String fieldName;

    // 포토 추가건 필드 타입 (문자형 : CHAR / 숫자형 : INT)
    private String fieldType;

    // 포토 추가건 필드 길이 (필드타입이 숫자형일때 필수)
    private Integer fieldLength;
}
