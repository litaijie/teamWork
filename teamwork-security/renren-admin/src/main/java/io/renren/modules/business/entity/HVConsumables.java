package io.renren.modules.business.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HVConsumables {
    @ExcelProperty(value = "医生",index = 0)
    private String shoufeiyonghuming;
    @ExcelProperty(value = "科室",index = 1)
    private String zhuyuankeshi;
    @ExcelProperty(value = "金额",index = 2)
    private String jine;
    @ExcelProperty(value = "数量",index = 3)
    private String shuliang;
}
