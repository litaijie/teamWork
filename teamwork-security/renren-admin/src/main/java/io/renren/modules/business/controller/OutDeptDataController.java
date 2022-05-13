package io.renren.modules.business.controller;

import io.renren.common.utils.R;
import io.renren.modules.business.DTO.QueryParam;
import io.renren.modules.business.service.OutDeptDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/business/outDeptDat")
public class OutDeptDataController {
    @Autowired
    OutDeptDataService outDeptDataService;

    @RequestMapping(value = "/downloadOutDeptData", method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    public R downloadOutDeptData(HttpServletResponse response){
        QueryParam param = QueryParam.builder().build();
        outDeptDataService.outDeptData(response,param);
        return R.ok();
    }
}
