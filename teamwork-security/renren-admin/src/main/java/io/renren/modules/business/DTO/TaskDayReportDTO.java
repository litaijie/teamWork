package io.renren.modules.business.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDayReportDTO {
    private Long repId;

    private Long taskId;

    private String repDate;

    private Long taskStatus;

    private String taskStatusDes;

    private String taskName;

    private String repComment;

    private Long taskLevel;

    private String taskLevelDes;

    private Long taskLeaderId;

    private String taskLeader;

    private int taskLateDay;

}
