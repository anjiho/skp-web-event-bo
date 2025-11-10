package kr.co.syrup.adreport.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.syrup.adreport.framework.utils.JsonUtils;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDomain implements Serializable {
    private static final long serialVersionUID = 6022304962855785456L;

    @JsonIgnore
    private Long createdBy;
    @JsonIgnore
    private Timestamp createdDate;
    @JsonIgnore
    private Long modifiedBy;
    @JsonIgnore
    private Timestamp modifiedDate;

/*    public static <T extends Enum<T>> T enumValueOf(Class<T> enumType, String value) {
        T returnValue = null;

        for (final T element : enumType.getEnumConstants()) {
            if (element.toString().equals(value)) {
                returnValue = element;
                break;
            }
        }

        return returnValue;
    }
*/

    @Override
    public String toString() {
        try {
            return JsonUtils.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }


}
