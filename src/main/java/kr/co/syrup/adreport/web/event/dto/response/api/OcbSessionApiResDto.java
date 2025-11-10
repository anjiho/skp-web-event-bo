package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.Data;

import java.io.Serializable;

@Data
public class OcbSessionApiResDto implements Serializable {

    private static final long serialVersionUID = 4777069920858814173L;

    private String mbrId;

    private String mdn;

    private String userName;

    private String birthDate;

    private String sessionType;
}
