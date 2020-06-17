/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.Assert;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SourceTypeMatcherTest {

    @org.junit.Test
    public void testMatch() {
        SourceTypeDataEnvelopeMatcher matcher = new SourceTypeDataEnvelopeMatcher();

        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        List<AbstractSubsetDefinition> inputs = new ArrayList<>();
        inputs.add(input);
        AbstractDataEnvelope envelope = new AbstractDataEnvelope();
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setInputs(inputs);
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.DWDDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.GDIDEDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.SENSORWEBSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.SENSORWEBDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.WACODISPRODUCTSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
        Assert.assertTrue(matcher.match(envelope, job, input));

        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.STATICSUBSETDEFINITION);
        envelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
        Assert.assertFalse(matcher.match(envelope, job, input));
    }

}
