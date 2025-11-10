package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ar_event_nft_repository")
public class ArEventNftRepositoryEntity implements Serializable {

    private static final long serialVersionUID = -6567554340924315047L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_nft_repository_id", nullable = false)
    private Long id;

    //AR_NFT_WALLET.index
    @Column(name = "ar_event_nft_wallet_id")
    private Long arEventNftWalletId;

    //이벤트 경품 배송 정보 인덱스
    @Column(name = "give_away_id", nullable = false)
    private Integer giveAwayId;

    //AR_EVENT_NFT_TOKEN_INFO.id
    @Column(name = "ar_event_nft_token_info_id")
    private Long arEventNftTokenInfoId;

    //NFT 이전 시각
    @Column(name = "nft_trance_date")
    private Date nftTranceDate;

    //소유권 이전 여부(0 : 이전전, 1 : 이전중, 2 : 이전완료)
    @Column(name = "holder_trance_status", nullable = false)
    private Integer holderTranceStatus;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "nft_webhook_result")
    private String nftWebhookResult;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "event_winning_log_id")
    private Long eventWinningLogId;

    @Column(name = "stp_give_away_id")
    private Long stpGiveAwayId;

    @PrePersist
    private void prePersist() {
        this.createdDate = PredicateUtils.isNull(this.createdDate) ? DateUtils.returnNowDate() : this.createdDate;
        this.holderTranceStatus = 0;
    }

    public static ArEventNftRepositoryEntity saveOf(int giveAwayId, long arEventNftTokenInfoId) {
        ArEventNftRepositoryEntity arEventNftRepositoryEntity = new ArEventNftRepositoryEntity();
        arEventNftRepositoryEntity.setGiveAwayId(giveAwayId);
        arEventNftRepositoryEntity.setArEventNftTokenInfoId(arEventNftTokenInfoId);
        return arEventNftRepositoryEntity;
    }

    public static ArEventNftRepositoryEntity prevSaveOf(long arEventNftTokenInfoId, long eventWinningLogId) {
        ArEventNftRepositoryEntity arEventNftRepositoryEntity = new ArEventNftRepositoryEntity();
        arEventNftRepositoryEntity.setArEventNftTokenInfoId(arEventNftTokenInfoId);
        arEventNftRepositoryEntity.setEventWinningLogId(eventWinningLogId);
        return arEventNftRepositoryEntity;
    }

    public static ArEventNftRepositoryEntity webhookOf(ArEventNftRepositoryEntity arEventNftRepositoryEntity, String webhookResultStr) {
        arEventNftRepositoryEntity.setNftWebhookResult(webhookResultStr);
        return arEventNftRepositoryEntity;
    }


}