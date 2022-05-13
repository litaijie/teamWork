package io.renren.modules.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.business.DTO.TaskDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.entity.tws.TaskDayReport;
import io.renren.modules.sys.entity.SysDictEntity;

import java.util.List;

public interface TaskDayReportService extends IService<TaskDayReport> {

}
