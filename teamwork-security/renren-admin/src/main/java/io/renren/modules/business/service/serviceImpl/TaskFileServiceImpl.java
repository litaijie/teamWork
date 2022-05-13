package io.renren.modules.business.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.business.dao.TaskFileMapper;
import io.renren.modules.business.entity.tws.TaskFile;
import io.renren.modules.business.service.TaskFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("taskFileService")
public class TaskFileServiceImpl extends ServiceImpl<TaskFileMapper, TaskFile> implements TaskFileService {

    @Transactional
    @Override
    public boolean saveTaskFile(List<Long> imageId, Long taskId, String fileType){
        if (imageId==null) return false;
        List<TaskFile> taskFiles = new ArrayList<>();
        if (!imageId.isEmpty()) {
            imageId.forEach(i -> {
                taskFiles.add(TaskFile.builder().fileId(i).taskId(taskId).fileType(fileType).build());
            });
            return this.saveBatch(taskFiles);
        }
        return false;
    }

}
