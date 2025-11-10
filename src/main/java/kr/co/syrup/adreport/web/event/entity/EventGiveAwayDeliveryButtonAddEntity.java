package kr.co.syrup.adreport.web.event.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "EVENT_GIVE_AWAY_DELIVERY_BUTTON_ADD")
public class EventGiveAwayDeliveryButtonAddEntity implements Serializable {

    private static final long serialVersionUID = -1239058363537131194L;

    // 포토 추가건 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "give_away_delivery_button_add_id")
    private Long id;

    // 포토 추가건 이벤트 경품 배송 정보 인덱스
    private Integer giveAwayId;

    // 포토 추가건 당첨정보 입력 추가 설정 테이블 인덱스
    private Integer arEventWinningButtonAddId;

    // 포토 추가건 사용자 입력값
    private String fieldValue;

    // 포토 추가건 생성일
    private Date createdDate;

}
