package kr.co.syrup.adreport.stamp.event.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class WebEventStampTypeListReqDto implements Serializable {

    private static final long serialVersionUID = 4324802495672608664L;

    // 현재 페이지 번호
    @ApiModelProperty(value = "page")
    private int page;

    // 한 페이지에 보여질 row 수
    @ApiModelProperty(value = "size")
    private int size;

    // 검색 타입
    @ApiModelProperty(value = "검색타입")
    private String searchType;

    // 검색어
    @ApiModelProperty(value = "검색어")
    private String searchWord;

    @ApiModelProperty(value = "검색모드 : ALL - 전체 조회 / FILTER : 매핑되어있는 이벤트만 필터링 조회")
    private String searchMode;
}
