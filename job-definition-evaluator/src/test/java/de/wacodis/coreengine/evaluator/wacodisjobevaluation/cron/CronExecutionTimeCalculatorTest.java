/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation.cron;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.joda.time.DateTime;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class CronExecutionTimeCalculatorTest {

    public CronExecutionTimeCalculatorTest() {
    }

    /**
     * Test of getCronExpression method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testGetCronExpression() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator("0 0 1 * *", CronType.UNIX);
        assertEquals("0 0 1 * *", calc.getCronExpression());
    }

    /**
     * Test of setCronExpression method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testSetCronExpression() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator("0 0 1 * *", CronType.UNIX);
        calc.setCronExpression("0 0 * * *");

        assertEquals("0 0 * * *", calc.getCronExpression());
    }

    /**
     * Test of setCronDefinition method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testSetCronDefinition_CronDefinition() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator("0 0 1 * *", CronType.UNIX);
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        calc.setCronDefinition(cronDef);

        assertEquals(cronDef, calc.getCronDefinition());
    }

    /**
     * Test of setCronDefinition method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testSetCronDefinition_CronType() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator("0 0 1 * *", CronType.UNIX);
        calc.setCronDefinition(CronType.QUARTZ);

        assertEquals(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ).getFieldDefinitions().size(), calc.getCronDefinition().getFieldDefinitions().size());
    }

    /**
     * Test of previousExecution method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testPreviousExecution_ZonedDateTime() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator();
        calc.setCronDefinition(CronType.UNIX);
        calc.setCronExpression("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        ZonedDateTime zdt = ZonedDateTime.of(2018, 1, 15, 11, 45, 25, 25, ZoneId.of("Europe/Berlin"));

        assertEquals(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Berlin")), calc.previousExecution(zdt));

    }

    /**
     * Test of previousExecution method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testPreviousExecution_DateTime() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator();
        calc.setCronDefinition(CronType.UNIX);
        calc.setCronExpression("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        DateTime dateTime = DateTime.parse("2018-01-15T10:30:25Z");

        assertEquals(DateTime.parse("2018-01-01T00:00:00Z"), calc.previousExecution(dateTime));
    }

    /**
     * Test of nextExecution method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testNextExecution_ZonedDateTime() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator();
        calc.setCronDefinition(CronType.UNIX);
        calc.setCronExpression("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        ZonedDateTime zdt = ZonedDateTime.of(2018, 1, 15, 11, 45, 25, 25, ZoneId.of("Europe/Berlin"));

        assertEquals(ZonedDateTime.of(2018, 2, 1, 0, 0, 0, 0, ZoneId.of("Europe/Berlin")), calc.nextExecution(zdt));
    }

    /**
     * Test of nextExecution method, of class CronExecutionTimeCalculator.
     */
    @Test
    public void testNextExecution_DateTime() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator();
        calc.setCronDefinition(CronType.UNIX);
        calc.setCronExpression("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        DateTime dateTime = DateTime.parse("2018-01-15T10:30:25+02:00");

        assertEquals(DateTime.parse("2018-02-01T00:00:00+02:00"), calc.nextExecution(dateTime));
    }

    /**
     * Test of nextExecution method, of class CronExecutionTimeCalculator.
     */
    @Test
    @DisplayName("check equality DateTime and ZoneDateTime")
    public void testNextExecution_Conversion() {
        CronExecutionTimeCalculator calc = new CronExecutionTimeCalculator();
        calc.setCronDefinition(CronType.UNIX);
        calc.setCronExpression("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2018, 1, 15, 11, 45, 25, 0, ZoneId.of("UTC+2"));
        DateTime dateTime = DateTime.parse("2018-01-15T11:45:25+02:00");

        ZonedDateTime zonedNextExec = calc.nextExecution(zonedDateTime);
        DateTime nextExec = calc.nextExecution(dateTime);

        assertTrue((zonedNextExec.toEpochSecond() == (nextExec.getMillis() / 1000)));
    }
}
