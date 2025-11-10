package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SmsCallReqDto implements Serializable {

    private String mdn;

    private String contents;

    public static SmsCallReqDto condition(String mdn, String contents) {
        return new SmsCallReqDto().builder()
                .mdn(mdn).contents(contents)
                .build();
    }
}
