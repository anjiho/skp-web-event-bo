package kr.co.syrup.adreport.stamp.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.define.StampWinningAttendTypeDefine;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

// 스탬프 이벤트 메인 (스탬프 신규건)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampEventMainModel implements Serializable {

    private static final long serialVersionUID = 3773274102370024016L;

    // 인덱스
    private Integer stpId;

    //이벤트 아이디(WEB_EVENT_BASE.event_id)
    private String eventId;

    // 스탬프 이벤트 명
    private String stpEventTitle;

    // 메인 설정 여부 (설정함, 설정안함)
    private String stpMainSettingYn;

    // 참여 인증 조건(전화번호, 참여번호)
    private String stpAttendAuthCondition;

    // 참여번호 등록 종류(자동생성, 파일등록)
    private String stpAttendCodeRegType;

    // 참여번호 자릿수
    private Integer stpAttendCodeDigit;

    // 참여번호 생성수
    private Integer stpAttendCodeCount;

    // 파일 업로드 시 엑셀 파일명
    private String stpAttendCodeFileName;

    // 참여번호 비매칭시 문구
    private String stpAttendCodeMisTxt;

    // 알림톡 발송 여부(발송함, 발송안함)
    private String alimtokSendYn;

    // 당첨정보(공통)설정 > 중복당첨수 제한 타입
    private String duplicateWinningType;

    // 중복당첨 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer duplicateWinningLimitType;

    // 중복 당첨 당첨제한 회수
    private Integer duplicateWinningCount;

    // 추첨형 스탬프 기점 번호 (1,2,3,~)
    private String winningRaffleStartPoint;

    // 당첨없음, 교환형, 추첨형
    private String stpWinningType;

    // 배경 이미지 URL
    private String stpBgImgUrl;

    //스탬프 메인 BG 색상 지정 종류
    private String stpMainBgColorAssignType;

    //스탬프 메인 BG 색상 지정일떄 RGB, HEX 여부)
    private String stpMainBgColorInputType;

    //스탬프 메인 색상 rgb 값
    private Integer stpMainBgColorRed;

    //스탬프 메인 색상 rgb 값
    private Integer stpMainBgColorGreen;

    //스탬프 메인 색상 rgb 값
    private Integer stpMainBgColorBlue;

    //스탬프 메인 색상 hex 값
    private String stpMainBgColorHex;

    //당첨 - 참여순번 설정 (설정안함 : N, 설정 : Y) - 2차 고도화
    private String stpAttendSortSettingYn;

    private String createdBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date createdDate;

    private String lastModifiedBy = PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자";

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    private Date lastModifiedDate;

    public static StampEventMainModel ofSave(StampEventMainModel stampEventMainModel, String eventId) {
        if (PredicateUtils.isNotNull(eventId)) {
            stampEventMainModel.setEventId(eventId);
        }
        return stampEventMainModel;
    }

    public static Boolean isMdnCondition(StampEventMainModel stampEventMainModel) {
        if (PredicateUtils.isNull(stampEventMainModel.getStpAttendAuthCondition())) {
            return true;
        } else {
            if (PredicateUtils.isEqualsStr(stampEventMainModel.getStpAttendAuthCondition(), StampWinningAttendTypeDefine.MDN.name())) {
                return true;
            } else if (PredicateUtils.isEqualsStr(stampEventMainModel.getStpAttendAuthCondition(), StampWinningAttendTypeDefine.ATTEND.name())) {
                return false;
            }
            return null;
        }
    }
}
