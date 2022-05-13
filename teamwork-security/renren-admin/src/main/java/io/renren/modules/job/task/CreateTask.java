package io.renren.modules.job.task;

import com.alibaba.fastjson.JSON;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.service.TaskService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component("createTask")
public class CreateTask implements ITask {
    @Autowired
    private TaskService taskService;

    @Override
    public void run(String params) {
        if (StringUtils.isNotEmpty(params)){
            Task task = JSON.parseObject(params,Task.class);
            DateTime dateTime= DateTime.now();
            String s = dateTime.toString("yyyy-MM");
            SimpleDateFormat format1=new SimpleDateFormat("-dd HH:mm:ss");
            SimpleDateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                task.setTaskEndDate(format2.parse(s+format1.format(task.getTaskEndDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            task.setCreateId(task.getTaskLeaderId());
            task.setLastUpdateId(task.getTaskLeaderId());
            task.setCreateTime(dateTime.toDate());
            task.setLastUpdateTime(dateTime.toDate());
            taskService.save(task);
        }
    }
}

