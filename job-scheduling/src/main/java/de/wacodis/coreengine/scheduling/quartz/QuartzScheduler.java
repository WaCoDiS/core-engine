/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import java.io.IOException;
import java.util.Properties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class QuartzScheduler implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzScheduler.class);

    private Scheduler scheduler;

    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();

            scheduler = schedulerFactory.getScheduler();
            scheduler.start();

        } catch (SchedulerException e) {
            LOGGER.warn(e.getMessage());
            LOGGER.trace("Error during scheduling", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        scheduler.shutdown(true);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

}
