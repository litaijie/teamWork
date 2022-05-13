package io.renren.modules.business.entity.drgs;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("TWS_DRGS_EXPENSES")
@KeySequence(value = "TWS_DRGS_EXPENSES_S", clazz = Long.class)
public class DRGsDatas {
    @TableId
    private Long id;
    //科室
    @TableField("DEPTNAME")
    private String Deptname;
    //医生
    @TableField("DOCTOR_NAME")
    private String DoctorName;
    //医保类型
    @TableField("FUND_TYPE")
    private String FundType;
    //参保人姓名
    @TableField("PATIENT_NAME")
    private String PatientName;

    //病例点数
    @TableField("SCORE_VALUE")
    private BigDecimal ScoreValue;
    //DRGs费用
    @TableField("DRGS_VALUE")
    private BigDecimal drgsValue;
    //结算日期
    @TableField("BILL_DATE")
    private Date BillDate;
    //拨付日期
    @TableField("ALLOCATE_DATE")
    private Date AllocateDate;


    //积分查询结果的时间
    @TableField(exist = false)
    private String YearMonth;
    //月度每积分费用(元)
    @TableField(exist = false)
    private BigDecimal Integralrate;
    //医保类型，积分查询用
    @TableField(exist = false)
    private String FundName;
}
