package io.renren.modules.business.entity.tws;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tws_day_report")
@KeySequence(value = "tws_day_report_s", clazz = Long.class)
public class TaskDayReport extends BaseEntity{

    @TableId
    private Long repId;

    private Date repDate;

    private Long taskId;

    private String repComment;

    private Long taskLeaderId;

    private Long taskStatus;

    private Long taskLevel;

    private int taskLateDay;
}
