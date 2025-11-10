package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArEventNftCouponInfoResDto implements Serializable {

    private static final long serialVersionUID = -341540513522790773L;

    private Long id;

    private Integer arEventId;

    private Integer stpId;

    private Integer arEventWinningId;

    private String nftCouponId;

    private Boolean isPayed;

    private String uploadExcelFileName;

    private Date createdDate;
    
    private String nftImgUrl;

    private String nftInactiveImgUrl;

    private String productName;

    private String stpPanCouponActiveUrl;

    private String stpPanCouponInActiveUrl;
}
