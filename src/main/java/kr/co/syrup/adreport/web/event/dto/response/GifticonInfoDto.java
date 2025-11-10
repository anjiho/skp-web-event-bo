package kr.co.syrup.adreport.web.event.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GifticonInfoDto implements Serializable {

    private static final long serialVersionUID = 3800766116109224546L;
    //기프티콘 쿠폰 번호
    @JsonProperty(value = "CPNO")
    private String cpno;

    //기프티콘 쿠폰 순번
    @JsonProperty(value = "CPNO_SEQ")
    private String cpnoSeq;

    //쿠폰 교환 시작일자 (YYYYMMDD)
    @JsonProperty(value = "EXCH_FR_DY")
    private String exchFrDy;

    //쿠폰 교환 종료 일자 (YYYYMMDD)
    @JsonProperty(value = "EXCH_TO_DY")
    private String exchToDy;
}
