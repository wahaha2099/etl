package cc.changic.platform.etl.base.service;

import cc.changic.platform.etl.base.dao.JobMapper;
import cc.changic.platform.etl.base.model.ExecutableJob;
import cc.changic.platform.etl.base.model.db.Job;
import cc.changic.platform.etl.base.schedule.ETLScheduler;
import cc.changic.platform.etl.base.util.LogFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Panda.Z on 2015/1/31.
 */
@Component
public class JobService {

    private Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired(required = false)
    private JobMapper jobMapper;
    @Autowired(required = false)
    private ETLScheduler etlScheduler;

    public boolean doIncrementalFileSuccess(Job job, Short nextInterval, long offset) {
        job.setOptionDesc("SUCCESS");
        job.setModifyTime(new Date());
        job.setLastRecordOffset(offset);
        if (null == job.getNextTime()) {
            job.setNextTime(new Date());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(job.getNextTime());
        calendar.add(Calendar.MINUTE, nextInterval);
        job.setNextTime(calendar.getTime());
        jobMapper.updateByPrimaryKey(job);
        return etlScheduler.addAndScheduleJob(job);
    }

    public boolean doFileSuccess(Job job, String fileName, String desc, Short nextInterval) {
        job.setStatus(ExecutableJob.SUCCESS);
        job.setOptionDesc(desc);
        job.setModifyTime(new Date());
        Date logTime = null;
        try {
            logTime = LogFileUtil.getLogFileTimestamp(fileName);
        } catch (Exception e) {
            doError(job, nextInterval, "日志文件时间格式错误");
            logger.error("时间格式转换错误:job_id={},{}", job.getId(), e.getMessage(), e);
            // return 确保不会影响到已存在的数据
            return false;
        }
        job.setLastRecordTime(logTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(logTime);
        calendar.add(Calendar.MINUTE, nextInterval);
        job.setNextTime(calendar.getTime());
        jobMapper.updateByPrimaryKey(job);
        return etlScheduler.addAndScheduleJob(job);
    }

    public void doError(Job job, Short nextInterval, String message) {
        job.setStatus(ExecutableJob.FAILED);
        job.setOptionDesc(message);
        job.setModifyTime(new Date());
        if (null == job.getNextTime()) {
            job.setNextTime(new Date());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(job.getNextTime());
        calendar.add(Calendar.MINUTE, nextInterval);
        job.setNextTime(calendar.getTime());
        jobMapper.updateByPrimaryKey(job);
        etlScheduler.addAndScheduleJob(job);
    }


}
