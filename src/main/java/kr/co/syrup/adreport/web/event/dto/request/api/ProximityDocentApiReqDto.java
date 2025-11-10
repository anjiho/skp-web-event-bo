package kr.co.syrup.adreport.web.event.dto.request.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProximityDocentApiReqDto implements Serializable {

    private static final long serialVersionUID = 911116027377939775L;

    @NotNull
    private String eventId;

    @Min(value = 1)
    private double lat;

    @Min(value = 1)
    private double lon;

    private int radius;

    private int mradius;
}
