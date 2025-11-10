package kr.co.syrup.adreport.stamp.event.mybatis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AttendCodeUseVO implements Serializable {

    private static final long serialVersionUID = 1145084012628596818L;

    private String attendCode;

    private String stampName;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date accDate;

    private String isRaffle;

    private String productName;

    private String winningDate;
}
