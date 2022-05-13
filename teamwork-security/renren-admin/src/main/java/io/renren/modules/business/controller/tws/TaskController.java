package io.renren.modules.business.controller.tws;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.modules.business.DTO.SaveTaskDTO;
import io.renren.modules.business.DTO.TaskCountDTO;
import io.renren.modules.business.DTO.TaskDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskDayReportVO;
import io.renren.modules.business.VO.TaskDetailVO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.entity.tws.TaskDayReport;
import io.renren.modules.business.entity.tws.TaskFile;
import io.renren.modules.business.service.TaskDayReportService;
import io.renren.modules.business.service.TaskFileService;
import io.renren.modules.business.service.TaskService;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.shiro.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
@Api("任务单管理")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDayReportService taskDayReportService;

    @Autowired
    private TaskFileService taskFileService;

    @SysLog("保存任务单")
    @PostMapping("/save")
    public R saveTask(@RequestBody SaveTaskDTO task) throws InvocationTargetException, IllegalAccessException {
        Task t =new Task();
        BeanUtils.copyProperties(t,task);
        boolean save = taskService.saveOrUpdate(getTask(t));
        if (save){
            taskFileService.saveTaskFile(task.getImageId(), t.getTaskId(), task.getFileType());//保存文件
            if (task.getDelImageId()!=null) taskFileService.removeByIds(task.getDelImageId());//删除文件
        }
        return save?R.ok():R.error("保存任务内容失败！");
    }

    @SysLog("删除任务单")
    @PostMapping("/delete")
    public R deleteTask(@RequestBody List<Long> taskIds){
        boolean save = taskService.removeByIds(taskIds);
        return save?R.ok():R.error("删除任务内容失败！");
    }

    @PostMapping("/listTask")
    public R listTask(@RequestBody TaskDTO taskDTO){
        Page<TaskVO> taskVOPage = taskService.listTask(taskDTO);

        return R.ok("data",taskVOPage);
    }


    @ApiOperation("每日报表查询")
    @PostMapping("/listTaskDayReport")
    public R listTaskDayReport(@RequestBody TaskDTO taskDTO){
        TaskDayReportVO data =new TaskDayReportVO();
        List<TaskDayReportDTO> taskDayReportDTOS = taskService.listTaskDayReport(taskDTO);//日报查询
        if (taskDayReportDTOS==null||taskDayReportDTOS.isEmpty()){
            taskDayReportDTOS=taskService.listTaskDaySchedule(taskDTO);
            data.setTaskList(taskDayReportDTOS);//当前进度查询
            data.setDayReport(false);
        }else {
            data.setTaskList(taskDayReportDTOS);
            data.setDayReport(true);
        }
        if(taskDayReportDTOS!=null&&!taskDayReportDTOS.isEmpty()){
            Map<Long, List<TaskDayReportDTO>> collect = taskDayReportDTOS.stream().collect(Collectors.groupingBy(TaskDayReportDTO::getTaskStatus));
            TaskCountDTO unfinish = TaskCountDTO.builder().color("#ed3f14").count(collect.get(0L)==null?0:collect.get(0L).size()).icon("md-close-circle").title("未完成").build();
            TaskCountDTO check = TaskCountDTO.builder().color("#ff9900").count(collect.get(1L)==null?0:collect.get(1L).size()).icon("md-code-working").title("待审核").build();
            TaskCountDTO finish = TaskCountDTO.builder().color("#19be6b").count(collect.get(2L)==null?0:collect.get(2L).size()).icon("md-checkmark-circle").title("已完成").build();
            List<TaskCountDTO> countDTOS =new ArrayList<>();
            countDTOS.add(unfinish);
            countDTOS.add(check);
            countDTOS.add(finish);
            data.setTaskCount(countDTOS);
        }
        return R.ok("data",data);
    }

    /**
     * 保存任务日报
     * @param taskDayReport
     * @return
     */
    @PostMapping("/saveTaskReport")
    public R saveTaskReport(@RequestBody List<TaskDayReport> taskDayReport){
        taskDayReportService.saveBatch(taskDayReport);
        return R.ok();
    }

    @GetMapping("/readTask")
    public R readTask(@RequestParam Long taskId) throws InvocationTargetException, IllegalAccessException {
//        Task detail = taskService.getById(taskId);
//        TaskDetailVO taskDetailVO =new TaskDetailVO();
//        BeanUtils.copyProperties(taskDetailVO,detail);
        return R.ok("data",taskService.getTaskDetail(taskId));
    }

    @GetMapping("/getCat2")
    public R getCat2(){
        return R.ok("data",taskService.getCat2());
    }

    @PostMapping("downloadTask")
    public void downloadTask(HttpServletResponse response,@RequestBody List<Long> taskIds){
        try {
            taskService.getTaskDocument(taskIds,response);
        } catch (Exception e) {
            e.printStackTrace();
//            return R.error(e.getMessage());
        }
//        return R.ok();
    }

    private Task getTask(Task task){
        if (task==null){
            return null;
        }
        Date date = new Date();
        SysUserEntity currentUser = (SysUserEntity)ShiroUtils.getSubject().getPrincipal();
        if (task.getTaskId()==null){
            task.setCreateId(currentUser.getUserId());
            task.setCreateTime(date);
            task.setTaskLeaderId(task.getTaskLeaderId()!=null?task.getTaskLeaderId():currentUser.getUserId());//任务负责人
        }
        task.setLastUpdateId(currentUser.getUserId());
        task.setLastUpdateTime(date);
        return task;
    }
}
