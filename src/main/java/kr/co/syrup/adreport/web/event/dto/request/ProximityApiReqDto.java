package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProximityApiReqDto implements Serializable {

    private static final long serialVersionUID = -2768103800078559382L;

    @NotNull(message = "PID가 없습니다.")
    private String eventId;

    private String appType;

    @NotNull(message = "위도값이 없습니다.")
    private double lat;

    @NotNull(message = "경도값이 없습니다.")
    private double lon;

    @NotNull(message = "GF 그리드 조회 반경값이 없습니다.")
    private int radius;

    @NotNull(message = "가맹점 MID 조회 반경값이 없습니다.")
    private int mradius;

    private String mid;

    public static ProximityApiReqDto condition(String pid, String lat, String lon) {
        return new ProximityApiReqDto().builder()
                .eventId(pid)
                .appType("WEB")
                .lat(Double.parseDouble(lat))
                .lon(Double.parseDouble(lon))
                .radius(150)
                .mradius(0)
                .mid("")
                .build();

    }

    public static ProximityApiReqDto conditionByAbsoluteCoordinates(ProximityApiReqDto reqDto) {
        return new ProximityApiReqDto().builder()
                .eventId(reqDto.eventId)
                .appType("WEB")
                .lat(reqDto.getLat())
                .lon(reqDto.getLon())
                .radius(reqDto.getRadius())
                .mradius(reqDto.getMradius())
                .build();

    }
}
