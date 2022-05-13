package io.renren.modules.business.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskDayReportVO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.entity.tws.TaskDayReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskDayReportMapper extends BaseMapper<TaskDayReport> {

    @Select("select * from (\n" +
            "            select t.rep_id,t.task_leader_id,b.task_name,\n" +
            "                   to_char(t.rep_date,'yyyy-MM-dd') rep_date,\n" +
            "                   a.username task_leader,\n" +
            "                   t.task_id,\n" +
            "                   d.value task_status_des,\n" +
            "                   t.task_status,\n" +
            "                   t.task_level,\n" +
            "                   c.value task_level_des,\n" +
            "                   t.rep_comment,\n" +
            "                   t.task_late_day from TWS_DAY_REPORT t,sys_user a,tws_task b,sys_dict c,sys_dict d where t.task_leader_id=a.user_id and t.task_id=b.task_id and t.task_level=c.code and c.type='taskLevel' and t.task_status=d.code and d.type='taskStatus' order by t.task_status) ${ew.customSqlSegment}")
    List<TaskDayReportDTO> listTaskDayReport(@Param(Constants.WRAPPER) Wrapper wrapper);


}
