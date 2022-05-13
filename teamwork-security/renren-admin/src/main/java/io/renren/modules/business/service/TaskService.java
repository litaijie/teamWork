package io.renren.modules.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.business.DTO.TaskDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskDayReportVO;
import io.renren.modules.business.VO.TaskDetailVO;
import io.renren.modules.business.VO.TaskFileVO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.sys.entity.SysDictEntity;
import io.renren.modules.sys.entity.SysFile;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface TaskService extends IService<Task> {
    Page<TaskVO> listTask(TaskDTO task);

    List<SysDictEntity> getCat2();

    List<TaskDayReportDTO> listTaskDayReport(TaskDTO task);

    List<TaskDayReportDTO> listTaskDaySchedule(TaskDTO task);

    TaskDetailVO getTaskDetail(Long taskId) throws InvocationTargetException, IllegalAccessException;

    List<TaskFileVO> getTaskFile(Long taskId);

    void getTaskDocument(List<Long> taskIds, HttpServletResponse response) throws Exception;
}
