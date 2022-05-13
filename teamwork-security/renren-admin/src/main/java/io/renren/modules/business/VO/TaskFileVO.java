package io.renren.modules.business.VO;

import lombok.Data;

import java.util.Date;

@Data
public class TaskFileVO {
    private Long taskFileId;
    private Long sysFileId;
    private Long taskId;
    private String fileName;
    private Long fileSize;
    private String fileFormat;
    private String originalName;
    private String filePath;
}
