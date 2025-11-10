package kr.co.syrup.adreport.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PageResponse extends BaseDomain {
    private Integer pageIdx;
    private Integer listCnt;
    private Integer resultCnt;
    private List<?> list;

    public PageResponse(){
        super();
    }
    public PageResponse(Integer pageIdx, Integer listCnt, List<?> list) {
        this.pageIdx = pageIdx;
        this.listCnt = listCnt;
        this.list = list;
        this.resultCnt = list.size();
    }

}
