package io.renren.modules.business.DTO;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.Date;

@Data
public class TaskDTO {
    private Long taskId;

    private String taskName;

    private String taskCat1;

    private String taskCat2;

    private String taskLevel;

    private Date taskCreateTimeStart;

    private Date taskCreateTimeEnd;

    private Date taskEndDateStart;

    private Date taskEndDateEnd;

    private String taskLeader;

    private String taskStatus;

    private String taskReason;

    private String taskMethod;

    private String taskRemarks;

    private Long pageNum;

    private Long pageSize;
}
