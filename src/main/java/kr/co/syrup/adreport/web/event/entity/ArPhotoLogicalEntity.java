package kr.co.syrup.adreport.web.event.entity;

import kr.co.syrup.adreport.framework.utils.DateUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "AR_PHOTO_LOGICAL")
public class ArPhotoLogicalEntity implements Serializable {

    private static final long serialVersionUID = 4557949638825600971L;

    // 포토 추가건 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_photo_logical_id")
    private Integer id;

    // 포토 추가건 AR 이벤트 아이디
    @Column(name = "ar_event_id")
    private Integer arEventId;

    // 포토 추가건 튜토리얼 설정여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "tutorial_yn")
    private String tutorialYn;


    // 포토 추가건 AR 포토 비율 설정 (기본비율 : BASIC / 확장비율 : EXTENSION)
    @Column(name = "photo_ratio_setting_type")
    private String photoRatioSettingType;

    // 포토 추가건 AR 프레임 설정 여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "ar_frame_setting_yn")
    private String arFrameSettingYn;

    // 포토 추가건 탭메뉴 추가설정 (설정안함 : N/ 설정함 : Y)
    @Column(name = "photo_tab_menu_add_setting_yn")
    private String photoTabMenuAddSettingYn;

    // 포토 추가건 탭메뉴 제목
    @Column(name = "tab_menu_title")
    private String tabMenuTitle;

    // 포토 추가건 AR 필터 설정 (설정안함 : N/ 설정함 : Y)
    @Column(name = "ar_filter_setting_yn")
    private String arFilterSettingYn;

    // 포토 추가건 AR 캐릭터 설정 (설정안함 : N/ 설정함 : Y)
    @Column(name = "ar_character_setting_yn")
    private String arCharacterSettingYn;

    // 포토 추가건 스티커 설정 (설정안함 : N/ 설정함 : Y)
    @Column(name = "ar_sticker_setting_yn")
    private String arStickerSettingYn;

    // 포토 추가건 촬영 결과 이미지 url
    @Column(name = "film_result_img_url")
    private String filmResultImgUrl;

    // 포토 추가건 해시태그 설정 여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "hash_tag_setting_yn")
    private String hashTagSettingYn;

    // 포토 추가건 해시태그 값 (콤마 , 로 구분자)
    @Column(name = "hash_tag_value")
    private String hashTagValue;

    // 포토 추가건 공유하기 동의팝업 설정여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "share_agree_popup_setting_yn")
    private String shareAgreePopupSettingYn;

    // 포토 추가건 동의 팝업 문구
    @Column(name = "agree_popup_text")
    private String agreePopupText;

    // 포토 추가건 자세히 보기 링크 url
    @Column(name = "agree_popup_detail_link_url")
    private String agreePopupDetailLinkUrl;

    // 포토 추가건 입력창 기본 문구
    @Column(name = "agree_popup_input_text")
    private String agreePopupInputText;

    // 포토 추가건 사진 출력 설정 여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "photo_print_setting_yn")
    private String photoPrintSettingYn;

    // 포토 추가건 사진 출력 버튼명
    @Column(name = "photo_print_button_text")
    private String photoPrintButtonText;

    // 포토 추가건 경품 당첨 설정 여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "photo_give_away_setting_yn")
    private String photoGiveAwaySettingYn;

    // 포토 추가건 경품 당첨 버튼명
    @Column(name = "photo_give_away_button_text")
    private String photoGiveAwayButtonText;

    // 포토 추가건 결과 하단 이미지 설정 여부 (설정안함 : N/ 설정함 : Y)
    @Column(name = "film_result_footer_img_setting_yn")
    private String filmResultFooterImgSettingYn;

    // 포토 추가건 하단 이미지 url
    @Column(name = "film_result_footer_img_url")
    private String filmResultFooterImgUrl;

    // 포토 추가건 생성자
    @Column(name = "created_by")
    private String createdBy;

    // 포토 추가건 생성일
    @Column(name = "created_date")
    private Date createdDate;

    // 포토 추가건 수정자
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // 포토 추가건 수정일
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = DateUtils.returnNowDate();
    }

    public static ArPhotoLogicalEntity ofUpdate(ArPhotoLogicalEntity findEntity, ArPhotoLogicalEntity newEntity) {
        ArPhotoLogicalEntity updateEntity = new ArPhotoLogicalEntity();
        updateEntity.setId(findEntity.getId());
        updateEntity.setArEventId(findEntity.getArEventId());
        updateEntity.setTutorialYn(PredicateUtils.isNull(newEntity.getTutorialYn()) ? findEntity.getTutorialYn() : newEntity.getTutorialYn());
        updateEntity.setPhotoRatioSettingType(PredicateUtils.isNull(newEntity.getPhotoRatioSettingType()) ? findEntity.getPhotoRatioSettingType() : newEntity.getPhotoRatioSettingType());
        updateEntity.setArFrameSettingYn(PredicateUtils.isNull(newEntity.getArFrameSettingYn()) ? findEntity.getArFrameSettingYn() : newEntity.getArFrameSettingYn());
        updateEntity.setPhotoTabMenuAddSettingYn(PredicateUtils.isNull(newEntity.getPhotoTabMenuAddSettingYn()) ? findEntity.getPhotoTabMenuAddSettingYn() : newEntity.getPhotoTabMenuAddSettingYn());
        updateEntity.setTabMenuTitle(PredicateUtils.isNull(newEntity.getTabMenuTitle()) ? findEntity.getTabMenuTitle() : newEntity.getTabMenuTitle());
        updateEntity.setArFilterSettingYn(PredicateUtils.isNull(newEntity.getArFilterSettingYn()) ? findEntity.getArFilterSettingYn() : newEntity.getArFilterSettingYn());
        updateEntity.setArCharacterSettingYn(PredicateUtils.isNull(newEntity.getArCharacterSettingYn()) ? findEntity.getArCharacterSettingYn() : newEntity.getArCharacterSettingYn());
        updateEntity.setArStickerSettingYn(PredicateUtils.isNull(newEntity.getArCharacterSettingYn()) ? findEntity.getArStickerSettingYn() : newEntity.getArStickerSettingYn());
        updateEntity.setFilmResultImgUrl(PredicateUtils.isNull(newEntity.getFilmResultImgUrl()) ? findEntity.getFilmResultImgUrl() : newEntity.getFilmResultImgUrl());
        updateEntity.setHashTagSettingYn(PredicateUtils.isNull(newEntity.getHashTagSettingYn()) ? findEntity.getHashTagSettingYn() : newEntity.getHashTagSettingYn());
        updateEntity.setHashTagValue(PredicateUtils.isNull(newEntity.getHashTagValue()) ? findEntity.getHashTagValue() : newEntity.getHashTagValue());
        updateEntity.setShareAgreePopupSettingYn(PredicateUtils.isNull(newEntity.getShareAgreePopupSettingYn()) ? findEntity.getShareAgreePopupSettingYn() : newEntity.getShareAgreePopupSettingYn());
        updateEntity.setAgreePopupText(PredicateUtils.isNull(newEntity.getAgreePopupText()) ? findEntity.getAgreePopupText() : newEntity.getAgreePopupText());
        updateEntity.setAgreePopupDetailLinkUrl(PredicateUtils.isNull(newEntity.getAgreePopupDetailLinkUrl()) ? findEntity.getAgreePopupDetailLinkUrl() : newEntity.getAgreePopupDetailLinkUrl());
        updateEntity.setAgreePopupInputText(PredicateUtils.isNull(newEntity.getAgreePopupInputText()) ? findEntity.getAgreePopupInputText() : newEntity.getAgreePopupInputText());
        updateEntity.setPhotoPrintSettingYn(PredicateUtils.isNull(newEntity.getPhotoPrintSettingYn()) ? findEntity.getPhotoPrintSettingYn() : newEntity.getPhotoPrintSettingYn());
        updateEntity.setPhotoPrintButtonText(PredicateUtils.isNull(newEntity.getPhotoPrintButtonText()) ? findEntity.getPhotoPrintButtonText() : newEntity.getPhotoPrintButtonText());
        updateEntity.setPhotoGiveAwaySettingYn(PredicateUtils.isNull(newEntity.getPhotoGiveAwaySettingYn()) ? findEntity.getPhotoGiveAwaySettingYn() : newEntity.getPhotoGiveAwaySettingYn());
        updateEntity.setPhotoGiveAwayButtonText(PredicateUtils.isNull(newEntity.getPhotoGiveAwayButtonText()) ? findEntity.getPhotoGiveAwayButtonText() : newEntity.getPhotoGiveAwayButtonText());
        updateEntity.setFilmResultFooterImgSettingYn(PredicateUtils.isNull(newEntity.getFilmResultFooterImgSettingYn()) ? findEntity.getFilmResultFooterImgSettingYn() : newEntity.getFilmResultFooterImgSettingYn());
        updateEntity.setFilmResultFooterImgUrl(PredicateUtils.isNull(newEntity.getFilmResultFooterImgUrl()) ? findEntity.getFilmResultFooterImgUrl() : newEntity.getFilmResultFooterImgUrl());
        updateEntity.setCreatedDate(findEntity.getCreatedDate());
        return updateEntity;
    }
}
