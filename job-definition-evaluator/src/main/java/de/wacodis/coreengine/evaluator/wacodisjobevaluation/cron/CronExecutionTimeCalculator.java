/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import java.time.ZonedDateTime;
import org.joda.time.DateTime;
import java.util.Optional;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helps calculating the point in time of previous/next execution according to a cron expression
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CronExecutionTimeCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CronExecutionTimeCalculator.class);

    private String cronExpression;
    private CronDefinition cronDefinition;

    public CronExecutionTimeCalculator(String cronExpression, CronDefinition cronDefinition) {
        this.cronExpression = cronExpression;
        this.cronDefinition = cronDefinition;

        LOGGER.debug(CronExecutionTimeCalculator.class.getSimpleName() + " instance created with Cron Expression: " + cronExpression + " and CronDefinition: " + cronDefinition.toString());
    }

    /**
     * uses UNIX-style cron definition
     * @param cronExpression 
     */
    public CronExecutionTimeCalculator(String cronExpression) {
        this(cronExpression, CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    }

    public CronExecutionTimeCalculator(String cronExpression, CronType cronType) {
        this(cronExpression, CronDefinitionBuilder.instanceDefinitionFor(cronType));
    }

    public CronExecutionTimeCalculator() {
        LOGGER.debug(CronExecutionTimeCalculator.class.getSimpleName() + " instance created without attributes set");
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public CronDefinition getCronDefinition() {
        return cronDefinition;
    }

    public void setCronDefinition(CronDefinition cronDefinition) {
        this.cronDefinition = cronDefinition;
    }

    public void setCronDefinition(CronType cronType) {
        this.cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(cronType);
    }

    /**
     * returns the point in time of the last scheduled execution before dateTime
     * @param dateTime
     * @return
     */
    public ZonedDateTime previousExecution(ZonedDateTime dateTime) {
        Cron cron = parseCronExpression(this.cronExpression, this.cronDefinition);
        ExecutionTime execTime = ExecutionTime.forCron(cron);
        Optional<ZonedDateTime> prevExecTime = execTime.lastExecution(dateTime);

        return prevExecTime.get();
    }

    /**
     * returns the point in time of the last scheduled execution before dateTime
     * @param dateTime
     * @return
     */
    public DateTime previousExecution(DateTime dateTime) {
        ZonedDateTime zonedDateTime = convertDateTimeToZonedDateTime(dateTime);
        ZonedDateTime prevExecTime = previousExecution(zonedDateTime);

        return convertZonedDateTimeToDateTime(prevExecTime);
    }

    /**
     * returns the point in time of the next scheduled execution after dateTime
     * @param dateTime
     * @return
     */
    public ZonedDateTime nextExecution(ZonedDateTime dateTime) {
        Cron cron = parseCronExpression(this.cronExpression, this.cronDefinition);
        ExecutionTime execTime = ExecutionTime.forCron(cron);
        Optional<ZonedDateTime> nextExecTime = execTime.nextExecution(dateTime);

        return nextExecTime.get();
    }

    /**
     * returns the point in time of the next scheduled execution after dateTime
     * @param dateTime
     * @return
     */
    public DateTime nextExecution(DateTime dateTime) {
        ZonedDateTime zonedDateTime = convertDateTimeToZonedDateTime(dateTime);
        ZonedDateTime nextExecTime = nextExecution(zonedDateTime);

        return convertZonedDateTimeToDateTime(nextExecTime);
    }

    private Cron parseCronExpression(String cronExpression, CronDefinition cronDefinition) {
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(cronExpression);

        return cron;
    }

    private DateTime convertZonedDateTimeToDateTime(ZonedDateTime zonedDateTime) {
        return new DateTime(
                zonedDateTime.toInstant().toEpochMilli(),
                DateTimeZone.forTimeZone(TimeZone.getTimeZone(zonedDateTime.getZone())));
    }

    private ZonedDateTime convertDateTimeToZonedDateTime(DateTime dateTime) {
        return dateTime.toGregorianCalendar().toZonedDateTime();
    }
}
