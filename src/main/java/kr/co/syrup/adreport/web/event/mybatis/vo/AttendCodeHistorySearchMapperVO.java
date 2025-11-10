package kr.co.syrup.adreport.web.event.mybatis.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendCodeHistorySearchMapperVO implements Serializable {

     private static final long serialVersionUID = -6993941576927402207L;

     private Integer rowNum;

     private Long id;

     private String attendCode;

     private Integer giveAwayId;

     private String winningType;

     private String logCreatedDate;

     private String createdDate;

     private String productName;

     private String name;

     private String phoneNumber;

     private String status;
}
