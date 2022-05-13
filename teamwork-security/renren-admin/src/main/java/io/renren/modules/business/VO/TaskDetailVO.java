package io.renren.modules.business.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.renren.modules.business.entity.tws.TaskFile;
import io.renren.modules.sys.entity.SysFile;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class TaskDetailVO {
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

    private List<TaskFileVO> taskFiles;

    private List<Long> imageId;

    private List<Long> delImageId;
}
