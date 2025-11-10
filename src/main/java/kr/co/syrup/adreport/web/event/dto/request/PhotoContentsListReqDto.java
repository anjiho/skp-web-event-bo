package kr.co.syrup.adreport.web.event.dto.request;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoContentsListReqDto implements Serializable {

    private static final long serialVersionUID = -1893299954151550413L;

    //프레임 컨텐츠 정보
    private List<PhotoContentsReqDto> frameContentsInfo;

    //탭 컨텐츠 정보
    private List<PhotoContentsReqDto> tabContentsInfo;

    //필터 컨텐츠 정보
    private List<PhotoContentsReqDto> filterContentsInfo;

    //캐릭터 컨텐츠 정보
    private List<PhotoContentsReqDto> characterContentsInfo;

    //스티커 컨텐츠 정보
    private List<PhotoContentsReqDto> stickerContentsInfo;

}
