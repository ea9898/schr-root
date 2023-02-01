package moscow.ptnl.app.sae.handler.config;

import moscow.ptnl.app.sae.handler.task.SchoolAttachmentEventProcessTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.invoke.MethodHandles;

/**
 * @author sorlov
 */
@Configuration
@EnableScheduling
public class SchedulerConfiguration implements SchedulingConfigurer {

    protected static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Autowired
    private SchoolAttachmentEventProcessTask schoolAttachmentEventProcessTask;

    private TaskScheduler buildTaskExecutor() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(10);
        executor.setThreadNamePrefix("scheduled-task-");
        executor.setDaemon(true);
        executor.setRemoveOnCancelPolicy(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(buildTaskExecutor());
        addEsuPublisherTask(taskRegistrar);
    }
    
    private void addEsuPublisherTask(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedDelayTask(new IntervalTask(schoolAttachmentEventProcessTask::runTask, 60000, 10000));
    }
}