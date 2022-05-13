package io.renren.modules.business.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.DateUtils;
import io.renren.modules.business.DTO.TaskDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.dao.TaskDayReportMapper;
import io.renren.modules.business.dao.TaskMapper;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.entity.tws.TaskDayReport;
import io.renren.modules.business.service.TaskDayReportService;
import io.renren.modules.business.service.TaskService;
import io.renren.modules.sys.dao.SysDictDao;
import io.renren.modules.sys.entity.SysDictEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("taskDayReportServiceImpl")
public class TaskDayReportServiceImpl extends ServiceImpl<TaskDayReportMapper, TaskDayReport> implements TaskDayReportService {

}
