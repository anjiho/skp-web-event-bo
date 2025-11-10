package kr.co.syrup.adreport.web.event.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class TestReqDto {

    private TestReqDto1 test1;

    private String test2;

    private List<TestReqDto2> test3;

    private TestReqDto3 test4;


}
