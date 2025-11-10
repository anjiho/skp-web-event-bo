package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class SmsSendResDto implements Serializable {

    private static final long serialVersionUID = 3322105392181696074L;

    private String code;

    private List<Map<String, String>> successList;

    private List<Map<String, String>> failList;
}
