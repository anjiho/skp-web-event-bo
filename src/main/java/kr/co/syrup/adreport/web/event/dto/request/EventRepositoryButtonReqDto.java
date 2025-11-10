package kr.co.syrup.adreport.web.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventRepositoryButtonReqDto implements Serializable {

    private static final long serialVersionUID = 7521897918939415062L;

    // index
    private Long arEventRepositoryButtonId;

    // 버튼 타입 (사용확인 : CONFIRM, URL 접속 : URL)
    private String repositoryButtonType;

    // 버튼 문구
    private String repositoryButtonText;

    // 버튼 이동 URL
    private String repositoryButtonTargetUrl;

    // sort order
    private Integer sort;
}
