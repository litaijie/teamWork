package io.renren.modules.business.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.business.DTO.TaskDayReportDTO;
import io.renren.modules.business.VO.TaskVO;
import io.renren.modules.business.entity.tws.Task;
import io.renren.modules.business.entity.tws.TaskFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskFileMapper extends BaseMapper<TaskFile> {

}
