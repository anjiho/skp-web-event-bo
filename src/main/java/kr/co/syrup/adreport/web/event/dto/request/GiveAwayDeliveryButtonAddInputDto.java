package kr.co.syrup.adreport.web.event.dto.request;

import lombok.*;

import java.io.Serializable;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GiveAwayDeliveryButtonAddInputDto implements Serializable {

    private static final long serialVersionUID = -8281789391089796973L;

    private Integer arEventWinningButtonAddId;

    private String fieldValue;

}
