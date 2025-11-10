package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoPrintCountResDto implements Serializable {

    private static final long serialVersionUID = 3171100725655941294L;

    // 현재 설정되어있는 무료 출력 가능 횟수
    private long settingCount;

    // 서버에 기록되어있는 출력 횟수
    private long printCount;

    // 무료출력 가능 여부 / true : 가능 / false : 불가능
    private boolean isFreePrintable = false;

}
