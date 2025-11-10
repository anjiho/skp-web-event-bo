package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.web.event.define.SmsTranTypeDefine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "em_tran_mms")
public class EmTranMmsEntity implements Serializable {

    private static final long serialVersionUID = 8221709771253046178L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mms_seq", nullable = false)
    private Integer id;

    @Column(name = "file_cnt", nullable = false)
    private Integer fileCnt;

    @Column(name = "build_yn")
    private Character buildYn;

    @Lob
    @Column(name = "mms_body")
    private String mmsBody;

    @Column(name = "mms_subject", length = 40)
    private String mmsSubject;

    @Column(name = "file_type1", length = 3)
    private String fileType1;

    @Column(name = "file_type2", length = 3)
    private String fileType2;

    @Column(name = "file_type3", length = 3)
    private String fileType3;

    @Column(name = "file_type4", length = 3)
    private String fileType4;

    @Column(name = "file_type5", length = 3)
    private String fileType5;

    @Column(name = "file_name1", length = 100)
    private String fileName1;

    @Column(name = "file_name2", length = 100)
    private String fileName2;

    @Column(name = "file_name3", length = 100)
    private String fileName3;

    @Column(name = "file_name4", length = 100)
    private String fileName4;

    @Column(name = "file_name5", length = 100)
    private String fileName5;

    @Column(name = "service_dep1", length = 3)
    private String serviceDep1;

    @Column(name = "service_dep2", length = 3)
    private String serviceDep2;

    @Column(name = "service_dep3", length = 3)
    private String serviceDep3;

    @Column(name = "service_dep4", length = 3)
    private String serviceDep4;

    @Column(name = "service_dep5", length = 3)
    private String serviceDep5;

    @Column(name = "skn_file_name")
    private String sknFileName;

    public static EmTranMmsEntity ofMms(String mmsBody, String mmsSubject) {
        EmTranMmsEntity saveEntity = new EmTranMmsEntity();
        saveEntity.setFileCnt(1);
        saveEntity.setMmsBody(mmsBody);
        saveEntity.setMmsSubject(mmsSubject);
        return saveEntity;
    }


}