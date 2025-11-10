package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AR_EVENT_HTML")
public class ArEventHtmlEntity implements Serializable {

    private static final long serialVersionUID = -1286202737345212932L;

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventHtmlId;

    private String eventId;

    // 이벤트 아이디
    private Integer arEventId;

    // 스탬프 메인 인덱스
    private Integer stpId;

    //스탬프 판 인덱스
    private Integer stpPanId;

    // html 정보 타입(1:이미지, 2:버튼, 3:공유하기)
    private String htmlType;

    // 순서
    private Integer htmlTypeSort;

    // 이미지 url
    private String htmlImageUrl;

    // 버튼 유형
    private String htmlButtonType;

    // 버튼 배경색 지정여부
    private String htmlButtonBgColorAssignType;

    // 버튼 배경색 지정일떄 RGB, HEX 여부)
    private String htmlButtonBgColorInputType;

    // 버튼 배경색 rgb 값
    private Integer htmlButtonBgColorRed;

    // 버튼 배경색 rgb 값
    private Integer htmlButtonBgColorGreen;

    // 버튼 배경색 rgb 값
    private Integer htmlButtonBgColorBlue;

    // 버튼 배경색 hex 값
    private String htmlButtonBgColorHex;

    // 버튼 text
    private String htmlButtonText;

    // 버튼 target url
    private String htmlButtonTargetUrl;

    // 공유하기 버튼 이미지 url
    private String htmlShareButtonImageUrl;

    // 버튼색 지정여부
    private String htmlButtonColorAssignType;

    // 버튼색 지정일떄 RGB, HEX 여부)
    private String htmlButtonColorInputType;

    // 버튼색 rgb 값
    private Integer htmlButtonColorRed;

    // 버튼색 rgb 값
    private Integer htmlButtonColorGreen;

    // 버튼색 rgb 값
    private Integer htmlButtonColorBlue;

    // 버튼색 hex 값
    private String htmlButtonColorHex;

    // 버튼 텍스트색 지정여부
    private String htmlButtonTextColorAssignType;

    // 버튼 테스트색 지정일떄 RGB, HEX 여부)
    private String htmlButtonTextColorInputType;

    // 버튼 테스트색 rgb값
    private Integer htmlButtonTextColorRed;

    // 버튼 테스트색 rgb값
    private Integer htmlButtonTextColorGreen;

    // 버튼 테스트색 rgb값
    private Integer htmlButtonTextColorBlue;

    // 버튼 테스트색 rgb값
    private String htmlButtonTextColorHex;

    //카카오톡 공유하기 썸네일 url
    private String kakaoShareThumbnailUrl;

    //카카오톡 공유하기 내용
    private String kakaoShareContents;

    // 서베이고 추가건 버튼 유형 지정 (둥글게 : ROUND, 부드럽게 : SOFT, 각지게 : ANGLE)
    private String htmlButtonShapeType;

    // 포토 추가건 디바이스 위치 찾기 버튼 설정 여부 (설정안함 : N / 설정함 : Y)
    private String deviceLocationFindSettingYn;

    // 포토 추가건 디바이스 위치 찾기 버튼 문구
    private String deviceLocationFindButtonText;

    // 포토 추가건 위치 찾기 노출 설정 (지도보기 : MAP / 팝업보기 : POPUP)
    private String locationFindExposureType;

    // 포토 추가건 위치찾기 팝업 이미지 url
    private String locationFindPopupImgUrl;

    // 포토 추가건 무료출력수 제어 (설정안함 : N / 설정함 : Y)
    private String freePrintControlYn;

    // 포토 추가건 고객당 출력 개수
    private Integer freePrintCustomerCount;

    // 생성자
    private String createdBy;

    // 생성일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    // 수정자
    private String lastModifiedBy;

    // 수정일
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;
}
