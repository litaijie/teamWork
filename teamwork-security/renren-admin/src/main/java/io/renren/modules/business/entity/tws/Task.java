package io.renren.modules.business.entity.tws;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tws_task")
@KeySequence(value = "tws_task_s", clazz = Long.class)
public class Task extends BaseEntity{

    @TableId
    private Long taskId;

    private String taskName;

    private Long taskCat1;

    private Long taskCat2;

    private Long taskLevel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date taskEndDate;

    private String taskReason;

    private String taskMethod;

    private String taskRemarks;

    private Long taskLeaderId;

    private Long taskStatus;

}
