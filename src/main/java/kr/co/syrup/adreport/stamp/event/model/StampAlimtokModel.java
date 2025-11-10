package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

// 스탬프 알림톡 설정
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampAlimtokModel implements Serializable {

    private static final long serialVersionUID = -2594293438478786977L;

    // 스탬프 알림톡 인덱스
    private Integer stpAlimtokId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    // 스탬프 알림톡 문구
    private String stpAlimtokTxt;

    // 타입(스탬프 최초 : STAMP , 경품최초 : WINNING)
    private String stpAlimtokSendType;

    private String createdBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private String lastModifiedBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    public static StampAlimtokModel of(String stpAlimtokTxt, String stpAlimtokSendType) {
        StampAlimtokModel alimtokModel = new StampAlimtokModel();
        alimtokModel.setStpAlimtokTxt(stpAlimtokTxt);
        alimtokModel.setStpAlimtokSendType(stpAlimtokSendType);
        return alimtokModel;
    }

    public static StampAlimtokModel ofUpdate(Integer stpAlimtokId, String stpAlimtokTxt, String stpAlimtokSendType) {
        StampAlimtokModel alimtokModel = new StampAlimtokModel();
        alimtokModel.setStpAlimtokId(stpAlimtokId);
        alimtokModel.setStpAlimtokTxt(stpAlimtokTxt);
        alimtokModel.setStpAlimtokSendType(stpAlimtokSendType);
        return alimtokModel;
    }
}
