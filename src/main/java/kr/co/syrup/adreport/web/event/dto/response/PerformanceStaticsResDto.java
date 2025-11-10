package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedList;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PerformanceStaticsResDto implements Serializable {

    private static final long serialVersionUID = 8241341418171436688L;
    //제목
    LinkedList<String> tableTitleInfo;

    //캐치기준
    LinkedList<String> tableValueInfo;

    //당첨정보 입력 기준
    LinkedList<String> tableValueInfo2;
}
