package kr.co.syrup.adreport.web.event.mybatis.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhotoPrintResultMapperVO implements Serializable {

    private static final long serialVersionUID = -5997892994723043225L;

    //촬영결과페에지 PV
    private String mainPhotoCnt;

    //저장 클릭  PV
    private String saveClickCnt;

    //공유 클릭 PV
    private String shareClickCnt;

    //해시태그 복사 클릭 PV
    private String hashtagClickCnt;

    //사진 출력 클릭 PV
    private String printClickCnt;
}
