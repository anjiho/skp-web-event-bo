package kr.co.syrup.adreport.stamp.event.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StampEventGiveAwayDeliveryButtonAddModel implements Serializable {

    private static final long serialVersionUID = -7789019080562450392L;

    private Long id;

    //경품 배송 정보 인덱스
    private Long stpGiveAwayId;

    //당첨정보 입력 추가 설정 테이블 인덱스
    private Integer arEventWinningButtonAddId;

    //사용자 입력값
    private String fieldValue;

    //생성일
    private Date createdDate;
}
