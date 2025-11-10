package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

// 스탬프 알림톡 버튼 설정
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampAlimtokButtonModel implements Serializable {

    private static final long serialVersionUID = -4359196368152004450L;

    // 스탬프 알림톡 버튼 인덱스
    private Long stpAlimtokBtnId;

    // 스탬프 알림톡 인덱스
    private Integer stpAlimtokId;

    // 버튼 문구
    private String stpAlimtokBtnTxt;

    // 버튼 URL
    private String stpAlimtokBtnUrl;

    // 순서
    private Integer stpAlimtokBtnSort;

    private String createdBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private String lastModifiedBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;
}
