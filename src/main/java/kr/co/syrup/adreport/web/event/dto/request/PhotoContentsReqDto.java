package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@NoArgsConstructor
@Data
public class PhotoContentsReqDto implements Serializable {

    private static final long serialVersionUID = -3533005539057224590L;

    //인덱스
    private Long id;

    // 포토 추가건 순서
    private Integer sort;

    // 포토 추가건 컨텐츠 메뉴 선택 타입 (라이브러리 :  LIBRARY / 직접등록 : DIRECT)
    private String photoContentChoiceType;

    // 포토 추가건 파일명 (라이브러리 선택시 필수)
    private String photoFileName;

    // 포토 추가건 썸네일 이미지 URL
    private String photoThumbnailImgUrl;

    // 포토 추가건 원본 파일 URL
    private String photoOriginalFileUrl;

    // 포토 추가건 컨텐츠 타입 (필터 : FILTER / 캐릭터 : CHARACTER  / 스티커 : STICKER) - 탭메뉴선택시 필수
    private String photoContentTabMenuType;
}
