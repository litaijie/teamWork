package io.renren.modules.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.business.entity.tws.TaskFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskFileService extends IService<TaskFile> {

    @Transactional
    boolean saveTaskFile(List<Long> imageId, Long taskId, String fileType);
}
