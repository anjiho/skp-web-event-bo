package kr.co.syrup.adreport.framework.config.jwt;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class TokenDto implements Serializable {

    private static final long serialVersionUID = 5503531244562459655L;

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private Long accessTokenExpiresIn;
}
