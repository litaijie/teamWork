package io.renren.modules.business.service;

import io.renren.modules.business.DTO.QueryParam;

import javax.servlet.http.HttpServletResponse;

public interface OutDeptDataService {
    void outDeptData(HttpServletResponse response, QueryParam param);
}
