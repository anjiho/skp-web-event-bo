package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventWinningTextReqDto implements Serializable {

    private static final long serialVersionUID = -1033689337767134685L;

    // index
    private Long arEventWinningTextId;

    // 컨텐츠 문구
    private String winningText;

    // sort order
    private Integer sort;
}
