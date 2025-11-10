package kr.co.syrup.adreport.web.event.mybatis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class CommonLogPersonAgreeMapperVO {

    private String agreeId;

    private String createdDate;

    private String phoneNumber;
}
