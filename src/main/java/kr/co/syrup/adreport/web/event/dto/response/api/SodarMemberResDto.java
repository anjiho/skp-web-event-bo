package kr.co.syrup.adreport.web.event.dto.response.api;

import lombok.Data;

import java.io.Serializable;

@Data
public class SodarMemberResDto implements Serializable {

    private static final long serialVersionUID = -2414702915038871505L;

    private String memberId;

    private String name;

    private String email;
}
