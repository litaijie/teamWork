package io.renren.modules.business.VO;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.Date;

@Data
public class TaskVO {
    private Long taskId;

    private String taskName;

    private String taskReason;

    private String taskCat1;

    private String taskCat2;

    private String taskLevel;

    private Date taskCreateTime;

    private Date taskEndDate;

    private String taskLeader;

    private String taskStatus;
}
