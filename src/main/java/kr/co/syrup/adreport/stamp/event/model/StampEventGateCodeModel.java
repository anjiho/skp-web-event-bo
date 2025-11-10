package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

// 스탬프 이벤트 코드 테이블(엑셀 업로드)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventGateCodeModel implements Serializable {

    private static final long serialVersionUID = -3099572171589325376L;

    private Long id;

    // 스탬프 메인 아이디
    private Integer stpId;

    // 참여번호 값
    private String attendCode;

    private Boolean isUse;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;
}
