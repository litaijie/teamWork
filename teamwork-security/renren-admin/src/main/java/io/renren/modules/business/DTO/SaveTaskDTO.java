package io.renren.modules.business.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class SaveTaskDTO {
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

    private List<Long> imageId;

    private List<Long> delImageId;

    private String fileType;
}
