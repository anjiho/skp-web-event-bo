package kr.co.syrup.adreport.web.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.syrup.adreport.framework.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ar_event_nft_banner")
public class ArEventNftBannerEntity implements Serializable {

    private static final long serialVersionUID = -2312228098696921122L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "arNftBannerId")
    @Column(name = "ar_nft_banner_id", nullable = false)
    private Integer id;

    @Column(name = "ar_event_id")
    private Integer arEventId;

    @Column(name = "stp_id")
    private Integer stpId;

    @Column(name = "event_html_id", nullable = false)
    private Integer eventHtmlId;

    @Column(name = "banner_img_url", length = 200)
    private String bannerImgUrl;

    @Column(name = "banner_target_url", length = 200)
    private String bannerTargetUrl;

    @Column(name = "banner_sort", nullable = false)
    private Integer bannerSort;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Seoul")
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = this.createdDate == null ? DateUtils.returnNowDate() : this.createdDate;
    }

    public static ArEventNftBannerEntity saveOf(int arEventId, int eventHtmlId, String bannerImgUrl, String bannerTargetUrl, int bannerSort) {
        ArEventNftBannerEntity arEventNftBannerEntity = new ArEventNftBannerEntity();
        arEventNftBannerEntity.setArEventId(arEventId);
        arEventNftBannerEntity.setEventHtmlId(eventHtmlId);
        arEventNftBannerEntity.setBannerImgUrl(bannerImgUrl.trim());
        arEventNftBannerEntity.setBannerTargetUrl(bannerTargetUrl.trim());
        arEventNftBannerEntity.setBannerSort(bannerSort);
        return arEventNftBannerEntity;
    }

    public static ArEventNftBannerEntity saveStampOf(int stpId, int eventHtmlId, String bannerImgUrl, String bannerTargetUrl, int bannerSort) {
        ArEventNftBannerEntity arEventNftBannerEntity = new ArEventNftBannerEntity();
        arEventNftBannerEntity.setStpId(stpId);
        arEventNftBannerEntity.setEventHtmlId(eventHtmlId);
        arEventNftBannerEntity.setBannerImgUrl(bannerImgUrl.trim());
        arEventNftBannerEntity.setBannerTargetUrl(bannerTargetUrl.trim());
        arEventNftBannerEntity.setBannerSort(bannerSort);
        return arEventNftBannerEntity;
    }

    public static ArEventNftBannerEntity updateOf(int id, int arEventId, int eventHtmlId, String bannerImgUrl, String bannerTargetUrl, int bannerSort, Date createdDate) {
        ArEventNftBannerEntity arEventNftBannerEntity = new ArEventNftBannerEntity();
        arEventNftBannerEntity.setId(id);
        arEventNftBannerEntity.setArEventId(arEventId);
        arEventNftBannerEntity.setEventHtmlId(eventHtmlId);
        arEventNftBannerEntity.setBannerImgUrl(bannerImgUrl.trim());
        arEventNftBannerEntity.setBannerTargetUrl(bannerTargetUrl.trim());
        arEventNftBannerEntity.setBannerSort(bannerSort);
        arEventNftBannerEntity.setLastModifiedDate(DateUtils.returnNowDate());
        arEventNftBannerEntity.setCreatedDate(createdDate);
        return arEventNftBannerEntity;
    }

    public static ArEventNftBannerEntity updateOf(ArEventNftBannerEntity nftBanner, int arEventId, int eventHtmlId) {
        ArEventNftBannerEntity arEventNftBannerEntity = new ArEventNftBannerEntity();
        arEventNftBannerEntity.setId(nftBanner.getId());
        arEventNftBannerEntity.setArEventId(arEventId);
        arEventNftBannerEntity.setEventHtmlId(eventHtmlId);
        arEventNftBannerEntity.setBannerImgUrl(nftBanner.getBannerImgUrl().trim());
        arEventNftBannerEntity.setBannerTargetUrl(nftBanner.getBannerTargetUrl().trim());
        arEventNftBannerEntity.setBannerSort(nftBanner.getBannerSort());
        arEventNftBannerEntity.setCreatedDate(nftBanner.getCreatedDate());
        arEventNftBannerEntity.setLastModifiedDate(DateUtils.returnNowDate());
        return arEventNftBannerEntity;
    }

    public static ArEventNftBannerEntity updateOfStamp(int id, int stpId, int eventHtmlId, String bannerImgUrl, String bannerTargetUrl, int bannerSort, Date createdDate) {
        ArEventNftBannerEntity arEventNftBannerEntity = new ArEventNftBannerEntity();
        arEventNftBannerEntity.setId(id);
        arEventNftBannerEntity.setStpId(stpId);
        arEventNftBannerEntity.setEventHtmlId(eventHtmlId);
        arEventNftBannerEntity.setBannerImgUrl(bannerImgUrl.trim());
        arEventNftBannerEntity.setBannerTargetUrl(bannerTargetUrl.trim());
        arEventNftBannerEntity.setBannerSort(bannerSort);
        arEventNftBannerEntity.setLastModifiedDate(DateUtils.returnNowDate());
        arEventNftBannerEntity.setCreatedDate(createdDate);
        return arEventNftBannerEntity;
    }
}