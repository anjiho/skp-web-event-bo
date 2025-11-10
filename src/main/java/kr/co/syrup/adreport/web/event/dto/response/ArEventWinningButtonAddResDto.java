package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArEventWinningButtonAddResDto implements Serializable {

    private static final long serialVersionUID = 9184724908639865355L;

    //인덱스
    private Long id;

    // 포토 추가건 필드 이름
    private String fieldName;

    // 포토 추가건 필드 타입 (문자형 : CHAR / 숫자형 : INT)
    private String fieldType;

    // 포토 추가건 필드 길이 (필드타입이 숫자형일때 필수)
    private Integer fieldLength;
}
