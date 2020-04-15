package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.coreengine.scheduling.job.JobContext;
import de.wacodis.coreengine.scheduling.job.JobContextFactory;
import de.wacodis.coreengine.scheduling.job.JobDetailFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JobContextFactory.class, JobDetailFactory.class, QuartzSchedulingManager.class, SchedulerFactoryBean.class})
public class QuartzSchedulingManagerIT {

    private static final UUID ID_1 = UUID.randomUUID();
    private static final UUID ID_2 = UUID.randomUUID();
    private static final String UNIX_CRON_EXPRESSION = "0 0 1 * *";

    @Autowired
    private QuartzSchedulingManager manager;

    @Test
    void roundTrip() {
        WacodisJobDefinition jobDef_1 = new WacodisJobDefinition();
        jobDef_1.setId(ID_1);
        jobDef_1.setExecution(new WacodisJobDefinitionExecution().pattern(UNIX_CRON_EXPRESSION));

        WacodisJobDefinition jobDef_2 = new WacodisJobDefinition();
        jobDef_2.setId(ID_2);
        jobDef_2.setExecution(new WacodisJobDefinitionExecution().pattern(UNIX_CRON_EXPRESSION));

        manager.scheduleNewJob(jobDef_1);
        manager.scheduleNewJob(jobDef_2);

        Assertions.assertTrue(manager.existsJob(jobDef_1));
        Assertions.assertTrue(manager.existsJob(jobDef_2));

        JobContext context_1 = manager.getJob(jobDef_1);
        JobContext context_2 = manager.getJob(jobDef_2);
        Assertions.assertEquals(ID_1.toString(), context_1.getJobDetails().getKey().getName());
        Assertions.assertEquals(ID_2.toString(), context_2.getJobDetails().getKey().getName());

        Assertions.assertTrue(manager.deleteJob(jobDef_1));
        Assertions.assertFalse(manager.deleteJob(jobDef_1));
        Assertions.assertFalse(manager.existsJob(jobDef_1));
        Assertions.assertNull(manager.getJob(jobDef_1));
        Assertions.assertTrue(manager.existsJob(jobDef_2));

        manager.deleteAll();
        Assertions.assertFalse(manager.existsJob(jobDef_2));
    }

}
