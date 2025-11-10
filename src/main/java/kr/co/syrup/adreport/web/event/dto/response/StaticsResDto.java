package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaticsResDto implements Serializable {

    private static final long serialVersionUID = 5965896535143153724L;

    private ApiResultObjectDto connectionStatics;

    private ApiResultObjectDto performanceStatics;

    private ApiResultObjectDto hourlyStatics;

    private ApiResultObjectDto performanceSubscriptionStatics;

    private ApiResultObjectDto nftRepositoryStatics;

    private ApiResultObjectDto surveyGenderConnectionStatics;

    private ApiResultObjectDto surveyAgeConnectionStatics;

    private ApiResultObjectDto photoPrintResultStatics;

    private ApiResultObjectDto photoBoxStatics;

    private ApiResultObjectDto stampTrAccStatics;

    private Boolean isStampEvent;

}
