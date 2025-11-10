package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class ArEventByIdAtWinningProcessMapperVO implements Serializable {

    private static final long serialVersionUID = -4819132379565008437L;

    private String eventId;

    private Integer arEventId;

    // 당첨정보(공통)설정 > 중복당첨수 제한 타입
    private String duplicateWinningType;

    // AR 참여조건(참여번호)
    private Boolean arAttendConditionCodeYn;

    // 중복당첨 당첨제한 (전체 : 0 , 1일 : 1)
    private Integer duplicateWinningLimitType;

    // 중복 당첨 당첨제한 회수
    private Integer duplicateWinningCount;

    // AR 구동 정보(기본형 ~ 이미지스캐닝형)
    private String eventLogicalType;

    private Boolean attendConditionMdnYn;

    private String eventExposureType;

    private String ocbPointSaveType;

    private String stpConnectYn;

}
