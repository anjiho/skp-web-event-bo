package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.stamp.event.model.StampEventPanModel;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonAddEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GiveAwayResDto implements Serializable {

    private static final long serialVersionUID = -852210103989194611L;

    private String winningPasswordYn;

    private String winningType;

    private String subscriptionRaffleDay;

    private String subscriptionRaffleTime;

    private ArEventWinningButtonEntity buttonInfo;

    private ArEventEntity arEventInfo;

    private ArEventWinningEntity arEventWinningInfo;

    private List<ArEventWinningButtonAddEntity> arEventWinningButtonAddList;

    private StampEventPanModel stampEventPanInfo;

}
