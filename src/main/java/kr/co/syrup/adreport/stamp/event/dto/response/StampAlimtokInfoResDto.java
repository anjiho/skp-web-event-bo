package kr.co.syrup.adreport.stamp.event.dto.response;

import kr.co.syrup.adreport.stamp.event.model.StampAlimtokButtonModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@NoArgsConstructor
@Data
public class StampAlimtokInfoResDto implements Serializable {

    // 스탬프 알림톡 인덱스
    private Integer stpAlimtokId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    // 스탬프 알림톡 문구
    private String stpAlimtokTxt;

    // 타입(스탬프 최초 : STAMP , 경품최초 : WINNING)
    private String stpAlimtokSendType;

    private List<StampAlimtokButtonModel> stampAlimtokButtonList;
}
