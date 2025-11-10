package kr.co.syrup.adreport.controller.rest.local.test;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@ApiModel(value = "board_info mapping model")
@EqualsAndHashCode(callSuper = false)
public class BoardInfoDto extends BaseWrapDto implements Serializable {

    private static final long serialVersionUID = -9176871534947767759L;

    private Long idx;

    private String title;

    private String contents;
}
