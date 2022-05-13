package io.renren.modules.business.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryParam {
    private String yearMonth;
    private String year;
    private String startDate;
    private String endDate;
}
