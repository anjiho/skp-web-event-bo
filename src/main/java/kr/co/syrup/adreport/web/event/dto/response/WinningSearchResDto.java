package kr.co.syrup.adreport.web.event.dto.response;

import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WinningSearchResDto implements Serializable {

    private static final long serialVersionUID = -4144227194681995503L;

    private List<UserWinningInfoResDto> userWinningInfoResDtoList;

    // 당첨 비밀번호 사용 여부(Y, N)
    private String winningPasswordYn;

    private String nftWinningIncludeYn = StringDefine.N.name();

    private String nftCouponWinningIncludeYn = StringDefine.N.name();
}
