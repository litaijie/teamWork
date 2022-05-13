package io.renren.modules.business.controller;

import io.renren.common.utils.R;
import io.renren.modules.business.service.DRGsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("DRG")
@RestController
@RequestMapping("/test/drgs")
public class DRGsController {

    @Autowired
    DRGsService drGsService;

    @ApiOperation("request")
    @RequestMapping(value = "/request22", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public R request(@RequestParam String date){
        DateTime parse = DateTime.parse(date);
        drGsService.request(parse);
        return R.ok();
    }
}
