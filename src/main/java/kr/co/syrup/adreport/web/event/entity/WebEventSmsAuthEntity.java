package kr.co.syrup.adreport.web.event.entity;

import com.jcraft.jsch.jce.SHA256;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.SecurityUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@DynamicInsert
@ToString
@Getter
@Setter
@Entity
@Table(name = "WEB_EVENT_SMS_AUTH")
public class WebEventSmsAuthEntity implements Serializable {

    private static final long serialVersionUID = 6670487316612473933L;

    @Id
    private String smsAuthCode;

    // 이벤트 ID
    private String eventId;

    // 핸드폰 번호
    private String phoneNumber;

    // 인증 메뉴종류(참여인증, 당첨조회,..)
    private String authMenuType;

    // 인증코드
    private String authCode;

    // 인증여부
    private Boolean isAuth;

    // 인증만료 시간
    private Date authExpireDate;

    // 생성일
    private Date createdDate;

    public static WebEventSmsAuthEntity saveOf(String eventId, String phoneNumber, String authMenuType) {
        SecurityUtils securityUtils = new SecurityUtils();

        String smsAuthCode = StringTools.joinStringsNoSeparator(DateUtils.returnNowDateByYYmmddhhmmss(), eventId, phoneNumber);

        WebEventSmsAuthEntity authEntity = new WebEventSmsAuthEntity();
        authEntity.setSmsAuthCode(securityUtils.encryptSHA256(smsAuthCode));
        authEntity.setEventId(eventId);
        authEntity.setPhoneNumber(phoneNumber);
        authEntity.setAuthMenuType(authMenuType);
        authEntity.setAuthCode(RandomStringUtils.randomNumeric(6));
        authEntity.setIsAuth(false);
        authEntity.setAuthExpireDate(DateUtils.convertDateTimeFormat(DateUtils.plusMinute(3)));
        return authEntity;
    }
}
