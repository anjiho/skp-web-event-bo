package kr.co.syrup.adreport.web.event.dto.response;

import lombok.*;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WinningResDto implements Serializable {

    private static final long serialVersionUID = -1229729732641582676L;

    private Integer arEventWinningId;

    //당첨 타입 > 기타(배송) : ETC, 기프티콘 : GIFTICON, 꽝 : FAIL
    private String winningType;

    //당첨 이미지 url
    private String winningImageUrl;

    //응모여부
    private String subscriptionYn;

    //자동당첨여부
    private String autoWinningYn;

}
