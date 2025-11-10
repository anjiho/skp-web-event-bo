package kr.co.syrup.adreport.web.event.dto.response;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AttendCodeValidateResDto implements Serializable {

    private static final long serialVersionUID = -5994088429729238366L;

    private boolean duplicateYn;

    private long attendCodeCount;

    private long duplicateCodeCount;

    private long totalCount;

}
