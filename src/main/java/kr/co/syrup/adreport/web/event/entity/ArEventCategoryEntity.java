package kr.co.syrup.adreport.web.event.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_CATEGORY")
public class ArEventCategoryEntity implements Serializable {

    private static final long serialVersionUID = 458295217884818197L;

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 카테고리 코드 값
    private String categoryCode;

    // 카테고리 이름
    private String categoryName;

    // 카테고리 값
    private String categoryValue;

    // 카테고리 타입
    private String categoryType;

    // 부모 카테고리 코드
    private String parentCode;

    // 카테고리 뎁스
    private Integer categoryDepth;
}
