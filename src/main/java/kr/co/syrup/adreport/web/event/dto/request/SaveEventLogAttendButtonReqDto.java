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
public class SaveEventLogAttendButtonReqDto implements Serializable {

    private static final long serialVersionUID = -1022921159453368419L;

    private String eventId;

    private String successYn;
}
