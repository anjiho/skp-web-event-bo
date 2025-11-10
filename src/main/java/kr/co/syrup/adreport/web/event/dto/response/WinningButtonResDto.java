package kr.co.syrup.adreport.web.event.dto.response;

import lombok.*;

import java.io.Serializable;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WinningButtonResDto implements Serializable {

    private static final long serialVersionUID = 7171643145715265791L;

    private Integer arEventWinningButtonId;

    //버튼유형( 경품배송입력 : DELIVERY, 계속하기(닫기) : CLOSE, URL접속 : URL )
    private String buttonActionType;

    //버튼 문구
    private String buttonText;

    //버튼 순서
    private Integer buttonSort;

    //버튼 링크 url (target url정보)
    private String linkUrl;

    //이동 경로 위치 > STAMP_PAN : 스탬프판, NEXT_EVENT : 다음 이벤트
    private String moveLocation;

    private String eventId;
}
