package io.renren.modules.business.entity.drgs;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("TWS_DRGS_INTEGRAL")
@KeySequence(value = "TWS_DRGS_INTEGRAL_S", clazz = Long.class)
public class DRGsIntegral {
    @TableId
    private Long id;
    //居民医保，月度每积分费用
    @TableField("JUMIN_VALUE")
    private BigDecimal juminValue;
    //职工医保，月度每积分费用
    @TableField("ZHIGONG_VALUE")
    private BigDecimal zhigongValue;
    //月度积分的时间
    @TableField("EXPENSES_DATE")
    private String expensesDate;
}
