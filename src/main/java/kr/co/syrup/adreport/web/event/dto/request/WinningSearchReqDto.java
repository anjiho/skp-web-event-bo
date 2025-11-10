package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WinningSearchReqDto implements Serializable {

    private static final long serialVersionUID = 6495508542199606621L;

    private String eventId;

    private String phoneNumber;

    private String attendCode;

    private String stampEventIds;
}
