package kr.co.syrup.adreport.web.event.dto.response;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class NftTokenIdValidateResDto implements Serializable {

    private static final long serialVersionUID = -4363005065210348579L;

    private boolean duplicateYn;

    private long totalCount;

        private int addNftTokenCount;

    private int duplicateNftTokenCount;

        private String uploadFileName;

        private Long uploadFileSeqNum;
}
