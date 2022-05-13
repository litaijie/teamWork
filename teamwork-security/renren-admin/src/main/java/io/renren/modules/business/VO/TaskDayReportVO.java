package io.renren.modules.business.VO;

import io.renren.modules.business.DTO.TaskCountDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TaskDayReportVO {
    List<TaskDayReportDTO> taskList;

    /**是否日报查询：true:日报查询;false:当前进度查询**/
    private boolean dayReport;

    private List<TaskCountDTO> taskCount;
}
