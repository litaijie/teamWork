package io.renren.modules.job.task;

import io.renren.modules.business.service.DRGsService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("crawlDRGsTask")
public class CrawlDRGsTask implements ITask {
    @Autowired
    private DRGsService drGsService;

    @Override
    public void run(String params) {
        drGsService.request(DateTime.now());
    }

}
