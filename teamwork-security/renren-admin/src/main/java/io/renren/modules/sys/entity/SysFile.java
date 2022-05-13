package io.renren.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("SYS_FILE")
@KeySequence(value = "SYS_FILE_S", clazz = Long.class)
public class SysFile {
    @TableId
    private Long id;
    private String fileName;
    private Long fileSize;
    private String fileFormat;
    private String originalName;
    private String filePath;
}
