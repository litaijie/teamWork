package io.renren.modules.business.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.business.DTO.TaskDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskDayReportVO;
import io.renren.modules.business.VO.TaskFileVO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.sys.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Mapper
public interface TaskMapper  extends BaseMapper<Task> {

    @Select("select TASK_ID,task_Lead_ID,task_Name,task_create_Time,task_Leader,task_Status,task_Level,task_Cat1,task_Cat2,task_End_Date,CREATE_TIME from(SELECT a.TASK_ID,a.TASK_LEADER_ID task_Lead_ID,a.TASK_NAME task_Name,a.CREATE_TIME,a.CREATE_TIME task_create_Time,b.USERNAME task_Leader,status.value task_Status,levelDesc.value task_Level,cat1.value task_Cat1,cat2.value task_Cat2,a.TASK_END_DATE task_End_Date,a.task_reason,a.task_method,a.task_remarks FROM TWS_TASK a,SYS_USER b,(select * from SYS_DICT c where DEL_FLAG=0 and type='taskCat1') cat1,(select * from SYS_DICT c where DEL_FLAG=0 and type='taskCat2') cat2,(select * from SYS_DICT c where DEL_FLAG=0 and type='taskLevel') levelDesc,(select * from SYS_DICT c where DEL_FLAG=0 and type='taskStatus') status WHERE a.TASK_LEADER_ID=b.USER_ID and a.TASK_CAT1=cat1.CODE and a.TASK_CAT2=cat2.CODE and a.TASK_STATUS=status.code and a.TASK_LEVEL=levelDesc.code) ${ew.customSqlSegment}")
    Page<TaskVO> listTask(Page page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 每日工作进度
     * @param wrapper
     * @return
     */
    @Select("select * from (select a.task_id,a.task_leader_id,d.username task_leader,a.task_name,c.value task_Level_des,a.task_Level,b.value task_status_des,a.task_status,a.task_end_date,to_date(to_char(decode(a.task_status,0,sysdate,a.last_update_time),'yyyy-MM-dd'),'yyyy-MM-dd')-to_date(to_char(a.task_end_date,'yyyy-MM-dd'),'yyyy-MM-dd') task_late_day,a.last_update_time,a.create_time from tws_task a,sys_dict b,sys_dict c,sys_user d where not exists(select 1 from TWS_DAY_REPORT p where p.task_id=a.task_id and p.task_status=2 and a.task_status=2) and a.task_leader_id=d.user_id and a.task_level=c.code and c.type='taskLevel' and a.task_status=b.code and b.type='taskStatus' order by a.task_status) ${ew.customSqlSegment}")
    List<TaskDayReportDTO> listTaskDaySchedule(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select b.ID task_file_id,c.ID sys_file_id,a.TASK_ID,c.FILE_FORMAT,c.FILE_NAME,c.FILE_PATH,c.FILE_SIZE,c.ORIGINAL_NAME from TWS_TASK a,TWS_TASK_FILE b,SYS_FILE c where a.TASK_ID=b.TASK_ID and c.ID=b.FILE_ID and a.TASK_ID=${taskId}")
    List<TaskFileVO> selectSysFilesByTaskId(@Param("taskId") Long taskId);

//    @Select("select o.TASK_ID,o.TASK_NAME,o.task_CREATE_TIME,o.TASK_END_DATE,o.TASK_REASON,p.USERNAME task_leader from tws_task o,SYS_USER p where p.USER_ID=o.TASK_LEADER_ID and task_id=${taskId}")
    List<TaskVO> selectTaskByTaskIds(@Param("taskIds") Collection<? extends Serializable> taskIds);
}
