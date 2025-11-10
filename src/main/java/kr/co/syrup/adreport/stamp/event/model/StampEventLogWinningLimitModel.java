package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//스탬프 당첨 제한 메모리 테이블
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventLogWinningLimitModel implements Serializable {

    private static final long serialVersionUID = -7047437792070978094L;

    private Integer stpId;

    private String code;

    private Boolean status;

    private String codeDesc;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;
}
