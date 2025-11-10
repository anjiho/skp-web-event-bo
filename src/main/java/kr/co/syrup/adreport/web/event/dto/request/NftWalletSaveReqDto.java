package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class NftWalletSaveReqDto implements Serializable {

    private static final long serialVersionUID = 4734414851271194681L;

    @NotNull
    @NotEmpty
    private String nftWalletId;

    @NotNull
    @NotEmpty
    private String eventId;

    @NotNull
    @NotEmpty
    private String userPhoneNumber;

    @NotNull
    @NotEmpty
    private String walletAddress;
}
