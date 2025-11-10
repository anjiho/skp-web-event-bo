package kr.co.syrup.adreport.web.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FutureSenseTokenDetailResDto {

    private String name;

    private String description;

    private String image;

    @JsonProperty(value = "external_url")
    private String externalUrl;

    @JsonProperty(value = "animation_url")
    private String animationUrl;

}
