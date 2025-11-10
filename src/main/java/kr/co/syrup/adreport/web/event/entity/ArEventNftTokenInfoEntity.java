package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "ar_event_nft_token_info")
public class ArEventNftTokenInfoEntity implements Serializable {

    private static final long serialVersionUID = 974103060427392616L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //이벤트 아이디
    private Integer arEventId;

    //스탬프 이벤트 인덱스
    private Integer stpId;

    //당첨 정보 아이디
    @Column(name = "ar_event_winning_id")
    private Integer arEventWinningId;

    //토큰 아이디
    @Column(name = "nft_token_id", nullable = false)
    private String nftTokenId;

    //지급 여부
    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;

    @Column(name = "upload_excel_file_name")
    private String uploadExcelFileName;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
        this.isPayed = false;
    }

    public static ArEventNftTokenInfoEntity transferOf(ArEventNftTokenInfoEntity tokenInfo) {
        tokenInfo.setIsPayed(true);
        return tokenInfo;
    }

    public static ArEventNftTokenInfoEntity excelUploadOf(String nftTokenId, String uploadExcelFileName) {
        ArEventNftTokenInfoEntity tokenInfoEntity = new ArEventNftTokenInfoEntity();
        tokenInfoEntity.setNftTokenId(nftTokenId);
        tokenInfoEntity.setUploadExcelFileName(uploadExcelFileName);
        return tokenInfoEntity;
    }

    public static ArEventNftTokenInfoEntity addExcelUploadOf(int arEventId, int arEventWinningId, String nftTokenId, String uploadExcelFileName) {
        ArEventNftTokenInfoEntity tokenInfoEntity = new ArEventNftTokenInfoEntity();
        tokenInfoEntity.setArEventId(arEventId);
        tokenInfoEntity.setArEventWinningId(arEventWinningId);
        tokenInfoEntity.setNftTokenId(nftTokenId);
        tokenInfoEntity.setUploadExcelFileName(uploadExcelFileName);
        return tokenInfoEntity;
    }

}