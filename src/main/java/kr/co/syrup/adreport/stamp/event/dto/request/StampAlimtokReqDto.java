package kr.co.syrup.adreport.stamp.event.dto.request;

import kr.co.syrup.adreport.stamp.event.model.StampAlimtokButtonModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StampAlimtokReqDto implements Serializable {

    private static final long serialVersionUID = -8986311992648422324L;

    // 스탬프 알림톡 인덱스
    private Integer stpAlimtokId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    // 스탬프 알림톡 문구
    private String stpAlimtokTxt;

    // 타입(스탬프 최초 : STAMP , 경품최초 : WINNING)
    private String stpAlimtokSendType;

    private List<StampAlimtokButtonModel> stampAlimtokButton;
}
