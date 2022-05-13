package io.renren.modules.business.service.serviceImpl;

import io.renren.modules.business.DTO.QueryParam;
import io.renren.modules.business.dao.ReportsMapper;
import io.renren.modules.business.entity.HVConsumables;
import io.renren.modules.business.service.ReportsService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service("reportsServiceImpl")
public class ReportsServiceImpl implements ReportsService {
    @Autowired
    private ReportsMapper reportsMapper;

    public void hvcStatistics(DateTime date){
        if (date==null){
            date = DateTime.now();
        }
        QueryParam param = QueryParam.builder()
                .year(date.toString("yyyy"))
                .yearMonth("yyyy-MM")
                .build();
        List<HVConsumables> hvConsumables = null;
        List<HVConsumables> drugs = null;
        List<HVConsumables> hVConsumablesByDept = null;
        List<HVConsumables> drugsByDept = null;
        List<HVConsumables> hVConsumables10 = null;
        List<HVConsumables> drugs10 = null;
        try {
            Future<String> result1 = this.getHvConsumables(hvConsumables, param);
            Future<String> result2 = this.getHvConsumables(drugs, param);
            Future<String> result3 = this.getHvConsumables(hVConsumablesByDept, param);
            Future<String> result4 = this.getHvConsumables(drugsByDept, param);
            Future<String> result5 = this.getHvConsumables(hVConsumables10, param);
            Future<String> result6 = this.getHvConsumables(drugs10, param);
            boolean back = false;
            while (!back){
                Thread.sleep(10000);
                if (result1.isDone() && result2.isDone() && result3.isDone() && result4.isDone() && result5.isDone() && result6.isDone()) {
                    back = true;
                    break;
                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void excelExport(){

    }

    @Async
    public Future<String> getHvConsumables(List<HVConsumables> hvConsumables,QueryParam param) throws InterruptedException {
        String msg = "500";
        hvConsumables = reportsMapper.hVConsumables(param);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getDrugs(List<HVConsumables> drugs,QueryParam param) throws InterruptedException {
        String msg = "500";
        drugs = reportsMapper.drugs(param);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getHVConsumablesByDept(List<HVConsumables> hVConsumablesByDept,QueryParam param) throws InterruptedException {
        String msg = "500";
        hVConsumablesByDept = reportsMapper.hVConsumablesByDept(param);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getDrugsByDept(List<HVConsumables> drugsByDept,QueryParam param) throws InterruptedException {
        String msg = "500";
        drugsByDept = reportsMapper.drugsByDept(param);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getHVConsumables10(List<HVConsumables> hVConsumables10,QueryParam param) throws InterruptedException {
        String msg = "500";
        hVConsumables10 = reportsMapper.hVConsumables10(param);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getDrugs10(List<HVConsumables> drugs10,QueryParam param) throws InterruptedException {
        String msg = "500";
        drugs10 = reportsMapper.drugs10(param);
        return new AsyncResult<String>(msg);
    }
}
