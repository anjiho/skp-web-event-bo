package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhotoBoxStaticsMapperVO implements Serializable {

    private static final long serialVersionUID = -3429360955298874368L;

    //AR포토함PV 누적/기준일
    private String photoboxCnt;

    //출력 요청 누적/기준일
    private String printRequestCnt;

    //출력 성공 누적/기준일
    private String printSuccessCnt;

    //출력실패 누적/기준일
    private String printFailCnt;
}
