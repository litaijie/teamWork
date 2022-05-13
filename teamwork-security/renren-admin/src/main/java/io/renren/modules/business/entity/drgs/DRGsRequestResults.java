package io.renren.modules.business.entity.drgs;

import io.renren.modules.business.entity.drgs.DRGsDatas;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DRGsRequestResults {
    //居民医保，月度每积分费用
    private BigDecimal juminValue;
    //职工医保，月度每积分费用
    private BigDecimal zhigongValue;
    //月度积分的时间
    private String expensesDate;

    private Integer PageNum;
    private Integer PageSize;
    private List<DRGsDatas> Result;
}
