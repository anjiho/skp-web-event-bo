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
@Table(name = "AR_PHOTO_CONTENTS")
public class ArPhotoContentsEntity implements Serializable {

    private static final long serialVersionUID = -4090448700628942026L;

    // 포토 추가건 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_content_id")
    private Long id;

    // 포토 추가건 AR 이벤트 아이디
    @Column(name = "ar_event_id")
    private Integer arEventId;

    // 포토 추가건 구분 타입 (프레임 : FRAME / 탭메뉴 : TAB / 필터 : FILTER / 캐릭터 : CHARACTER  / 스티커 : STICKER)
    @Column(name = "photo_content_type")
    private String photoContentType;

    // 포토 추가건 순서
    @Column(name = "sort")
    private Integer sort;

    // 포토 추가건 컨텐츠 메뉴 선택 타입 (라이브러리 :  LIBRARY / 직접등록 : DIRECT)
    @Column(name = "photo_content_choice_type")
    private String photoContentChoiceType;

    // 포토 추가건 파일명 (라이브러리 선택시 필수)
    @Column(name = "photo_file_name")
    private String photoFileName;

    // 포토 추가건 썸네일 이미지 URL
    @Column(name = "photo_thumbnail_img_url")
    private String photoThumbnailImgUrl;

    // 포토 추가건 원본 파일 URL
    @Column(name = "photo_original_file_url")
    private String photoOriginalFileUrl;

    // 포토 추가건 컨텐츠 타입 (필터 : FILTER / 캐릭터 : CHARACTER  / 스티커 : STICKER) - 탭메뉴선택시 필수
    @Column(name = "photo_content_tab_menu_type")
    private String photoContentTabMenuType;

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

    public static ArPhotoContentsEntity ofUpdate(ArPhotoContentsEntity findEntity, ArPhotoContentsEntity newEntity, String contentsType) {
        ArPhotoContentsEntity updateEntity = new ArPhotoContentsEntity();
        updateEntity.setId(findEntity.getId());
        updateEntity.setArEventId(findEntity.getArEventId());
        updateEntity.setPhotoContentType(contentsType);
        updateEntity.setSort(PredicateUtils.isNull(newEntity.getSort()) ? findEntity.getSort() : newEntity.getSort());
        updateEntity.setPhotoContentChoiceType(PredicateUtils.isNull(newEntity.getPhotoContentChoiceType()) ? findEntity.getPhotoContentChoiceType() : newEntity.getPhotoContentChoiceType());
        updateEntity.setPhotoFileName(PredicateUtils.isNull(newEntity.getPhotoFileName()) ? findEntity.getPhotoFileName() : newEntity.getPhotoFileName());
        updateEntity.setPhotoThumbnailImgUrl(PredicateUtils.isNull(newEntity.getPhotoThumbnailImgUrl()) ? findEntity.getPhotoThumbnailImgUrl() : newEntity.getPhotoThumbnailImgUrl());
        updateEntity.setPhotoOriginalFileUrl(PredicateUtils.isNull(newEntity.getPhotoOriginalFileUrl()) ? findEntity.getPhotoOriginalFileUrl() : newEntity.getPhotoOriginalFileUrl());
        updateEntity.setPhotoContentTabMenuType(PredicateUtils.isNull(newEntity.getPhotoContentTabMenuType()) ? findEntity.getPhotoContentTabMenuType() : newEntity.getPhotoContentTabMenuType());
        updateEntity.setCreatedBy(findEntity.getCreatedBy());
        updateEntity.setCreatedDate(findEntity.getCreatedDate());
        updateEntity.setLastModifiedBy(updateEntity.getLastModifiedBy());
        return updateEntity;
    }
}
