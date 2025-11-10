package kr.co.syrup.adreport.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by ho on 2017. 3. 7..
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultListCriteria extends BaseDomain{
    private static final long serialVersionUID = 3133499405540351181L;
    private Long userNo;
    private String searchType;
    private String gender;
    private Integer distance;
    private Integer fromAge;
    private Integer toAge;
    private String areaCode;
    private String detailAreaCode;
    private Integer pageIdx;
    private Integer listCnt;
}
