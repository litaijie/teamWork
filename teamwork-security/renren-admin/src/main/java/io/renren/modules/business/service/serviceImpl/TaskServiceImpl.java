package io.renren.modules.business.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.DateUtils;
import io.renren.modules.business.DTO.TaskDTO;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskDayReportVO;
import io.renren.modules.business.VO.TaskDetailVO;
import io.renren.modules.business.VO.TaskFileVO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.dao.TaskDayReportMapper;
import io.renren.modules.business.dao.TaskFileMapper;
import io.renren.modules.business.dao.TaskMapper;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.service.TaskService;
import io.renren.modules.business.util.Base64FileUtils;
import io.renren.modules.business.util.DownloadUtil;
import io.renren.modules.business.util.ExportWordUtil;
import io.renren.modules.sys.dao.SysDictDao;
import io.renren.modules.sys.entity.SysDictEntity;
import io.renren.modules.sys.entity.SysFile;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysFileService;
import io.renren.modules.sys.shiro.ShiroUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("taskServiceImpl")
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private SysDictDao sysDictDao;

    @Autowired
    private TaskDayReportMapper taskDayReportMapper;

    @Autowired
    private SysFileService sysFileService;

    @Override
    public Page<TaskVO> listTask(TaskDTO task){
        Page<TaskVO> taskPage=new Page<>(task.getPageNum(),task.getPageSize()==0?20:task.getPageSize());
        QueryWrapper<TaskDTO> queryWrapper = new QueryWrapper<TaskDTO>();
        queryWrapper.like(StringUtils.isNotEmpty(task.getTaskLeader()),"task_Leader",task.getTaskLeader())
                .like(StringUtils.isNotEmpty(task.getTaskName()),"task_Name",task.getTaskName())
                .like(StringUtils.isNotEmpty(task.getTaskReason()),"task_reason",task.getTaskReason())
                .like(StringUtils.isNotEmpty(task.getTaskMethod()),"task_method",task.getTaskMethod())
                .like(StringUtils.isNotEmpty(task.getTaskRemarks()),"task_remarks",task.getTaskRemarks())
                .eq(StringUtils.isNotEmpty(task.getTaskStatus()),"task_Status",task.getTaskStatus())
                .eq(StringUtils.isNotEmpty(task.getTaskLevel()),"task_Level",task.getTaskLevel())
                .eq(StringUtils.isNotEmpty(task.getTaskCat1()),"task_Cat1",task.getTaskCat1())
                .eq(StringUtils.isNotEmpty(task.getTaskCat2()),"task_Cat2",task.getTaskCat2())
                .le(task.getTaskCreateTimeEnd()!=null,"task_Create_Time",task.getTaskCreateTimeEnd())
                .ge(task.getTaskCreateTimeStart()!=null,"task_Create_Time",task.getTaskCreateTimeStart())
                .le(task.getTaskEndDateEnd()!=null,"task_End_Date",task.getTaskEndDateStart())
                .ge(task.getTaskEndDateStart()!=null,"task_End_Date",task.getTaskEndDateEnd())
                .orderBy(true,false,"CREATE_TIME");
        return taskMapper.listTask(taskPage,queryWrapper);
    }

    /**
     * 获取软件分类
     * @return
     */
    @Override
    public List<SysDictEntity> getCat2(){
        return sysDictDao.getCat2();
    }

    /**
     * 日报查询
     * @param task
     * @return
     */
    @Override
    public List<TaskDayReportDTO> listTaskDayReport(TaskDTO task){
        QueryWrapper<TaskDTO> queryWrapper = new QueryWrapper<TaskDTO>();
        String repDate=task.getTaskCreateTimeStart()==null?DateTime.now().toString("yyyy-MM-dd"):DateUtils.format(task.getTaskCreateTimeStart(),DateUtils.DATE_PATTERN);

        SysUserEntity user =(SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        queryWrapper.like(StringUtils.isNotEmpty(task.getTaskLeader()),"task_Leader_id",task.getTaskLeader())
                .eq("task_leader_id",user.getUserId())
        .eq("rep_date", repDate);
        return taskDayReportMapper.listTaskDayReport(queryWrapper);
    }

    /**
     * 当前工作进度查询
     * @param task
     * @return
     */
    @Override
    public List<TaskDayReportDTO> listTaskDaySchedule(TaskDTO task){
        QueryWrapper<TaskDTO> queryWrapper = new QueryWrapper<TaskDTO>();
        String repDate=task.getTaskCreateTimeStart()==null?DateTime.now().toString("yyyy-MM-dd"):DateUtils.format(task.getTaskCreateTimeStart(),DateUtils.DATE_PATTERN);
        StringBuffer dateSql =new StringBuffer("to_char(create_time,'yyyy-MM-dd')<='");
        dateSql.append(repDate).append("'");

        SysUserEntity user =(SysUserEntity) SecurityUtils.getSubject().getPrincipal();
        queryWrapper.like(StringUtils.isNotEmpty(task.getTaskLeader()),"task_Leader_id",task.getTaskLeader())
                .eq("task_leader_id",user.getUserId())
        .apply(dateSql.toString());
        return taskMapper.listTaskDaySchedule(queryWrapper);
    }

    @Override
    public TaskDetailVO getTaskDetail(Long taskId) throws InvocationTargetException, IllegalAccessException {
        Task task = taskMapper.selectById(taskId);
        TaskDetailVO taskDetailVO =new TaskDetailVO();
        BeanUtils.copyProperties(taskDetailVO,task);
        taskDetailVO.setTaskFiles(taskMapper.selectSysFilesByTaskId(taskId));
        return taskDetailVO;
    }

    @Override
    public List<TaskFileVO> getTaskFile(Long taskId){
        //获取任务关联的文件
        List<TaskFileVO> sysFiles = taskMapper.selectSysFilesByTaskId(taskId);
        return sysFiles;
    }

    @Override
    public void getTaskDocument(List<Long> taskIds, HttpServletResponse response) throws Exception {
        if (taskIds!=null&&!taskIds.isEmpty()){
            List<TaskVO> tasks = taskMapper.selectTaskByTaskIds(taskIds);
            List<File> files=new ArrayList<>();
            if (tasks!=null&&!tasks.isEmpty()){
                String filePath = sysFileService.createFilePath();
                String zipName = UUID.randomUUID().toString()+"问题汇总.rar";
                String zipPassword = "4237809";
                Map<String, Object> dataMap = new HashMap<>();
                SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
                tasks.forEach(t->{
                    StringBuffer fileName=new StringBuffer("[");
                    fileName.append(t.getTaskLeader()).append("]")
                            .append(t.getTaskId())
                            .append("-")
                            .append(t.getTaskName())
                            .append(".doc");
                    dataMap.put("taskLeader",StringUtils.isNotEmpty(t.getTaskLeader())?t.getTaskLeader():"");
                    dataMap.put("createDate", t.getTaskCreateTime()!=null?format.format(t.getTaskCreateTime()):"");
                    dataMap.put("taskName", StringUtils.isNotEmpty(t.getTaskName())?t.getTaskName():"");
                    dataMap.put("taskReason", StringUtils.isNotEmpty(t.getTaskReason())?t.getTaskReason():"");
                    List<TaskFileVO> sysFiles = taskMapper.selectSysFilesByTaskId(t.getTaskId());
                    List<String> image =new ArrayList<>();
                    if (sysFiles!=null&&!sysFiles.isEmpty()){
                        sysFiles.forEach(f->{
                            image.add(Base64FileUtils.fileToBase64(f.getFilePath()));
                        });
                    }
                    dataMap.put("imageList", image);
                    dataMap.put("endDate", t.getTaskEndDate()!=null?format.format(t.getTaskEndDate()):"");
                    try {
                        new ExportWordUtil().exportDocFile( fileName.toString(),filePath, "questionTemplate.ftl", dataMap);
                        files.add(new File(filePath+fileName.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                //打包文件
                DownloadUtil.zipByFiles(response,files,filePath,zipName,zipPassword);
                DownloadUtil.delTempFile(filePath+zipName);
            }
        }
    }
}
