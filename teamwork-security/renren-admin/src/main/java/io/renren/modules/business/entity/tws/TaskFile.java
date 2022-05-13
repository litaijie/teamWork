package io.renren.modules.business.entity.tws;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@TableName("tws_task_file")
@KeySequence(value = "tws_task_file_s", clazz = Long.class)
@Builder
public class TaskFile {
    @TableId
    private Long id;
    private Long taskId;
    private Long fileId;
    private String fileType;
}
