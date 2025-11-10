package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.web.event.define.SmsTranTypeDefine;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "em_tran")
public class EmTranEntity implements Serializable {

    private static final long serialVersionUID = 5160865623255974665L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tran_pr", nullable = false)
    private Integer id;

    @Column(name = "tran_refkey", length = 40)
    private String tranRefkey;

    @Column(name = "tran_id", length = 20)
    private String tranId;

    @Column(name = "tran_phone", nullable = false, length = 15)
    private String tranPhone;

    @Column(name = "tran_callback", length = 15)
    private String tranCallback;

    @Column(name = "tran_status")
    private String tranStatus;

    @Column(name = "tran_date", nullable = false)
    private String tranDate;

    @Column(name = "tran_rsltdate")
    private String tranRsltdate;

    @Column(name = "tran_reportdate")
    private String tranReportdate;

    @Column(name = "tran_rslt")
    private String tranRslt;

    @Column(name = "tran_net", length = 3)
    private String tranNet;

    @Column(name = "tran_msg")
    private String tranMsg;

    @Column(name = "tran_etc1", length = 64)
    private String tranEtc1;

    @Column(name = "tran_etc2", length = 16)
    private String tranEtc2;

    @Column(name = "tran_etc3", length = 16)
    private String tranEtc3;

    @Column(name = "tran_etc4")
    private Integer tranEtc4;

    @Column(name = "tran_type", nullable = false)
    private Integer tranType;

    public static EmTranEntity ofSms(String tranPhone, String tranCallback, String tranMsg) {
        EmTranEntity saveEntity = new EmTranEntity();
        saveEntity.setTranPhone(tranPhone.trim());
        saveEntity.setTranCallback(tranCallback.trim());
        saveEntity.setTranStatus("1");
        saveEntity.setTranMsg(tranMsg.trim());
        saveEntity.setTranDate(DateUtils.returnNowDateByYyyymmddhhmmss());
        saveEntity.setTranType(SmsTranTypeDefine.SMS.key());
        return saveEntity;
    }

    public static EmTranEntity ofSmsAssignTranDate(String tranPhone, String tranCallback, String tranMsg, String tranDate) {
        EmTranEntity saveEntity = new EmTranEntity();
        saveEntity.setTranPhone(tranPhone.trim());
        saveEntity.setTranCallback(tranCallback.trim());
        saveEntity.setTranStatus("1");
        saveEntity.setTranMsg(tranMsg.trim());
        saveEntity.setTranDate(tranDate);
        saveEntity.setTranType(SmsTranTypeDefine.SMS.key());
        return saveEntity;
    }

    public static EmTranEntity ofMMS(String tranPhone, String tranCallback, String tranMsg, Integer mmsSeq) {
        EmTranEntity saveEntity = new EmTranEntity();
        saveEntity.setTranPhone(tranPhone.trim());
        saveEntity.setTranCallback(tranCallback.trim());
        saveEntity.setTranStatus("1");
        saveEntity.setTranMsg(tranMsg.trim());
        saveEntity.setTranDate(DateUtils.returnNowDateByYyyymmddhhmmss());
        saveEntity.setTranEtc4(mmsSeq);
        saveEntity.setTranType(SmsTranTypeDefine.MMS.key());
        return saveEntity;
    }

    public static EmTranEntity ofMMSAssignTranDate(String tranPhone, String tranCallback, String tranMsg, String tranDate, Integer mmsSeq) {
        EmTranEntity saveEntity = new EmTranEntity();
        saveEntity.setTranPhone(tranPhone.trim());
        saveEntity.setTranCallback(tranCallback.trim());
        saveEntity.setTranStatus("1");
        saveEntity.setTranMsg(tranMsg.trim());
        saveEntity.setTranDate(tranDate);
        saveEntity.setTranEtc4(mmsSeq);
        saveEntity.setTranType(SmsTranTypeDefine.MMS.key());
        return saveEntity;
    }

}