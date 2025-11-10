package kr.co.syrup.adreport.web.event.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class CategoryDto {

    private String categoryCode;

    private String categoryName;

    private String categoryType;

    private int categoryDepth;

    private List<Map<String, Object>> childCategoryList;
}
